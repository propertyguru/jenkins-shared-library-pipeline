package org.stages

import org.common.Git
import org.common.Log
import org.common.StepExecutor
import org.common.slack.Message
import org.common.slack.Slack
import org.common.slack.StageBlock
import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

abstract class Base implements Serializable {
    abstract String stage
    StageBlock stageBlock

    Base() {}

    // variable test: this is just for debugging purposes. quite useful sometimes when you want to debug the pipeline
    // without actually doing the checkout or deployment. Though it does sends message on slack!

    void execute(Boolean test = false) {
        this.stageBlock = new StageBlock()
        this.stageBlock.addHeading(this.stage, "running")
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
                this.stageBlock.addHeading(this.stage, "skipped")
                Slack.updateMessage()
            })
        } else {
            try {
                StepExecutor.stage(this.stage, {
                    // we only skip this line while running
                    if (!test) {
                        this.body()
                    }
                    this.stageBlock.addHeading(this.stage, "success")
                    Slack.updateMessage()
                })
            } catch (Exception e) {
                this.stageBlock.addHeading(this.stage, "failed")
                Slack.updateMessage()
                Log.error("STAGE FUNCTION REPORTING: " + e.toString())
            }
        }

    }

    void step(String name, def closure) {
        try {
            Git.updatePRStatus(this.stage, name, "PENDING")
            this.stageBlock.addSteps(name)
            Slack.updateMessage()
            closure()
            Git.updatePRStatus(this.stage, name, "SUCCESS")
        } catch (Exception e) {
            Git.updatePRStatus(this.stage, name, "FAILURE")
            Message.addError("ERROR: ${e.toString()}}")
            Slack.updateMessage()
            Log.info("Step exception: ${name}")
            Log.info(e.printStackTrace())
            Log.error("STEP FUNCTION REPORTING: " + e.toString())
        }
    }

    abstract def body()
    abstract Boolean skip()

}