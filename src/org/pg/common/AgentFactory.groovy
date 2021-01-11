package org.pg.common

import org.pg.common.agents.DockerAgent
import org.pg.common.agents.NoAgent

class AgentFactory {
    String environment

    AgentFactory(environment) {
        this.environment = environment
    }

    def getAgent() {
        return new DockerAgent(this.environment)
    }

}
