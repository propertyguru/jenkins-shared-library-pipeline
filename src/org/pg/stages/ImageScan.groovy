package org.pg.stages

import org.pg.common.Blueprint
import org.pg.common.Docker
import org.pg.common.Output

class ImageScan extends Base {

    ImageScan(environment) {
        super(environment)
        this.stage = "Image Scanning"
        this.description = "Scanning docker image and file"
    }

    @Override
    def body() {
        def docker = new Docker()
        docker.setup()

        this.step("scanning docker file", {
            // Define path and file variables for Anchore
            String path = this.context.sh(script: 'pwd', returnStdout: true).trim()
            String dockerfile = path + "/${Blueprint.dockerfile()}"
            String anchorefile = path + "/anchore_images"
            String anchore_url = 'http://internal-ac935e7eeb5ed11eaaf2206349892d33-705533012.ap-southeast-1.elb.amazonaws.com:8228/v1'

            // Write the anchorefile to call the scan from Jenkins Anchore plugin
            try {
                // Try to add the container image with Dockerfile
                this.context.writeFile(file: anchorefile, text: docker.imageName() + " " + dockerfile)
                this.context.anchore(
                        url: anchore_url,
                        engineCredentialsId: 'anchore',
                        name: anchorefile,
                        engineRetries: "900",
                        bailOnFail: false,
                        bailOnPluginFail: false
                )
            } catch (Exception e) {
                this.context.error("Failed to analyze the container with Dockerfile")
            }
        })
    }
}
