package org.pg.stages

import org.pg.common.Blueprint
import org.pg.common.Log
import org.pg.common.slack.Slack

class Build extends Base {
    def stage

    Build(environment) {
        super(environment)
        this.stage = "build"
    }

    def body() {
        Slack.sendMessage("Building code")
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
