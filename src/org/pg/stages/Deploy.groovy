package org.pg.stages

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
        })
    }

    @Override
    Boolean skip() {
        // set skip variable to true if this stage needs to be skipped.
        if (this.environment in this.context.ENVIRONMENT.tokenize(',')) {
            return false
        }
        return true
    }
}
