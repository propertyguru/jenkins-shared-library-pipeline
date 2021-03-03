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
    }

    @Override
    def body() {

        this.step("uploading static content", {
            Map static_content_branches = [:]
            for (String e in StepExecutor.env('ENVIRONMENT').tokenize(',')) {
                String env = e
                static_content_branches[env] = {
                    String tag = env
                    String name = Blueprint.component() + "/" + Blueprint.subcomponent() + "/static_content:" + tag
                    DockerClient.build(
                            name,
                            Blueprint.dockerfile(),
                            "${Blueprint.dockerArgs()} --build-arg BUILD_ENV=${env}",
                            "static-content",
                            "${env}_static"
                    )
                    Log.info("Uploading Static content to S3 to ${env}")
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
        // TODO: change this to checking from Dockerfile
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
