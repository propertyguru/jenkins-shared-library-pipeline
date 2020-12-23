package org.pg.common

import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonSlurper

class Salt {
    def context

    Salt(context) {
        this.context = context
    }

    def sync() {
        saltCall("saltutil.sync_all > /dev/null")
    }

    def blueprint(){
        def text = this.saltCallWithOutput("github.subcomponent 'pg_${BuildArgs.component()}_${BuildArgs.subcomponent()}' --output json")
        return jsonSlurper(text)
    }

    def saltCall(cmd){
        def log_level = Log.level() == "debug" ? '-l debug' : ''
        this.context.sh("salt-call ${log_level} ${cmd}")
    }

    def saltCallWithOutput(cmd){
        def log_level = Log.level() == "debug" ? '-l debug' : ''
        this.context.sh(returnStdout: true, script: "salt-call ${log_level} ${cmd}")
    }

    @NonCPS
    def jsonSlurper(data) {
        def js = new JsonSlurper()
        return js.parseText(data)
    }
}
