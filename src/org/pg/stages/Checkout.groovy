package org.pg.stages

import org.pg.common.Blueprint
import org.pg.common.Git
import org.pg.common.JobDescription
import org.pg.common.Log
import org.pg.common.Output

class Checkout extends Base {

    private ArrayList extensions

    Checkout(String environment) {
        super(environment)
        this.stage = "checkout"
        this.description = "checking out code"
        this.extensions = []
    }

    @Override
    def body() {
        this.step("loading data from blueprints", {
            Blueprint.load()
        })
        this.step("checking out code from github", {
            // TODO: find a better place to load blueprints. It has to be done on a node with access to salt-call.
            if (this._context.ENVIRONMENT.tokenize(',').size() > 0) {
                String tag = this._context.ENVIRONMENT.tokenize(',')[-1] + "-previous"
                new Output().sh("git tag -f ${tag} ${JobDescription.getValue()}")
                extensions.add([$class: 'ChangelogToBranch', options: [compareRemote: 'refs', compareTarget: "tags/${tag}"]])
            }
            Log.info("Done loading extensions: ${extensions}")
            Git.checkout(extensions)
        })
        this.step("loading changelog", {
            this._context.ENVIRONMENT.tokenize(',').each { String env ->
                Git.getChangelog(env)
            }
        })
    }

    @Override
    Boolean skip() {
        return false
    }
}
