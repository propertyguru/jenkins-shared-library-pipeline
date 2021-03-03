package org.stages

import org.common.Blueprint

import org.common.StepExecutor

class Kong extends Base {

    private String environment

    Kong(String environment) {
        super()
        this.environment = environment
        this.stage = "Kong - ${this.environment}"
    }

    @Override
    def body() {
        this.step("Configuring Kong API gateway", {
            StepExecutor.unstash("pgbuild")
            String cmd = "kong.apply ${Blueprint.component()} ${Blueprint.subcomponent()} ${Blueprint.pgbuild()}"
//            (new Salt()).saltCallWithOutput(cmd)
        })
    }

    @Override
    Boolean skip() {
        // set skip variable to true if this stage needs to be skipped.
        if (this.environment in StepExecutor.env('ENVIRONMENT').tokenize(',')) {
            return false
        }
        return true
    }
}
