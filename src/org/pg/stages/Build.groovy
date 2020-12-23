package org.pg.stages

import org.pg.common.Log

class Build extends Base {
    def stage

    Build(context, environment) {
        super(context, environment)
        this.stage = "build"
    }

    def body() {
        def deployPath = Blueprint.deployPath()
        Log.info("Changing directory ${deployPath}")
        this.context.dir(deployPath) {
            this.context.stage('build') {
//                stashDir("infra", "${Blueprint.appConfig()}/*.*")
//                stash("pgbuild", Blueprint.pgbuild())
//                PGbuild.instance.executeSteps("pre-build")
//                PGbuild.instance.executeSteps("build")
            }

            this.context.stage('unit tests') {
                Log.info('unit tests')
//                PGbuild.instance.executeSteps('unit_tests')
            }
        }
    }

}
