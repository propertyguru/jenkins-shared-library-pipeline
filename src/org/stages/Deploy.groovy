package org.stages

import org.common.Blueprint
import org.common.BuildArgs
import org.common.Context
import org.common.Git
import org.common.Log
import org.common.Output
import org.common.Salt
import org.springframework.security.access.method.P

class Deploy extends Base {

    private String environment

    Deploy(String environment) {
        super()
        this.environment = environment
        this.stage = "Deploy - ${this.environment}"
        this.description = "Deploy - ${this.environment}"
    }

    @Override
    def body() {
        this.step("Promoting application", {
//            (new Salt()).sync()
//            (new Salt()).saltCallWithOutput("shipit.promote app_name=pg_${Blueprint.component()}_${Blueprint.subcomponent()}")
        })

        this.step("Deploying service", {
            if (BuildArgs.name().startsWith("guruland")){
                AutoScalingGroups.deploy()
            } else {
                Kubernetes.promote(this.environment)
                Kubernetes.deploy()
            }
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

@Singleton
class Kubernetes {
    static void promote(String environment) {
        def _context = Context.get()
//        Log.info(_context.getClass() as String)
        (new Output()).unstash("infra")
        String filename = "./${Blueprint.appConfig()}/${environment}.env"
        if (_context.fileExists(filename)) {
//            (new Salt()).saltCallWithOutput("shipit.deploy app_name='pg_${Blueprint.component()}_${Blueprint.subcomponent()}' config_file=${filename}")
            Log.info("inside fileexists.")
        } else {
            Log.info("${filename} is missing from infra folder.")
        }
    }

    static void deploy() {
        String cmd = "state.sls shipit.deploy pillar=\"{'app_name':'pg_${Blueprint.component()}_${Blueprint.subcomponent()}'}\" --retcode-passthrough"
//        (new Salt()).saltCallWithOutput(cmd)
    }

}

@Singleton
class AutoScalingGroups {

    static void promote() {
        // we don't have promotion step in this type of deployment!
    }

    static void deploy() {
        String cmd = "state.sls shipit.guruland pillar=\"{'app_name':'pg_${Blueprint.component()}_${Blueprint.subcomponent()}'}\" --retcode-passthrough"
//        (new Salt()).saltCallWithOutput(cmd)
    }

}