package org.pg.stages

import org.pg.common.slack.Slack

class Deploy extends Base {
    def stage

    Deploy(environment) {
        super(environment)
        this.stage = "deploy - ${this.environment}"
    }

    def body() {
        Slack.sendMessage("Deploying to ${this.environment}")
    }

}
