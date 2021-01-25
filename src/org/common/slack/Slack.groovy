package org.common.slack

import io.jenkins.cli.shaded.org.glassfish.tyrus.core.Utils
import org.common.BuildArgs
import org.common.Log
import org.common.StepExecutor

@Singleton
class Slack {
    static private ArrayList<String> channels
    static private ArrayList<String> users
    static private ArrayList<Message> messages
    static private ArrayList slackResponses
    static private String attachments = null

    static def setup() {
        messages = [
                new Message("heading", "ads-product"),
                new Message("sectionWithFields", "", "running", [
                        "*Started By:*\n${BuildArgs.buildUser()}",
                        "*Branch:*\n${StepExecutor.env('BRANCH')}",
                        "*Environment:*\n${StepExecutor.env('ENVIRONMENT')}",
                        "*Jenkins URL:*\n${BuildArgs.buildURL()}"
                ] as ArrayList<String>),
                new Message("divider")
        ]

        // get users from blueprints and get slack id of each user
        users = ["prince@propertyguru.com.sg"]
        ArrayList<String> userEmails = []
        users = users.each { user ->
            String userEmail = StepExecutor.slackUserIdFromEmail(user)
            userEmails.add(userEmail)
        }
        users = userEmails

        // get channels from blueprints
        channels = ["#prince-test", "#test-please-delete"]

        // build the blocks
        ArrayList blocks = buildBlocks()
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
        channels.each { String channel ->
            responses.add(
                    StepExecutor.slackSend(channel, blocks)
            )
        }
        slackResponses = responses
    }

    static def uploadFile(String name, String text) {
        StepExecutor.writeFile(name, text)
        slackResponses.each { response ->
            StepExecutor.slackUploadFile(response.threadId as String, "changelog.txt")
        }
    }

    private static def updateMessage() {
        ArrayList blocks = buildBlocks()
        slackResponses.each { response ->
            StepExecutor.slackSend(response.channelId as String, blocks, response.ts as String, attachments)
        }
    }

}
