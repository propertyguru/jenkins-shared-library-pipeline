import org.Pipeline
import org.common.Blueprint
import org.common.BuildArgs
import org.common.Context
import org.common.Git
import org.common.Log
import org.common.PGbuild
import org.common.StepExecutor
import org.common.slack.Slack
import org.jenkinsci.plugins.workflow.steps.Step

def call(body) {

    def PipelineParams = [
            string(
                    name: 'BRANCH',
                    defaultValue: "master",
                    description: 'Either put a branch name or tags/[tag-name]'
            ),
            [
                    $class: 'ExtendedChoiceParameterDefinition',
                    name: 'ENVIRONMENT',
                    value: 'integration,staging,production',
                    type: 'PT_CHECKBOX',
                    defaultValue: 'integration',
                    description: 'Select environments to deploy',
                    multiSelectDelimiter: ",",
                    visibleItemCount: 3
            ],
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
    Slack.setup()

    StepExecutor.currentBuild().changeSets.clear()

    (new Pipeline()).execute()

//    def text = libraryResource('resources/default_values.yaml')
//    Log.error(text)
//    def a = new YamlSlurper().parseText(text)
//    println(a)
//    def jobs = a['pipeline']['JOBS']
//    println(jobs)
//    println('guruland-guruland' in jobs)

}