import org.common.AgentFactory
import org.common.Blueprint
import org.common.BuildArgs
import org.common.Context
import org.common.Log
import org.common.StepExecutor
import org.common.slack.Slack
import org.stages.Build
import org.stages.Checkout
import org.stages.Docker
import org.stages.Setup
import org.stages.Sonarqube

def call(body) {

    properties([
            [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '15', artifactNumToKeepStr: '15']],
            disableConcurrentBuilds()
    ])

    Context.set(this)
    StepExecutor.setup()
    Log.setup()
    Slack.setup()

    // we are using this variable for BRANCH because github PR also sends the same name
    GIT_BRANCH = StepExecutor.env("ghprbSourceBranch")
    ENVIRONMENT = StepExecutor.setEnv("ENVIRONMENT", "")

    new AgentFactory("integration").getAgent().withSlave({
        new Setup().execute()
        new Checkout().execute()
        new Build().execute()
        StepExecutor.parallel([
                "sonarqube"     : {
                    new Sonarqube().execute()
                },
                "dockerimage"   : {
                    new Docker().execute()
                }
        ])
    })
}