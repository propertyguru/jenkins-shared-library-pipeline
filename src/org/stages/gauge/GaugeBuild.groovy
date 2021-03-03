package org.stages.gauge

import org.common.Blueprint
import org.common.DockerClient
import org.common.Git
import org.common.StepExecutor
import org.stages.Base

class GaugeBuild extends Base{

    GaugeBuild() {
        super()
        this.stage = "Gauge Build"
    }

    @Override
    def body() {
        ArrayList extensions = []
        this.step("Loading extensions", {
            extensions.add([$class: 'RelativeTargetDirectory', relativeTargetDir: "gauge"])
        })
        StepExecutor.dir("/srv", {
            this.step("Checking out code", {
                Git.checkout("git@github.com:propertyguru/qa-automation.git", extensions)
            })
            DockerClient.build(
                    "",
                    "${StepExecutor.env("BRANCH")}",
                    "${Blueprint.dockerfile()}",
                    "${Blueprint.dockerArgs()}"
            )
        })
    }

    @Override
    Boolean skip() {
        return null
    }
}
