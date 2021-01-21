package org


import org.common.AgentFactory
import org.common.Context
import org.common.agents.IAgent
import org.stages.AnchoreScan
import org.stages.Build
import org.stages.Checkout
import org.stages.Deploy
import org.stages.DockerImage
import org.stages.Kong
import org.stages.PostDeploy
import org.stages.Sentry
import org.stages.Sonarqube
import org.stages.StaticContent

class Pipeline {

    IAgent agent
    private def _context

    Pipeline() {
        this._context = Context.get()
    }

    def execute() {
        this.agent = new AgentFactory("integration").getAgent()
        this.agent.withSlave({
            new Checkout().execute()
            new Build().execute()
            new Sonarqube("integration").execute()
            new DockerImage("integration").execute()
            new StaticContent("integration").execute()
//            new AnchoreScan().execute()
        })

        ["integration", "staging", "production"].each{ String env ->
            this.agent = new AgentFactory(env).getAgent()
            this.agent.withSlave({
                new Deploy(env).execute()
                new Kong(env).execute()
                new Sentry(env).execute()
                new PostDeploy(env).execute()
            })
        }

    }

}
