package org.pg

import org.pg.common.slack.Slack
import org.pg.stages.Checkout
import org.pg.stages.Build
import org.pg.stages.Deploy

class Pipeline {

    Pipeline() {}

    def execute() {
        Slack.sendMessage()
        new Checkout("integration").execute()
        new Build("integration").execute()

        new Deploy("integration").execute()
        new Deploy("staging").execute()
        new Deploy("production").execute()
    }

}
