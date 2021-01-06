package org.pg.common

class Salt {
    private def _context

    Salt() {
        this._context = Context.get()
    }

    def sync() {
        saltCall("saltutil.sync_all > /dev/null")
    }

    def blueprint(){
        def text = this.saltCallWithOutput("github.subcomponent 'pg_${Blueprint.component()}_${Blueprint.subcomponent()}' --output json")
        return Utils.toJson(text)
    }

    def saltCall(cmd){
        def log_level = Log.level() == "debug" ? '-l debug' : ''
        this._context.sh("salt-call ${log_level} ${cmd}")
    }

    def saltCallWithOutput(cmd){
        def log_level = Log.level() == "debug" ? '-l debug' : ''
        this._context.sh(returnStdout: true, script: "salt-call ${log_level} ${cmd}")
    }

}
