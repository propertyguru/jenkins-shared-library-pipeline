package org.pg.common.agents

class NoAgent implements IAgent {
    def context
    def environment
    def stage

    NoAgent(context, environment, stage) {
        this.context = context
        this.environment = environment
        this.stage = stage
    }

    def withSlave(body) {
        body()
    }

}
