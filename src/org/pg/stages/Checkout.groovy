package org.pg.stages

import org.pg.common.Git

class Checkout extends Base {

    Checkout(String environment) {
        super(environment)
        this.stage = "checkout"
        this.description = "checking out code"
    }

    @Override
    def body() {
        this.step("checking out code from github", {
            Git.checkout()
        })

    }
}
