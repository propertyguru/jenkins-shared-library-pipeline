package org.stages

import org.common.Blueprint

class Setup extends Base {

    private ArrayList extensions

    Setup() {
        super()
        this.stage = "Setup"
    }

    @Override
    def body() {
        // nothing here! we were doing blueprint load here.. moved to main pipeline inside vars.
    }

    @Override
    Boolean skip() {
        return false
    }
}