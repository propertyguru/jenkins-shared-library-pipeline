package org.common

import org.common.agents.DockerAgent

class AgentFactory {
    String environment

    AgentFactory(environment) {
        this.environment = environment
    }

    def getAgent() {
        return new DockerAgent(this.environment)
    }

}
