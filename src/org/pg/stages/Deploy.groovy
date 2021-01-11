package org.pg.stages

import org.pg.common.slack.Message
import org.pg.common.slack.Slack

class Deploy extends Base {

    Deploy(environment) {
        super(environment)
        // set skip variable to true if this stage needs to be skipped.
        if (!(this.environment in this.context.ENVIRONMENT.tokenize(','))) {
            this.skip = true
        }
        this.stage = "deploy - ${this.environment}"
        this.description = "Deploying to ${this.environment}"
    }

    @Override
    def body() {
        this.step("starting deployment of service.", {
//            echo "I am good here!"
        })
    }

}
