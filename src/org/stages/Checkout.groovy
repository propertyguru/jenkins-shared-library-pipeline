package org.stages

import org.common.Blueprint
import org.common.Git
import org.common.Log
import org.common.StepExecutor

class Checkout extends Base {

    private ArrayList extensions

    Checkout() {
        super()
        this.stage = "Github"
        this.extensions = []
    }

    @Override
    def body() {
        // this first step will not run in case of PR job because envParam will be empty.
        ArrayList<String> envParam = StepExecutor.env('ENVIRONMENT').tokenize(',')
        // TODO: this needs to be fixed! doing it just for PR job
        String tag = StepExecutor.env("GIT_BRANCH")
        if (envParam.size() > 0) {
            this.step("Setting tags in github", {
                envParam.each { String env ->
                    tag = Git.getLastDeployedTag(env)
                    if (tag == null) {
                        // we are creating this tag for apps where we don't have tags pointing to previous deployments yet.
                        tag = "devtools-${env}"
                        Git.createTag(tag, "HEAD~6")
                    }
                }
            })
        }

        this.step("Loading extensions", {
            // we only load the changelog for 1 environment, production get the most priority, integration least!
            this.extensions.add([$class: 'ChangelogToBranch', options: [
                    compareRemote: 'origin', // it was refs
                    compareTarget: tag
            ]])
        })

        Log.info(this.extensions as String)
        this.step("Checking out code", {
            Git.checkout(Blueprint.repository(), this.extensions)
        })

    }

    @Override
    Boolean skip() {
        // checkout the branch, no matter what!
        return false
    }
}