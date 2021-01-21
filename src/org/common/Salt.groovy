package org.common

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

    def saltCall(String cmd){
        def log_level = Log.level() == "debug" ? '-l debug' : ''
        cmd = "salt-call ${log_level} ${cmd}"
        Log.info(cmd)
        this._context.sh(cmd)
    }

    def saltCallWithOutput(String cmd){
        String log_level = Log.level() == "debug" ? '-l debug' : ''
        cmd = "salt-call ${log_level} ${cmd}"
        Log.info(cmd)
        this._context.sh(returnStdout: true, script: cmd)
    }

}
