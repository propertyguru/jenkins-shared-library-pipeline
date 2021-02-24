package org

import org.common.AgentFactory
import org.common.BuildArgs
import org.common.StepExecutor
import org.common.agents.IAgent
import org.stages.Setup
import org.stages.gauge.GaugeBuild

class GaugePipeline {
    private Map<String, IAgent> agents = [:]

    GaugePipeline() {
        agents["integration"] = new AgentFactory("integration").getAgent()
        agents["staging"] = new AgentFactory("staging").getAgent()
        agents["production"] = new AgentFactory("production").getAgent()
    }

    def execute() {
        ["COUNTRY", "TYPE", "TEST", "BRANCH"].each { String env ->
            if (StepExecutor.env(env) == "") {
                StepExecutor.error("${env} not passed!")
            }
        }
        if ('regression' in TEST.tokenize(',') && StepExecutor.env("ENVIRONMENT") == "production") {
            StepExecutor.error("You cannot run regression tests in production environment.")
        }
        if (BuildArgs.name().contains("ondemand") && StepExecutor.env("TAGS") == "") {
            StepExecutor.error("OnDemand job should have tags")
        }

        new Setup().execute()
        new GaugeBuild().execute()

    }
}
