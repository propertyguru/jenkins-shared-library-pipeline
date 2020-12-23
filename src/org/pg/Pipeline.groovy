package org.pg

import org.pg.stages.Checkout
import org.pg.stages.Build
import org.pg.stages.Deploy
import org.pg.stages.Setup

class Pipeline {

    private def context

    Pipeline(context) {
        this.context = context
    }

    def execute() {
        new Setup(this.context, "integration").execute()
        new Checkout(this.context, "integration").execute()
        new Build(this.context, "integration").execute()

        new Deploy(this.context, "integration").execute()
        new Deploy(this.context, "staging").execute()
        new Deploy(this.context, "production").execute()
    }

}
