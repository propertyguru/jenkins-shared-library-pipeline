package org.pg.common.agents

class NoAgent implements IAgent {
    def environment
    def stage

    NoAgent(environment, stage) {
        this.environment = environment
        this.stage = stage
    }

    def withSlave(body) {
        body()
    }

}
