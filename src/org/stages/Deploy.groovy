package org.stages

import org.common.Blueprint
import org.common.BuildArgs
import org.common.Log

import org.common.StepExecutor

class Deploy extends Base {

    private String environment

    Deploy(String environment) {
        super()
        this.environment = environment
    }

    @Override
    def body() {
        this.step("Promoting application", {
//            (new Salt()).sync()
//            (new Salt()).saltCallWithOutput("shipit.promote app_name=pg_${Blueprint.component()}_${Blueprint.subcomponent()}")
        })

        this.step("Deploying service", {
            Kubernetes.promote(this.environment)
            Kubernetes.deploy()
        })

    }

    @Override
    Boolean skip() {
        // set skip variable to true if this stage needs to be skipped.
        if (this.environment in StepExecutor.env('ENVIRONMENT').tokenize(',')) {
            return false
        }
        return true
    }
}

@Singleton
class Kubernetes {
    static void promote(String environment) {
        StepExecutor.unstash("infra")
        String filename = "./${Blueprint.appConfig()}/${environment}.env"
        if (StepExecutor.fileExists(filename)) {
            String cmd = "shipit.deploy app_name='pg_${Blueprint.component()}_${Blueprint.subcomponent()}' config_file=${filename}"
            Log.info(cmd)
//            (new Salt()).saltCallWithOutput(cmd)
        } else {
            Log.info("${filename} is missing from infra folder.")
        }
    }

    static void deploy() {
        Log.info("Inside deploy function")
        String cmd = "state.sls shipit.deploy pillar=\"{'app_name':'pg_${Blueprint.component()}_${Blueprint.subcomponent()}'}\" --retcode-passthrough"
        Log.info(cmd)
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