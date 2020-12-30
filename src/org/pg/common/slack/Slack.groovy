package org.pg.common.slack

import org.pg.common.Context
import org.pg.common.Log

@Singleton
class Slack {
    static private def context
    static private String buildUserID
    static private String buildChannelID
    static private ArrayList<Message> messages
    static private def slackResponse

    static def setup() {
        context = Context.get()
        messages = [
                new Message("heading", "ads-product"),
                new Message("subheading", "Job initiated: Building *master* branch and deploying to *integration*")
        ]
        buildUserID = context.slackUserIdFromEmail(email: 'prince@propertyguru.com.sg', tokenCredentialId: 'slack-bot-token')
        def blocks = buildBlocks()
        Log.info(blocks)
        slackResponse = sendMessage(buildUserID, blocks)
        Log.info(slackResponse)
        buildChannelID = slackResponse.getChannelId()
    }

    static def send(Message msg=null) {
        // append msg
        if (msg != null) {
            messages.add(msg)
        }
        // build the block json array
        def blocks = buildBlocks()
        Log.info(blocks)
        // get timestamp from old slackResponse
        String timestamp = slackResponse.getTs()
        // send message and save response to slack response variable
        Log.info(slackResponse)
        slackResponse = sendMessage(buildChannelID, blocks, timestamp)
    }

    private static def buildBlocks() {
        def blocks = []
        messages.each { msg ->
            blocks.add(msg.format())
        }
        return blocks
    }

    // sendMessage works with channels and users.
    // for users, simply use @username.
    // @todo replace def with block Class type
    private static def sendMessage(String channels, def blocks, String timestamp = "") {
        Log.info("Block class type: ${blocks.getClass()}")
        context.slackSend(
                channel: channels,
                timestamp: timestamp,
                blocks: blocks,
                tokenCredentialId: 'slack-bot-token'
        )
    }

}

//if (message != "") {
//    def callingClass = ReflectionUtils.getCallingClass()
//    if (callingClass.isAssignableFrom(Base)) {
//
//    }
//    block.add([
//            "type": "context",
//            "elements": [
//                    [
//                            "type": "mrkdwn",
//                            "text": colors[status] + message
//                    ]
//            ]
//    ])
//}