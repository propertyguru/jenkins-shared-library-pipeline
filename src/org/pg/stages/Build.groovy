package org.pg.stages

class Build extends Base {
    String stage
    String slackMessage

    Build(environment) {
        super(environment)
        this.stage = "build"
        this.slackMessage = "Building the code"
    }

    def body() {
//        def deployPath = Blueprint.deployPath()
//        Log.info("Changing directory ${deployPath}")
//        this.context.dir(deployPath) {
//            this.context.stage('build') {
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
