package org.stages

import org.common.Blueprint
import org.common.DockerClient
import org.common.Log
import org.common.Salt
import org.common.StepExecutor

class StaticContent extends Base {
    StaticContent() {
        super()
        this.stage = "Static content"
        this.description = "upload static content"
    }

    @Override
    def body() {
        // check if static content needs to be uploaded.
        // TODO: this can be moved to deploy stage so that we upload content and deploy in parallel
        this.step("uploading static content", {
            Map static_content_branches = [:]
            Map image = DockerClient.imageDetails()
            for (String e in StepExecutor.env('ENVIRONMENT').tokenize(',')) {
                String env = e
                static_content_branches[env] = {
                    DockerClient.build(
                            "${image['name']}",
                            "${image['tag']}",
                            "${Blueprint.dockerfile()}",
                            "${Blueprint.dockerArgs()} --build-arg BUILD_ENV=${env}",
                            "static-content",
                            "${env}_static"
                    )
                    Log.debug("Uploading Static content to S3 to ${env}")
                    (new Salt()).saltCallWithOutput("shipit.static_content ${Blueprint.component()} " +
                            "${Blueprint.subcomponent()} ${env} ${env}_static/app/")
                    // cleanup
                    // TODO: see if this can be done inside try/catch
                    StepExecutor.sh("rm -rf ./${env}_static")
                }
            }
            StepExecutor.parallel(static_content_branches)
        })
    }

    @Override
    Boolean skip() {
        // if static content configuration is not defined in blueprints, skip this stage.
        if (Blueprint.staticContent().isEmpty()) {
            return true
        }
        // skip this stage if we are not deploying on integration and hotfix is not checked.
        if (!('integration' in StepExecutor.env('ENVIRONMENT').tokenize(',')) &&
                StepExecutor.env('HOTFIX') == "false") {
            return true
        }
        return false
    }
}
