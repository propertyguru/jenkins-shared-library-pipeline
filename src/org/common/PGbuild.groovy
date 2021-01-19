package org.common

@Singleton
class PGbuild {
    private static def _context
    private LinkedHashMap rawContent = null

    static def setup() {
        _context = Context.get()
    }

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
        for (step in stageSteps) {
            _context.sh "${step}"
        }
    }

    def content() {
        if (rawContent == null) {
            def file = Blueprint.pgbuild()
            if (_context.fileExists(file)){
                this.rawContent = _context.readYaml(file: file)
            } else {
                Log.error("${file} file not present")
            }
        }
        this.rawContent
    }
}
