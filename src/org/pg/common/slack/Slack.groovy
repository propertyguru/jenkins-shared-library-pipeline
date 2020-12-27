package org.pg.common.slack

import org.pg.common.Context

@Singleton
class Slack {
    static private def context
    static private def channelId
    static private def timestamp
    static private def block = [
            [
                    "type": "header",
                    "text": [
                            "type": "plain_text",
                            "text": "ads-product",
                            "emoji": true
                    ]
            ],
            [
                    "type": "context",
                    "elements": [
                            [
                                    "type": "mrkdwn",
                                    "text": "Job initiated: Building *master* branch and deploying to *integration*"
                            ]
                    ]
            ]
    ]

    static private def colors = [
            "running": ":waiting:",
            "success": ":white_check_mark:",
            "failed": ":x:"
    ]

    static def setup() {
        this.context = Context.get()
        def slackID = this.context.slackUserIdFromEmail email: 'prince@propertyguru.com.sg', tokenCredentialId: 'slack-bot-token'
        def slackResponse = sendMessage(slackID, "running", "")
        channelId = slackResponse.getChannelId()
        timestamp = slackResponse.getTs()
    }

    // sendMessage works with channels and users.
    // for users, simply use @username.
    static def sendMessage(String channels, String status, String message, Integer index=null) {
        if (channels=="") {
            channels = channelId
        }
        if (message != "") {
            if (index != null) {
                block.remove(index)
            } else {
                index = block.size()
            }
            block.add([
                    "type": "context",
                    "elements": [
                            [
                                    "type": "mrkdwn",
                                    "text": colors[status] + message
                            ]
                    ]
            ])
        }
        this.context.slackSend(
                channel: channels,
                timestamp: timestamp,
                blocks: block,
                tokenCredentialId: 'slack-bot-token'
        )
        return index
    }

}