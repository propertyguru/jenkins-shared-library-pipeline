package org.stages

import org.common.Blueprint
import org.common.BuildArgs
import org.common.Git
import org.common.Log
import org.common.PGbuild
import org.common.StepExecutor
import org.slack.Slack

class Build extends Base {

    Build() {
        super()
        this.stage = "Build"
    }

    def body() {
        this.step("Sharing changelog on slack", {
            StepExecutor.env('ENVIRONMENT').tokenize(',').each { String env ->
                ArrayList<String> changelog = Git.getChangelog(env)
                String msg = ""
                if (changelog.size() > 0) {
                    changelog.each { Map cl ->
                        msg += "\nAuthor: ${cl['author']}"
                        msg += "\nAuthor Email: ${cl['authorEmail']}"
                        msg += "\n*${cl['messageBody']}*"
                        msg += "\nChanged Files:"
                        cl['changedFiles'].each { String cf ->
                            msg += "\n - ${cf}"
                        }
                        msg += "\n"
                    }
                } else {
                    msg = "No changes in ${env}"
                }
                Slack.uploadFile("${env}-changelog.txt", "${msg}")
            }
        })

        this.step("Setting tags", {
            // setting tags
            String tagname
            StepExecutor.env('ENVIRONMENT').tokenize(',').each { String env ->
                tagname = env + "-" + BuildArgs.buildNumber()
//                Git.createTag(tagname)
            }
        })

        // starting a new step instead of a state for unit tests etc.
        this.step("Stashing files", {
            StepExecutor.stash("infra", "${Blueprint.appConfig()}/*.*")
            StepExecutor.stash("pgbuild", Blueprint.pgbuild())
        })

        this.step("Running pre-build from pgbuild file", {
            Log.debug("Running pre-build steps from pgbuild.")
            PGbuild.executeSteps("pre-build")
        })

        this.step("Running build steps from pgbuild", {
            Log.debug("Running build steps from pgbuild.")
            PGbuild.executeSteps("build")
        })

        this.step("Running unit-tests from pgbuild", {
            Log.debug('Running unit-tests steps from pgbuild.')
            PGbuild.executeSteps('unit_tests')
        })
    }

    @Override
    Boolean skip() {
        // make sure this runs for pr jobs too.
        // skip this stage if we are not deploying on integration and hotfix is not checked.
//        if (!('integration' in StepExecutor.env('ENVIRONMENT').tokenize(',')) &&
//                StepExecutor.env('HOTFIX') == "false") {
//            return true
//        }
        return false
    }
}
