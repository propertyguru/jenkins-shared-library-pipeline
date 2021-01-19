package org.common.agents

class NoAgent implements IAgent {
    String environment

    NoAgent(environment) {
        this.environment = environment
    }

    def withSlave(body) {
        body()
    }

}
