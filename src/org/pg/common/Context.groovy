package org.pg.common

import com.cloudbees.groovy.cps.NonCPS

@Singleton
class Context implements Serializable {
    private static def _context

    static def set(context) {
        this._context = context
    }

    @NonCPS
    static def get() {
        this._context
    }

}
