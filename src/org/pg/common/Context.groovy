package org.pg.common

import com.cloudbees.groovy.cps.NonCPS

@Singleton
class Context {
    private static def context

    static def set(context) {
        this.context = context
    }

    @NonCPS
    static def get() {
        this.context
    }

}
