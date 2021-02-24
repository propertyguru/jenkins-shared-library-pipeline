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
        this.step("Loading data from blueprints repository.", {
            // TODO: find a better place to load blueprints. It has to be done on a node with access to salt-call.
            Blueprint.load()
        })
    }

    @Override
    Boolean skip() {
        return false
    }
}