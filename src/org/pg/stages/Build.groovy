package org.pg.stages

import org.pg.common.Blueprint
import org.pg.common.BuildArgs
import org.pg.common.Docker
import org.pg.common.Git
import org.pg.common.Log
import org.pg.common.Output
import org.pg.common.PGbuild
import org.pg.common.Salt

class Build extends Base {
    String stage
    String description

    Build(environment) {
        super(environment)
        this.stage = "checkout & build"
        this.description = "Building the code"
    }

    def body() {
        this.step("checking out code from github", {
            Git.checkout()
        })

        // we have few services sharing the repository.
        // we store deployPath in blueprints to get the subpath in the repo.
        def deployPath = Blueprint.deployPath()
        Log.info("Moving to subdirectory ${deployPath}")
        this.context.dir(deployPath) {
            // starting a new step instead of a state for unit tests etc.
            this.step("stashing files", {
                (new Output()).stashDir("infra", "${Blueprint.appConfig()}/*.*")
                (new Output()).stash("pgbuild", Blueprint.pgbuild())
            })

            this.step("Running pre-build from pgbuild file", {
                Log.debug("Running pre-build steps from pgbuild.")
                PGbuild.executeSteps("pre-build")
            })

            this.step("Running build steps from pgbuild", {
                Log.debug("Running build steps from pgbuild.")
                PGbuild.executeSteps("build")
            })

            this.step("Running unit-tests from pgbuild", {
                Log.debug('Running unit-tests steps from pgbuild.')
                PGbuild.executeSteps('unit_tests')
            })

            if (this.context.fileExists(Blueprint.dockerfile())) {

                def docker = new Docker()
                docker.setup()

                if (this.context.fileExists("sonar-project.properties")) {
                    this.step("sonarqube", {
                        try {
                            String sonarPath = this.context.sh(script: 'pwd', returnStdout: true).trim()
                            sonarPath = "${sonarPath}/out/app"
                            docker.build(this.environment, "sonarqube", "out")
                            this.context.sh("docker run --rm -e SONAR_HOST_URL='http://sonarqube.guruestate.com:9000' " +
                                    "-e SONAR_PROJECT_BASE_DIR='/app' -e sonar.scm.provider=git -v ${sonarPath}:/app " +
                                    "sonarsource/sonar-scanner-cli:4.4")
                        } catch (Exception e) {
                            this.context.sh("rm -rf out/")
                            Log.error("Failure building dockerimage with sonarqube target")
                            this.context.error(e.toString())
                        }
                    })
                }

                this.step("Building docker image", {
                    // TODO: Do not build if it's not "integration" environment
                    // TODO: Cycle staging and production tags on Continuous Delivery and not rebuild containers
                    //		 to avoid newer versions than integration in staging and production environments
                    docker.build(this.environment)
                    if (!BuildArgs.isPRJob()) {
                        if (!Blueprint.skipDeployment()) {
                            docker.push()
                        }
                    }
                })

                // check if static content needs to be uploaded.
                // TODO: this can be moved to deploy stage so that we upload content and deploy in parallel
                if (!Blueprint.staticContent().isEmpty()) {
                    this.step("uploading static content", {
                        def static_content_branches = [:]
                        for (String e in this.context.ENVIRONMENT.tokenize(',')) {
                            String env = e
                            static_content_branches[env] = {
                                docker.build(env, "static-content", "${env}_static",
                                        "--build-arg BUILD_ENV=${env}")
                                Log.debug("Uploading Static content to S3 to ${env}")
                                (new Salt()).saltCallWithOutput("shipit.static_content ${Blueprint.component()} " +
                                        "${Blueprint.subcomponent()} ${env} ${env}_static/app/")
                                this.context.sh("rm -rf ./${env}_static")
                            }
                        }
                        this.context.parallel static_content_branches
                    })
                }
            }

        }
    }

}
