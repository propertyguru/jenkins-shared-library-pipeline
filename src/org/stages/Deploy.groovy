package org.stages

import org.common.Blueprint
import org.common.BuildArgs
import org.common.Git
import org.common.Log
import org.common.Output
import org.common.Salt

class Deploy extends Base {

    Deploy(environment) {
        super(environment)
        this.stage = "Deploy - ${this.environment}"
        this.description = "Deploy - ${this.environment}"
    }

    @Override
    def body() {
        this.step("Promoting application", {
//            (new Salt()).sync()
//            (new Salt()).saltCallWithOutput("shipit.promote app_name=pg_${Blueprint.component()}_${Blueprint.subcomponent()}")
        })

        this.step("Deploying", {
            (new Output()).unstash("infra")
            String filename = "./${Blueprint.appConfig()}/${this.environment}.env"
            if (this._context.fileExists(filename)) {
//                (new Salt()).saltCallWithOutput("shipit.deploy app_name='pg_${Blueprint.component()}_${Blueprint.subcomponent()}' config_file=${filename}")
//                (new Salt()).saltCallWithOutput("state.sls shipit.deploy pillar=\"{'app_name':'pg_${Blueprint.component()}_${Blueprint.subcomponent()}'}\" --retcode-passthrough")
//                if (this.environment == "integration") {
//                    (new Salt()).saltCallWithOutput("sentry.deploy ${BuildArgs.appname()} ${Git.getCommitID()}")
//                }
            } else {
                Log.error("${filename} is missing from infra folder.")
                this._context.error("${filename} is missing from infra folder.")
            }
        })

        this.step("Configuring Kong API gateway", {
            (new Output()).unstash("pgbuild")
//            (new Salt()).saltCallWithOutput("kong.apply ${Blueprint.component()} ${Blueprint.subcomponent()} ${Blueprint.pgbuild()}")
        })

    }

    @Override
    Boolean skip() {
        // set skip variable to true if this stage needs to be skipped.
        if (this.environment in this._context.ENVIRONMENT.tokenize(',')) {
            return false
        }
        return true
    }
}
