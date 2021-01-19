package org


import org.common.AgentFactory
import org.common.agents.IAgent
import org.stages.AnchoreScan
import org.stages.Build
import org.stages.Checkout
import org.stages.Deploy
import org.stages.DockerImage
import org.stages.PostDeploy
import org.stages.Sonarqube
import org.stages.StaticContent

class Pipeline {

    IAgent agent

    Pipeline() {}

    def execute() {
        this.agent = new AgentFactory("integration").getAgent()
        this.agent.withSlave({
            new Checkout("integration").execute()
            new Build("integration").execute()
            new Sonarqube("integration").execute()
            new DockerImage("integration").execute()
            new StaticContent("integration").execute()
            new AnchoreScan("integration").execute()
        })

        ["integration", "staging", "production"].each{ String env ->
            this.agent = new AgentFactory(env).getAgent()
            this.agent.withSlave({
                new Deploy(env).execute()
                new PostDeploy(env).execute()
            })
        }

    }

}
