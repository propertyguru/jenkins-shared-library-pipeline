package org.pg.common.slack

@Singleton
class Slack {
    static private def context
    static private String channels
    static private String timestamp
    static private def colors = [
            "running": "#fca326",
            "success": "good",
            "failure": "danger"
    ]

    static private def setup(context) {
        this.context = context
        channels = this.context.SLACK_ID
        timestamp = this.context.TIMESTAMP
    }

    // sendMessage works with channels and users.
    // for users, simply use @username.
    static def sendMessage(String message, String status="success") {
        context.slackSend(
                channel: channels,
                message: message,
                color: colors[status],
                timestamp: timestamp,
                tokenCredentialId: 'slack-bot-token'
        )
    }
}
