package org.common.slack

import org.common.Blueprint
import org.common.BuildArgs
import org.common.StepExecutor

@Singleton
class Slack implements Serializable {
    static private ArrayList<String> channels
    static private ArrayList<String> users
    static private ArrayList slackResponses

    static def setup() {
        // Setting up slack Message
        buildMessage()
        // get users from blueprints and get slack id of each user
        // TODO: this needs to be the build user, not my email.
        users = getUsers()
        // get channels from blueprints
        channels = getChannels()
        // send the message to channels
        sendMessage()
    }

    static ArrayList<String> getChannels() {
        String environment = "integration"
        if (StepExecutor.env('ENVIRONMENT').tokenize(',').size() > 0) {
            environment = StepExecutor.env('ENVIRONMENT').tokenize(',')[-1]
        }
        ArrayList<String> channels = Blueprint.channels(environment)
        if (environment == "production") {
            channels.push("deployment")
        } else {
            channels.push("alerts-qa")
        }
        return channels.unique()
    }

    static ArrayList<String> getUsers() {
        ArrayList<String> userEmails = Blueprint.teamEmails()
        ArrayList<String> userIds
        userEmails.each { String id ->
            String uid = StepExecutor.slackUserIdFromEmail(id)
            userIds.add(uid)
        }
        return userIds.unique()
    }

    static void buildMessage() {
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
