package org.stages

import org.common.Blueprint
import org.common.Docker
import org.common.StepExecutor

class AnchoreScan extends Base {

    AnchoreScan() {
        super()
        this.stage = "Image Scanning"
        this.description = "Scanning docker image and file"
    }

    @Override
    def body() {
        def docker = new Docker()
        docker.setup()

        this.step("scanning docker file", {
            // Define path and file variables for Anchore
            String path = StepExecutor.shWithOutput('pwd')
            String dockerfile = path + "/${Blueprint.dockerfile()}"
            String anchorefile = path + "/anchore_images"

            // Write the anchorefile to call the scan from Jenkins Anchore plugin
            try {
                // Try to add the container image with Dockerfile
                StepExecutor.writeFile(anchorefile, docker.imageName() + " " + dockerfile)
                StepExecutor.anchore(anchorefile)
            } catch (Exception e) {
                StepExecutor.error("Failed to analyze the container with Dockerfile")
            }
        })
    }

    @Override
    Boolean skip() {
        return false
    }
}
