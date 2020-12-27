package org.pg.stages

class Deploy extends Base {
    String stage
    String slackMessage
    Boolean skip = false

    Deploy(environment) {
        super(environment)
        if (!this.environment in this.context.ENVIRONMENT.tokenize(',')) {
            skip = true
        }
        this.stage = "deploy - ${this.environment}"
        this.slackMessage = "Deploying to ${this.environment}"
    }

    def body() {
    }

}
