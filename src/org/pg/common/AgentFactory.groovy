package org.pg.common

import org.pg.common.agents.DockerAgent
import org.pg.common.agents.NoAgent

class AgentFactory {
    def context
    def environment
    def stage
    def withAgent

    AgentFactory(context, environment, stage, withAgent) {
        this.context = context
        this.environment = environment
        this.stage = stage
        this.withAgent = withAgent
    }

    def getAgent() {
        if (this.withAgent) {
            return new DockerAgent(this.context, this.environment, this.stage)
        } else {
            return new NoAgent(this.context, this.environment, this.stage)
        }
    }

}
