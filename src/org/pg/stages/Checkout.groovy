package org.pg.stages

import org.pg.common.Blueprint
import org.pg.common.BuildArgs
import org.pg.common.Git
import org.pg.common.Log
import org.pg.common.Utils
import org.pg.common.slack.Slack

class Checkout extends Base {

    private ArrayList extensions

    Checkout(String environment) {
        super(environment)
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
            BuildArgs.getEnvParam().each { String env ->
                tag = Git.getLastTag(env)
            }
            if (tag != "") {
                extensions.add([$class: 'ChangelogToBranch', options: [compareRemote: 'refs', compareTarget: "tags/${tag}"]])
                Log.info("Added extension: ChangelogToBranch")
            }
            Git.checkout(extensions)
        })

        this.step("Sharing changelog on slack", {
            BuildArgs.getEnvParam().each { String env ->
                ArrayList<String> changelog = Git.getChangelog(env)
                String msg
                if (changelog.size() > 0) {
                    msg = Utils.toString(changelog)
                } else {
                    msg = "No new changes in ${env}"
                }
                Slack.uploadFile(msg)
            }
        })

        this.step("Setting tags", {
            // setting tags
            String tagname
            BuildArgs.getEnvParam().each { env ->
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
