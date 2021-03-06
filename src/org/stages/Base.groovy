package org.stages

import org.common.Git
import org.common.Log
import org.common.StepExecutor
import org.slack.MessageTemplate
import org.slack.Slack
import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

abstract class Base implements Serializable {
    abstract String stage
    protected ArrayList slackMessage
    protected Boolean test = true
    private Map<String, String> emoji = [
            "running": ":waiting:",
            "success": ":white_check_mark:",
            "failed": ":x:"
    ]
    protected Boolean skipSlack

    Base(Boolean skipSlack = false) {
        this.skipSlack = skipSlack
    }

    // variable test: this is just for debugging purposes. quite useful sometimes when you want to debug the pipeline
    // without actually doing the checkout or deployment. Though it does sends message on slack!
    void execute() {
        if (!this.skipSlack) {
            this.slackMessage = []
            MessageTemplate.addStageBlock(this.slackMessage)
        }

        // TODO: need a way to skip the stage without getting the node.
        // this will improve the pipeline execution time too.
        // there are ways to do this, but I am looking for an alternate which doesn't make the code look ugly.
        // every stage has a skip() method, which decides whether to skip the stage or not.
        if (this.skip()) {
            // we need to put the stage directive here because otherwise the skipped stage is not visible on the UI.
            StepExecutor.stage(this.stage, {
                Log.info("Skipping ${this.stage}")
                if (!StepExecutor.isUnitTest()) {
                    // TODO: this step needs to be fixed through build.gradle file maybe.
                    // its here because tests are not able to find this function
                    Utils.markStageSkippedForConditional(this.stage)
                }
                if (!skipSlack) {
                    this.slackMessage.add(MessageTemplate.markdownText("~* ${this.stage} *~"))
                    Slack.sendMessage()
                }
            })
        } else {
            Map<String, Serializable> stageMessage = [:]
            if (!skipSlack) {
                this.slackMessage.add(stageMessage)
            }
            try {
                StepExecutor.stage(this.stage, {
                    if (!skipSlack) {
                        stageMessage = MessageTemplate.markdownText(this.emoji["running"] + " *" + this.stage + "*")
                        Slack.sendMessage()
                    }
                    // we only skip this line while running
                    this.body()
                    if (!skipSlack) {
                        stageMessage = MessageTemplate.markdownText(this.emoji["success"] + " *" + this.stage + "*")
                        Slack.sendMessage()
                    }
                })
            } catch (Exception e) {
                if (!skipSlack) {
                    stageMessage = MessageTemplate.markdownText(this.emoji["failed"] + " *" + this.stage + "*")
                    Slack.sendMessage()
                }
                Log.error("STAGE FUNCTION REPORTING: " + e.toString())
            }
        }

    }

    void step(String name, def closure) {
        Map<String, Serializable> stepMessage = [:]
        if (!skipSlack) {
            String stepText = stepMessage.get("text", [:]).get("text", "")
            if (stepText != "") {
                stepText += "\n${name}"
                stepMessage = MessageTemplate.markdownText(stepText)
            } else {
                stepText = name
                stepMessage = MessageTemplate.markdownText(stepText)
            }
            this.slackMessage.add(stepMessage)
            Slack.sendMessage()
        }
        try {
            Git.updatePRStatus(this.stage, name, "PENDING")
            if (!test) {
                closure()
            }
            Git.updatePRStatus(this.stage, name, "SUCCESS")
        } catch (Exception e) {
            Git.updatePRStatus(this.stage, name, "FAILURE")
            if (!skipSlack) {
                MessageTemplate.errorBlock = MessageTemplate.markdownText("ERROR: ${e.toString()}}")
                Slack.sendMessage()
            }
            Log.info("Step exception: ${name}")
            Log.info(e.printStackTrace())
            Log.error("STEP FUNCTION REPORTING: " + e.toString())
        }
    }

    abstract def body()
    abstract Boolean skip()

}