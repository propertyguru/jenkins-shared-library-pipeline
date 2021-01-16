package org.pg.stages

import org.pg.common.BuildArgs
import org.pg.common.Git
import org.pg.common.JobDescription
import org.pg.common.Log

class Deploy extends Base {

    Deploy(environment) {
        super(environment)
        this.stage = "Deploy - ${this.environment}"
        this.description = "Deploy - ${this.environment}"
    }

    @Override
    def body() {
        this.step("Deploying", {
//            echo "I am good here!"
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
