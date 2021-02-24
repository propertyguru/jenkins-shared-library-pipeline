package org


import org.common.AgentFactory
import org.common.StepExecutor
import org.common.agents.IAgent
import org.stages.AnchoreScan
import org.stages.Build
import org.stages.Checkout
import org.stages.Deploy
import org.stages.Docker
import org.stages.Kong
import org.stages.PostDeploy
import org.stages.Sentry
import org.stages.Setup
import org.stages.Sonarqube
import org.stages.StaticContent

class Pipeline {

    private Map<String, IAgent> agents = [:]

    Pipeline() {
        agents["integration"] = new AgentFactory("integration").getAgent()
        agents["staging"] = new AgentFactory("staging").getAgent()
        agents["production"] = new AgentFactory("production").getAgent()
    }

    def execute() {
//        this.agents['integration'].withSlave({
//            new Setup().execute()
//            new Checkout().execute()
//            new Build().execute()
//            StepExecutor.parallel([
//                    "sonarqube"     : {
//                        new Sonarqube().execute()
//                    },
//                    "dockerimage"   : {
//                        new Docker().execute()
//                        new AnchoreScan().execute()
//                    },
//                    "static-content": {
//                        new StaticContent().execute()
//                    }
//            ])
//        })

//        for (String env in ["integration", "staging", "production"]) {
//            StepExecutor.timeout(7, "DAYS") {
//                StepExecutor.input("Deploy to ${env}?", "deploy-${env}", "Yes")
//            }
//            // TODO: maybe there is a better way to use the agent
//            agents[env].withSlave({
//                StepExecutor.parallel([
//                        "deploy": {
//                            new Deploy(env).execute()
//                        },
//                        "kong"  : {
//                            new Kong(env).execute()
//                        },
//                        "sentry": {
//                            new Sentry(env).execute()
//                        }
//                ])
//                new PostDeploy(env).execute()
//            })
//        }

//        // we are doing this because we only clone repositories on the integration slave.
//        this.integrationAgent.withSlave({
//            // setting up tags on git
//        })
    }

}
