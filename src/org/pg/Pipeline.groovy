package org.pg

import org.pg.stages.Checkout
import org.pg.stages.Build
import org.pg.stages.Deploy

class Pipeline {

    Pipeline() {}

    def execute() {
        new Build("integration").execute()

        new Deploy("integration").execute()
        new Deploy("staging").execute()
        new Deploy("production").execute()
    }

}
