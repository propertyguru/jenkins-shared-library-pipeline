package org.pg.common

import org.pg.common.agents.DockerAgent
import org.pg.common.agents.NoAgent

class AgentFactory {
    def environment
    def stage

    AgentFactory(environment, stage) {
        this.environment = environment
        this.stage = stage
    }

    def getAgent() {
        if (this.stage == "setup") {
            return new NoAgent(this.environment, this.stage)
        }
        return new DockerAgent(this.environment, this.stage)
    }

}
