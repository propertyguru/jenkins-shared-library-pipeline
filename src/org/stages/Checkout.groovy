package org.stages

import org.common.Blueprint
import org.common.BuildArgs
import org.common.Git
import org.common.Log
import org.common.StepExecutor
import org.common.Utils
import org.common.slack.Slack

class Checkout extends Base {

    private ArrayList extensions

    Checkout() {
        super()
        this.stage = "Github"
        this.description = "Github"
        this.extensions = []
    }

    @Override
    def body() {

        this.step("Loading data", {
            // TODO: find a better place to load blueprints. It has to be done on a node with access to salt-call.
            Blueprint.load()
        })

        this.step("Checking out code", {
            String tag = ""
            StepExecutor.env('ENVIRONMENT').tokenize(',').each { String env ->
                tag = Git.getLastTag(env)
            }
            if (tag != "") {
                extensions.add([$class: 'ChangelogToBranch', options: [compareRemote: 'refs', compareTarget: "tags/${tag}"]])
                Log.info("Added extension: ChangelogToBranch")
            }
            Git.checkout(extensions)
        })

        this.step("Sharing changelog on slack", {
            StepExecutor.env('ENVIRONMENT').tokenize(',').each { String env ->
                ArrayList<String> changelog = Git.getChangelog(env)
                String msg
                if (changelog.size() > 0) {
                    msg = Utils.toString(changelog)
                } else {
                    msg = "No new changes in ${env}"
                }
                Slack.uploadFile("${env}-changelog.txt", "${msg}")
            }
        })

        this.step("Setting tags", {
            // setting tags
            String tagname
            StepExecutor.env('ENVIRONMENT').tokenize(',').each { String env ->
                tagname = env + "-" + BuildArgs.buildNumber()
                Git.createTag(tagname)
            }
        })

    }

    @Override
    Boolean skip() {
        return false
    }
}
