package org.pg

import org.pg.common.AgentFactory
import org.pg.common.agents.IAgent
import org.pg.stages.Build
import org.pg.stages.Checkout
import org.pg.stages.Deploy
import org.pg.stages.ImageScan

class Pipeline {

    IAgent agent

    Pipeline() {}

    def execute() {
        this.agent = new AgentFactory("integration").getAgent()
        this.agent.withSlave({
            new Checkout("integration").execute()
            new Build("integration").execute()
            new ImageScan("integration").execute()
            new Deploy("integration").execute()
        })

        this.agent = new AgentFactory("staging").getAgent()
        this.agent.withSlave({
            new Deploy("staging").execute()
        })

        this.agent = new AgentFactory("production").getAgent()
        this.agent.withSlave({
            new Deploy("production").execute()
        })
    }

}
