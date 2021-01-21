package org.stages

import org.common.BuildArgs
import org.common.Git
import org.common.Salt

class Sentry extends Base  {

    private String environment

    Sentry(String environment) {
        super()
        this.environment = environment
        this.stage = "Sentry - ${this.environment}"
        this.description = "Sentry - ${this.environment}"
    }

    @Override
    def body() {
        String cmd = "sentry.deploy ${BuildArgs.appname()} ${Git.getCommitID()}"
        (new Salt()).saltCallWithOutput(cmd)
    }

    @Override
    Boolean skip() {
        if (this.environment == "integration" &&
                this.environment in this._context.ENVIRONMENT.tokenize(',')) {
            return false
        }
        return true
    }
}
