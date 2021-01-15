package org.pg.stages

import org.pg.common.Blueprint
import org.pg.common.Docker
import org.pg.common.Log
import org.pg.common.Salt

class StaticContent extends Base {
    StaticContent(Object environment) {
        super(environment)
        this.stage = "Static content"
        this.description = "upload static content"
    }

    @Override
    def body() {
        def docker = new Docker()
        docker.setup()

        this._context.dir(Blueprint.deployPath()) {
            // check if static content needs to be uploaded.
            // TODO: this can be moved to deploy stage so that we upload content and deploy in parallel
            this.step("uploading static content", {
                def static_content_branches = [:]
                for (String e in this._context.ENVIRONMENT.tokenize(',')) {
                    String env = e
                    static_content_branches[env] = {
                        docker.build(env, "static-content", "${env}_static",
                                "--build-arg BUILD_ENV=${env}")
                        Log.debug("Uploading Static content to S3 to ${env}")
                        (new Salt()).saltCallWithOutput("shipit.static_content ${Blueprint.component()} " +
                                "${Blueprint.subcomponent()} ${env} ${env}_static/app/")
                        this._context.sh("rm -rf ./${env}_static")
                    }
                }
                this._context.parallel static_content_branches
            })
        }
    }

    @Override
    Boolean skip() {
        if (Blueprint.staticContent().isEmpty()) {
            return true
        }
        return false
    }
}
