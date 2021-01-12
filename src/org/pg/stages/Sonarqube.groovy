package org.pg.stages

import org.pg.common.Blueprint
import org.pg.common.Docker
import org.pg.common.Log

class Sonarqube extends Base {

    Sonarqube(Object environment) {
        super(environment)
        this.stage = "Sonarqube"
        this.description = "Running code analysis"
    }

    @Override
    def body() {
        def docker = new Docker()
        docker.setup()

        this.context.dir(Blueprint.deployPath()) {
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
                    } finally {
                        this.context.sh("rm -rf ./out")
                    }
                })
            }
        }
    }
}
