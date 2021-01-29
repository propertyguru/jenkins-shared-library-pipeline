package org.stages

import org.common.BuildArgs
import org.common.Log
import org.common.StepExecutor
import org.common.slack.Message
import org.common.slack.Slack
import org.common.slack.StageBlock
import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

abstract class Base implements Serializable {
    abstract String stage
    abstract String description
    StageBlock stageBlock

    Base() {}

    void execute() {
        this.stageBlock = new StageBlock()
        this.stageBlock.addHeading(this.description, "running")
        Message.addStageBlock(this.stageBlock)
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
                this.stageBlock.addHeading(this.description, "skipped")
                Slack.updateMessage()
            })
        } else {
            try {
                StepExecutor.stage(this.stage, {
                    this.body()
                    this.stageBlock.addHeading(this.description, "success")
                    Slack.updateMessage()
                })
            } catch (Exception e) {
                this.stageBlock.addHeading(this.description, "failed")
                Slack.updateMessage()
                Log.error(e.toString())
            }
        }

    }

    void step(String name, def closure) {
        try {
            this.stageBlock.addSteps(name)
            Slack.updateMessage()
            closure()
        } catch (Exception e) {
            Message.addError("ERROR: ${e.toString()}}")
            Slack.updateMessage()
            Log.info("Step exception: ${name}")
            Log.error(e.toString())
            Log.info(e.printStackTrace())
        }
    }

    abstract def body()
    abstract Boolean skip()

}