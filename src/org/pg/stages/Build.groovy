package org.pg.stages

import org.pg.common.Blueprint
import org.pg.common.Git

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
            this.context.sh "ls -la"
        })
//        def deployPath = Blueprint.deployPath()
//        Log.info("Changing directory ${deployPath}")
//        this.context.dir(deployPath) {
//            this.context.stage('build') {
        this.step("Stashing dirs", {})
        this.step("Running pre-build steps from pgbuild.", {})
        this.step("Running build steps from pgbuild.", {})


//                stashDir("infra", "${Blueprint.appConfig()}/*.*")
//                stash("pgbuild", Blueprint.pgbuild())
//                PGbuild.instance.executeSteps("pre-build")
//                PGbuild.instance.executeSteps("build")
//            }

//            this.context.stage('unit tests') {
//                Log.info('unit tests')
//                PGbuild.instance.executeSteps('unit_tests')
//            }
//        }
    }

}
