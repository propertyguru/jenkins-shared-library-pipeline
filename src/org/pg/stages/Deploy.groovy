package org.pg.stages

import org.pg.common.slack.Message
import org.pg.common.slack.Slack

class Deploy extends Base {
    String stage
    String description
    Boolean skip = false

    Deploy(environment) {
        super(environment)
        // set skip variable to true
        if (!(this.environment in this.context.ENVIRONMENT.tokenize(','))) {
            skip = true
        }
        this.stage = "deploy - ${this.environment}"
        this.description = "Deploying to ${this.environment}"
    }

    def body() {
        Slack.send(new Message("step", "starting deployment of service."))
    }

}
