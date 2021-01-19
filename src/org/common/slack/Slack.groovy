package org.common.slack

import org.common.BuildArgs
import org.common.Context

@Singleton
class Slack {
    static private def context
    static private ArrayList<String> channels
    static private ArrayList<String> users
    static private ArrayList<Message> messages
    static private ArrayList slackResponses
    static private String attachments = null

    static def setup() {
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

        // get users from blueprints and get slack id of each user
        users = ["prince@propertyguru.com.sg"]
        ArrayList<String> userEmails = []
        users = users.each { user ->
            String userEmail = context.slackUserIdFromEmail(email: user, tokenCredentialId: 'slack-bot-token')
            userEmails.add(userEmail)
        }
        users = userEmails

        // get channels from blueprints
        channels = ["#prince-test", "#test-please-delete"]

        // build the blocks
        def blocks = buildBlocks()
        // send the message to channels
        sendMessage(channels, blocks)
    }

    static def send(Message msg=null) {
        // append msg
        if (msg != null) {
            messages.add(msg)
        }
        // send message and save response to slack response variable
        updateMessage()
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

    static def uploadFile(String name, String text) {
        context.writeFile(file: name, text: text)
        slackResponses.each { response ->
            context.slackUploadFile(
                    channel: response.threadId,
                    filePath: "changelog.txt"
            )
        }
    }

    private static def updateMessage() {
        ArrayList blocks = buildBlocks()
        slackResponses.each { response ->
            context.slackSend(
                    channel: response.channelId,
                    timestamp: response.ts,
                    blocks: blocks,
                    tokenCredentialId: 'slack-bot-token',
                    attachments: attachments
            )
        }
    }

}
