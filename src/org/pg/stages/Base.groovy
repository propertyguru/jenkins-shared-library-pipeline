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
    abstract String description
    Message stepMessage

    Base(environment) {
        this.context = Context.get()
        this.environment = environment
    }

    void execute() {
        // TODO: need a way to skip the stage without getting the node.
        // this will improve the pipeline execution time too.
        // there are ways to do this, but I am looking for an alternate which doesn't make the code look ugly.
        // every stage has a skip() method, which decides whether to skip the stage or not.
        if (this.skip()) {
            // we need to put the stage directive here because otherwise the skipped stage is not visible on the UI.
            this.context.stage(this.stage) {
                Log.info("Skipping ${this.stage}")
                Utils.markStageSkippedForConditional(this.stage)
                Slack.send(new Message("stage", this.description, "skipped"))
            }
        } else {
            def slackMessage = new Message("stage", this.description, "running")
            Slack.send(slackMessage)
            try {
                this.context.stage(this.stage) {
                    this.body()
                    slackMessage.update("success")
                }
            } catch (Exception e) {
                slackMessage.update("failed")
                Slack.send()
                this.context.error("${e.toString()}")
            } finally {
                Slack.send(new Message("divider"))
            }
        }

    }

    void step(name, closure) {
        try {
            println(this.stepMessage.getClass())
            println(this.stepMessage)
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

            this.context.error("${e.toString()}")
        } finally {
            Log.info("Running final of step ${name}")
        }
    }

    abstract def body()
    abstract Boolean skip()

}