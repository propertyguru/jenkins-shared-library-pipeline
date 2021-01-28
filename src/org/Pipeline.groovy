package org


import org.common.AgentFactory
import org.common.Log
import org.common.StepExecutor
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

    Pipeline() {}

    def execute() {
        this.agent = new AgentFactory("integration").getAgent()
        this.agent.withSlave({
            new Checkout().execute()
            new Build().execute()
            StepExecutor.parallel([
                    "sonarqube": {
                        new Sonarqube("integration").execute()
                    },
                    "dockerimage": {
                        new DockerImage("integration").execute()
                    },
                    "static-content": {
                        new StaticContent("integration").execute()
                    }
            ])
        })


        ["integration", "staging", "production"].each { String env ->
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
