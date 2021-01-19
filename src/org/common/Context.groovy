package org.common

import com.cloudbees.groovy.cps.NonCPS

@Singleton
class Context implements Serializable {
    private static def _context

    static def set(context) {
        _context = context
    }

    @NonCPS
    static def get() {
        _context
    }

}
