package org.pg.stages

import org.pg.common.AgentFactory
import org.pg.common.Context
import org.pg.common.Log
import org.pg.common.agents.IAgent
import org.pg.common.slack.Message
import org.pg.common.slack.Slack
import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

abstract class Base implements Serializable {
    def context
    String environment
    abstract String stage
    abstract Boolean skip = false
    Boolean failed = false
    abstract String description
    Message stepMessage

    Base(environment) {
        this.context = Context.get()
        this.environment = environment
        this.stepMessage = new Message("step", "Getting node to run the stage")
    }

    def execute() {
        if (!skip) {
            def slackMessage = new Message("stage", this.description, "running")
            Slack.send(slackMessage)
            Slack.send(this.stepMessage)
            IAgent agent = new AgentFactory(this.environment, this.stage).getAgent()
            agent.withSlave({
                try {
                    this.context.stage("${stage}") {
                        this.body()
                        slackMessage.update("success")
                    }
                } catch (Exception e) {
                    failed = true
                    slackMessage.update("failed")
                    Slack.send()
                    this.context.error("${e.toString()}")
                } finally {
                    Slack.send(new Message("divider"))
                }
            })
        } else {
            Log.info("Skipping ${this.stage}")
            Utils.markStageSkippedForConditional(this.stage)
            Slack.send(new Message("stage", this.description, "skipped"))
        }
    }

    void step(name, closure) {
        try {
            this.stepMessage.updateStep(name)
            Slack.send()
            closure()
        } catch (Exception e) {
            Slack.send(new Message("error", "ERROR: ${e.toString()}}"))
            Log.info("Prince Debugging: ${e.getStackTrace().join('\n')}")
            this.context.error("${e.toString()}")
        } finally {
            Log.info("Running final of step try/catch")
        }
    }

    abstract def body()

}