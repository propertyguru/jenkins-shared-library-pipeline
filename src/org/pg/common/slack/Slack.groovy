package org.pg.common.slack

import org.pg.common.BuildArgs
import org.pg.common.Context
import org.pg.common.Log

@Singleton
class Slack {
    static private def context
    static private String buildUserID
    static private String buildChannelID
    static private ArrayList<Message> messages
    static private def slackResponse

    static def setup(slack_id) {
        context = Context.get()
        messages = [
                new Message("heading", "ads-product"),
                new Message("sectionWithFields", "", "running", [
                        "*Started By:*\n${BuildArgs.buildUser()}",
                        "*Branch:*\n${context.BRANCH}",
                        "*Environment:*\n${context.ENVIRONMENT}",
                        "*Jenkins URL:*\n${BuildArgs.buildURL()}"
                ] as ArrayList<String>),
                new Message("divider")
        ]
        buildUserID = context.slackUserIdFromEmail(email: 'prince@propertyguru.com.sg', tokenCredentialId: 'slack-bot-token')
        Log.info(buildUserID)
        def blocks = buildBlocks()
        slackResponse = sendMessage(buildUserID, blocks)
        buildChannelID = slackResponse.getChannelId()
    }

    static def send(Message msg=null) {
        // append msg
        if (msg != null) {
            messages.add(msg)
        }
        // build the block json array
        def blocks = buildBlocks()
        // get timestamp from old slackResponse
        String timestamp = slackResponse.getTs()
        // send message and save response to slack response variable
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
    private static def sendMessage(String channels, ArrayList blocks, String timestamp = "") {
        context.slackSend(
                channel: channels,
                timestamp: timestamp,
                blocks: blocks,
                tokenCredentialId: 'slack-bot-token'
        )
    }

}
