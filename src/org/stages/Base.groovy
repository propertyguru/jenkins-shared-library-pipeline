package org.stages

import org.common.Log
import org.common.StepExecutor
import org.common.slack.Message
import org.common.slack.Slack

abstract class Base implements Serializable {
    abstract String stage
    abstract String description
    Message stepMessage

    Base() {}

    void execute() {
        // TODO: need a way to skip the stage without getting the node.
        // this will improve the pipeline execution time too.
        // there are ways to do this, but I am looking for an alternate which doesn't make the code look ugly.
        // every stage has a skip() method, which decides whether to skip the stage or not.
        if (this.skip()) {
            // we need to put the stage directive here because otherwise the skipped stage is not visible on the UI.
            StepExecutor.stage(this.stage, {
                Log.info("Skipping ${this.stage}")
//                Utils.markStageSkippedForConditional(this.stage)
                Slack.send(new Message("stage", this.description, "skipped"))
            })
        } else {
            def slackMessage = new Message("stage", this.description, "running")
            Slack.send(slackMessage)
            try {
                StepExecutor.stage(this.stage, {
                    this.body()
                    slackMessage.update("success")
                })
            } catch (Exception e) {
                slackMessage.update("failed")
                Slack.send()
                Slack.send(new Message("divider"))
                Log.error(e.toString())
            }
        }

    }

    void step(name, closure) {
        try {
            if (this.stepMessage == null) {
                this.stepMessage = new Message("step", name)
                Slack.send(this.stepMessage)
            } else {
                this.stepMessage.addStep(name)
                Slack.send()
            }
            closure()
        } catch (Exception e) {
            Slack.send(new Message("error", "ERROR: ${e.toString()}}"))
            Log.info("Step exception: ${name}")
            Log.error(e.toString())
            Log.info(e.printStackTrace())
        }
    }

    abstract def body()
    abstract Boolean skip()

}