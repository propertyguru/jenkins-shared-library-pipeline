package org.pg.stages

import org.pg.common.slack.Message
import org.pg.common.slack.Slack

class Build extends Base {
    String stage
    String description

    Build(environment) {
        super(environment)
        this.stage = "build"
        this.description = "Building the code"
    }

    def body() {
//        def deployPath = Blueprint.deployPath()
//        Log.info("Changing directory ${deployPath}")
//        this.context.dir(deployPath) {
//            this.context.stage('build') {
        Slack.send(new Message("step", "Stashing dirs"))
//                stashDir("infra", "${Blueprint.appConfig()}/*.*")
//                stash("pgbuild", Blueprint.pgbuild())
        Slack.send(new Message("step", "Running pre-build steps from pgbuild."))
//                PGbuild.instance.executeSteps("pre-build")
        Slack.send(new Message("step", "Running build steps from pgbuild."))
//                PGbuild.instance.executeSteps("build")
//            }

//            this.context.stage('unit tests') {
//                Log.info('unit tests')
//                PGbuild.instance.executeSteps('unit_tests')
//            }
//        }
    }

}
