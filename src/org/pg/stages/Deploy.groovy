package org.pg.stages


import org.pg.common.Git
import org.pg.common.JobDescription
import org.pg.common.Log

class Deploy extends Base {

    Deploy(environment) {
        super(environment)
        this.stage = "deploy - ${this.environment}"
        this.description = "Deploying to ${this.environment}"
    }

    @Override
    def body() {
        this.step("starting deployment of service.", {
//            echo "I am good here!"
            // set job description
            JobDescription.update(this.environment, Git.commitID)
            Log.info(JobDescription.getDescription())
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
