package org.common

import org.jenkinsci.plugins.workflow.steps.Step

@Singleton
class PGbuild {
    private LinkedHashMap rawContent = null

    static def load() {
        this.instance.content()
    }

    static def version() {
        load()
        return this.instance.rawContent.get('version', '1.0')
    }

    static def entrypoint() {
        // not sure who is doing this.
        // make sure to put in comments why was something built and who is using it.
        load()
        return this.instance.rawContent.get('entrypoint', "")
    }

    static def steps(String stage) {
        load()
        return this.instance.rawContent.get(stage, [])
    }

    static def executeSteps(String stage) {
        def stageSteps = steps(stage)
        for (String step in stageSteps) {
            StepExecutor.sh(step)
        }
    }

    def content() {
        if (this.rawContent == null) {
            def file = Blueprint.pgbuild()
            if (StepExecutor.fileExists(file)) {
                this.rawContent = StepExecutor.readYaml(file)
            } else {
                Log.error("${file} file not present")
            }
        }
        this.rawContent
    }
}
