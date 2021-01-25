package org.common

class Salt {
    Salt() {}

    def sync() {
        saltCall("saltutil.sync_all > /dev/null")
    }

    def blueprint(){
        def text = this.saltCallWithOutput("github.subcomponent 'pg_${Blueprint.component()}_${Blueprint.subcomponent()}' --output json")
        return Utils.toJson(text)
    }

    void saltCall(String cmd){
        def log_level = Log.level() == "debug" ? '-l debug' : ''
        cmd = "salt-call ${log_level} ${cmd}"
        Log.info(cmd)
        StepExecutor.sh(cmd)
    }

    String saltCallWithOutput(String cmd){
        String log_level = Log.level() == "debug" ? '-l debug' : ''
        cmd = "salt-call ${log_level} ${cmd}"
        Log.info(cmd)
        return StepExecutor.shWithOutput(cmd)
    }

}
