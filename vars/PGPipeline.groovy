import org.pg.Pipeline
import org.pg.common.Blueprint
import org.pg.common.BuildArgs
import org.pg.common.Log
import org.pg.common.slack.Slack

def call(body) {

    def PipelineParams = [
            string(
                    name: 'BRANCH',
                    defaultValue: "master",
                    description: 'Either put a branch name or tags/[tag-name]',
                    trim: true
            ),
            extendedChoice(
                    description: 'Select environments to deploy',
                    multiSelectDelimiter: ',',
                    name: 'ENVIRONMENT',
                    quoteValue: false,
                    saveJSONParameterToFile: false,
                    type: 'PT_MULTI_SELECT',
                    value: 'integration,staging,production',
                    visibleItemCount: 10
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
            [$class: 'WHideParameterDefinition', defaultValue: '', description: 'Slack ID of user initiating the job', name: 'SLACK_ID'],
            [$class: 'WHideParameterDefinition', defaultValue: '', description: 'Timestamp of the first message sent on slack', name: 'TIMESTAMP']
    ]

    properties([
            [$class: 'JiraProjectProperty', siteName: 'https://propertyguru.atlassian.net/'],
            [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '15', artifactNumToKeepStr: '15']],
            disableConcurrentBuilds(),
            parameters(PipelineParams)
    ])

    Log.setup(this)
    BuildArgs.setup(this)
    Blueprint.setup(this)
    Slack.setup(this)

    (new Pipeline(this)).execute()

}