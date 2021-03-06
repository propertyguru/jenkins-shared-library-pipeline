package org.stages

import org.common.BuildArgs
import org.common.Git
import org.common.Log
import org.common.Salt
import org.common.StepExecutor

class Sentry extends Base  {

    private String environment

    Sentry(String environment) {
        super()
        this.environment = environment
        this.stage = "Sentry - ${this.environment}"
    }

    @Override
    def body() {
        String cmd = "sentry.deploy ${BuildArgs.appname()} ${Git.getCommitID()}"
        Log.info(cmd)
//        (new Salt()).saltCallWithOutput(cmd)
    }

    @Override
    Boolean skip() {
        if (this.environment == "integration" &&
                this.environment in StepExecutor.env('ENVIRONMENT').tokenize(',')) {
            return false
        }
        return true
    }
}
