package org.pg.common.slack

import org.pg.common.Context
import org.pg.common.Log


@Singleton
class Slack {
    static private def context
    static private String userID
    static private ArrayList messageHistory
    static private String channels
    static private String timestamp

    static private def colors = [
            "running": "#fca326",
            "success": "good",
            "failure": "danger"
    ]

    static def setup() {
        this.context = Context.get()
        channels = this.context.SLACK_ID
        timestamp = this.context.TIMESTAMP
        messageHistory = []
    }

    // sendMessage works with channels and users.
    // for users, simply use @username.
    static def sendMessage(String message, String status="success") {
        if (messageHistory.size() > 0) {
            Log.info("Current timestamp is ${timestamp}")
            def response = messageHistory.getAt(0)
            Log.info("Current timestamp is ${timestamp}")
            timestamp = response.getTs()
            Log.info("Timestamp changed to ${timestamp}")
        }
        def slackResponse = this.context.slackSend(
                channel: channels,
                message: message,
                color: colors[status],
                timestamp: timestamp,
                tokenCredentialId: 'slack-bot-token'
        )
        messageHistory.add(0, slackResponse)
    }

}

