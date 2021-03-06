package org.slack

import org.common.Blueprint
import org.common.BuildArgs
import org.common.StepExecutor

@Singleton
class Slack implements Serializable {
    static private ArrayList<String> channels
    static private ArrayList<String> users
    static private ArrayList slackResponses = null

    static def setup() {
        // get users and channels from blueprints and get slack id of each user
        users = getUsers()
        channels = getChannels()
        buildMessage()
        sendMessage()
    }

    static ArrayList<String> getChannels() {
        // TODO: this needs to be the build user, not my email.
        return ["prince-test"]
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
        return ["prince@propertyguru.com.sg"]
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
            heading += " : Github Pull Request"
        }
        MessageTemplate.heading = heading
        MessageTemplate.startedBy = BuildArgs.buildUser()
        MessageTemplate.branch = StepExecutor.env('GIT_BRANCH')
        MessageTemplate.jenkinsJobURL = BuildArgs.buildURL()
    }

    // sendMessage works with channels and users.
    // for users, simply use @username.
    static void sendMessage() {
        ArrayList blocks = MessageTemplate.builder()
        // if slackResponses is null, means we are sending msg for the first time.
        if (slackResponses == null) {
            slackResponses = []
            channels.each { String channel ->
                slackResponses.add(
                    StepExecutor.slackSend(channel, blocks)
                )
            }
        }
        // else we keep updating the message everywhere.
        slackResponses.each { response ->
            StepExecutor.slackSend(response.channelId as String, blocks, response.ts as String)
        }
    }

    static def uploadFile(String name, String text) {
        StepExecutor.writeFile(name, text)
        slackResponses.each { response ->
            StepExecutor.slackUploadFile(response.threadId as String, "${name}")
        }
    }

}
