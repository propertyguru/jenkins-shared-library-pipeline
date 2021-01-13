package org.pg.common.slack

import org.pg.common.BuildArgs
import org.pg.common.Context
import org.pg.common.Log

@Singleton
class Slack {
    static private def context
    static private ArrayList channels
    static private ArrayList<Message> messages
    static private ArrayList slackResponses

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
//        buildUserID = context.slackUserIdFromEmail(email: 'prince@propertyguru.com.sg', tokenCredentialId: 'slack-bot-token')
//        Log.info(buildUserID)
        channels = ["#prince-test", "#test-please-delete"]
        def blocks = buildBlocks()
        sendMessage(channels, blocks)
    }

    static def send(Message msg=null) {
        // append msg
        if (msg != null) {
            messages.add(msg)
        }
        // send message and save response to slack response variable
        updateMessage(buildBlocks())
    }

    private static ArrayList buildBlocks() {
        def blocks = []
        messages.each { msg ->
            blocks.add(msg.format())
        }
        return blocks
    }

    // sendMessage works with channels and users.
    // for users, simply use @username.
    private static def sendMessage(ArrayList channels, ArrayList blocks) {
        ArrayList responses = []
        channels.each { channel ->
            responses.add(context.slackSend(
                    channel: channel,
                    blocks: blocks,
                    tokenCredentialId: 'slack-bot-token'
            ))
        }
        slackResponses = responses
    }

    private static def updateMessage(ArrayList blocks) {
        slackResponses.each { response ->
            context.slackSend(
                    channel: response.channelId,
                    timestamp: response.ts,
                    blocks: blocks,
                    tokenCredentialId: 'slack-bot-token'
            )
        }
    }

}
