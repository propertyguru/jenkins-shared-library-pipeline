package org.common.slack

import org.common.Blueprint
import org.common.BuildArgs
import org.common.Log
import org.common.StepExecutor
import org.common.Utils

@Singleton
class Slack implements Serializable {
    static private ArrayList<String> channels
    static private ArrayList<String> users
    static private ArrayList slackResponses

    static def setup() {
        // Setting up slack Message
        String heading = Blueprint.name()
        if (BuildArgs.isPRJob()) {
            heading += " - Triggered by Github"
        }
        Message.addHeading(heading)

        Message.addDetails([
                "*Started By:*\n${BuildArgs.buildUser()}",
                "*Branch:*\n${StepExecutor.env('GIT_BRANCH')}",
                "*Environment:*\n${StepExecutor.env('ENVIRONMENT')}",
                "*Jenkins URL:*\n${BuildArgs.buildURL()}"
        ] as ArrayList<String>)
        // get users from blueprints and get slack id of each user
        // TODO: this needs to be the build user, not my email.
        users = ["prince@propertyguru.com.sg"]
        ArrayList<String> userEmails = []
        users = users.each { user ->
            String userEmail = StepExecutor.slackUserIdFromEmail(user)
            userEmails.add(userEmail)
        }
        users = userEmails
        // get channels from blueprints
        channels = ["#prince-test", "#test-please-delete"]
        // send the message to channels
        sendMessage()
    }

    // sendMessage works with channels and users.
    // for users, simply use @username.
    static void sendMessage() {
        ArrayList responses = []
        channels.each { String channel ->
            responses.add(
                    StepExecutor.slackSend(channel, Message.toBlocks())
            )
        }
        slackResponses = responses
    }

    static def uploadFile(String name, String text) {
        StepExecutor.writeFile(name, text)
        slackResponses.each { response ->
            StepExecutor.slackUploadFile(response.threadId as String, "${name}")
        }
    }

    static def updateMessage() {
        ArrayList blocks = Message.toBlocks()
        slackResponses.each { response ->
            StepExecutor.slackSend(response.channelId as String, blocks, response.ts as String)
        }
    }

}
