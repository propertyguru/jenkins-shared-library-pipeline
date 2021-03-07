import org.common.AgentFactory
import org.common.Blueprint
import org.common.Context
import org.common.Log
import org.common.StepExecutor
import org.stages.Build
import org.stages.Checkout
import org.stages.Deploy
import org.stages.Docker
import org.stages.Kong
import org.stages.PostDeploy
import org.stages.Sentry
import org.stages.Sonarqube
import org.stages.StaticContent

def call(body) {

    def PipelineParams = [
            string(
                    name: 'GIT_BRANCH',
                    defaultValue: "master",
                    description: 'Either put a branch name or tags/[tag-name]'
            ),
            booleanParam(
                    defaultValue: false,
                    name: 'HOTFIX',
                    description: 'Check this for hotfix deployment'
            ),
            booleanParam(
                    defaultValue: false,
                    name: 'LOGLEVEL',
                    description: 'Check this to enable debug logs'
            ),
            booleanParam(
                    defaultValue: true,
                    name: 'TESTS',
                    description: 'Check this to run api and UI tests'
            ),
            [
                    $class: 'WHideParameterDefinition',
                    defaultValue: '',
                    description: 'Slack ID of user initiating the job',
                    name: 'SLACK_ID'
            ]
    ]

    properties([
            [$class: 'JiraProjectProperty', siteName: 'https://propertyguru.atlassian.net/'],
            [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '15', artifactNumToKeepStr: '15']],
            disableConcurrentBuilds(),
            parameters(PipelineParams)
    ])

    Context.set(this)
    StepExecutor.setup()
    Log.setup()

    StepExecutor.currentBuild().changeSets.clear()

    new AgentFactory("integration").getAgent().withSlave({
        Blueprint.load()
        Slack.setup()
//        new Setup().execute()
        new Checkout().execute()
        new Build().execute()
        StepExecutor.parallel([
                "sonarqube"     : {
                    new Sonarqube().execute()
                },
                "dockerimage"   : {
                    new Docker().execute()
                },
                "static-content": {
                    new StaticContent().execute()
                }
        ])
    })

    ["integration", "staging", "production"].each { String env ->
        new AgentFactory(env).getAgent().withSlave({
//            StepExecutor.timeout(7, "DAYS") {
//                StepExecutor.input("Deploy to ${env}?", "deploy-${env}", "Yes")
//            }
            StepExecutor.parallel([
                "deploy": {
                    new Deploy(env).execute()
                },
                "kong"  : {
                    new Kong(env).execute()
                },
                "sentry": {
                    new Sentry(env).execute()
                }
            ])
            new PostDeploy(env).execute()
        })
    }

    // few final touchups!
    // we are doing this because we only clone repositories on the integration slave.
//    new AgentFactory("integration").getAgent().withSlave({
        // setting up tags on git
//    })

}