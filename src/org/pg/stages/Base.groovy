package org.pg.stages

import org.pg.common.Context
import org.pg.common.Log
import org.pg.common.slack.Message
import org.pg.common.slack.Slack
import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

abstract class Base implements Serializable {
    def context
    String environment
    abstract String stage
    abstract Boolean skip = false
    abstract String description
    Message stepMessage

    Base(environment) {
        this.context = Context.get()
        this.environment = environment
        this.stepMessage = new Message("step", "Getting node to run the stage")
    }

    def execute() {
        def slackMessage = new Message("stage", this.description, "running")
        Slack.send(slackMessage)
        Slack.send(this.stepMessage)

        try {
            this.context.stage(this.stage) {
                // if stage sets the skip variable to true, we need to handle that case using Utils static method.
                if (!skip) {
                    this.body()
                    slackMessage.update("success")
                } else {
                    Log.info("Skipping ${this.stage}")
                    Utils.markStageSkippedForConditional(this.stage)
                    Slack.send(new Message("stage", this.description, "skipped"))
                }
            }
        } catch (Exception e) {
            slackMessage.update("failed")
            Slack.send()
            this.context.error("${e.toString()}")
        } finally {
            Slack.send(new Message("divider"))
        }

    }

    void step(name, closure) {
        try {
            this.stepMessage.updateStep(name)
            Slack.send()
            closure()
        } catch (Exception e) {
            Slack.send(new Message("error", "ERROR: ${e.toString()}}"))
            this.context.error("${e.toString()}")
        } finally {
            Log.info("Running final of step ${name}")
        }
    }

    abstract def body()

}