import org.common.AgentFactory
import org.common.Blueprint
import org.common.BuildArgs
import org.common.Context
import org.common.Log
import org.common.StepExecutor
import org.slack.Slack
import org.stages.Build
import org.stages.Checkout
import org.stages.Docker
import org.stages.Input
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

    // few of our stages have a dependency on GIT_BRANCH and ENVIRONMENT variables.
    ENVIRONMENT = StepExecutor.setEnv("ENVIRONMENT", "")
    // github PR hook sets this!
    GIT_BRANCH = StepExecutor.env("ghprbSourceBranch")

    new AgentFactory("integration").getAgent().withSlave({
        Blueprint.load()
        Slack.setup()
        Boolean test = true
        new Checkout().execute(test)
        new Build().execute(test)
        StepExecutor.parallel([
                "sonarqube"     : {
                    new Sonarqube().execute(test)
                },
                "dockerimage"   : {
                    new Docker().execute(test)
                }
        ])
        new Input("PR tests passed! Do you want to merge the PR?",
                "pr_success",
                ["yes", "no"] as ArrayList<String>).execute()
        Log.info("MERGE THE PR SOMEHOW!!!")
    })
}