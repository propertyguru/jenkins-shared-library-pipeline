package org.pg.common

import org.pg.common.agents.DockerAgent
import org.pg.common.agents.NoAgent

class AgentFactory {
    def context
    def environment
    def stage

    AgentFactory(context, environment, stage) {
        this.context = context
        this.environment = environment
        this.stage = stage
    }

    def getAgent() {
        if (this.stage == "setup") {
            return new NoAgent(this.context, this.environment, this.stage)
        }
        return new DockerAgent(this.context, this.environment, this.stage)
    }

}
