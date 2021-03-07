import org.GaugePipeline
import org.common.BuildArgs
import org.common.Context
import org.common.Log
import org.common.StepExecutor
import org.slack.Slack

def call(body) {

    Context.set(this)
    StepExecutor.setup()
    Log.setup()
    Slack.setup()

    def PipelineParams = [
            string(
                    name: 'BRANCH',
                    defaultValue: "dev",
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
            [
                    $class: 'ExtendedChoiceParameterDefinition',
                    name: 'COUNTRY',
                    value: 'SG,MY,MYB,ID,DD,DDE',
                    type: 'PT_CHECKBOX',
                    defaultValue: 'SG,MY,MYB,ID,DD,DDE',
                    description: 'Select country',
                    multiSelectDelimiter: ",",
                    visibleItemCount: 5
            ],
            [
                    $class: 'ExtendedChoiceParameterDefinition',
                    name: 'TEST',
                    value: 'smoke,regression',
                    type: 'PT_CHECKBOX',
                    defaultValue: 'smoke',
                    description: "Select test",
                    multiSelectDelimiter: ",",
                    visibleItemCount: 2
            ],
            [
                    $class: 'ExtendedChoiceParameterDefinition',
                    name: 'TYPE',
                    value: 'api,ui,mobile-view,tablet',
                    type: 'PT_CHECKBOX',
                    defaultValue: 'api,ui',
                    description: "Select type",
                    multiSelectDelimiter: ",",
                    visibleItemCount: 4
            ],
            booleanParam(
                    defaultValue: false,
                    name: 'LOGLEVEL',
                    description: 'Check this to enable debug logs'
            )
    ]

    if (BuildArgs.name().contains("ondemand")) {
        PipelineParams = [
                string(
                        name: 'TAGS',
                        defaultValue: '',
                        description: 'Tags to run specific test. Ex: NAME=abc&FEATURE=def'
                ),
                string(
                        name: 'SERVICE',
                        defaultValue: 'guruland-guruland',
                        description: 'SERVICE name is not mandatory'
                )
        ] + PipelineParams
    }

    properties([
            [$class: 'JiraProjectProperty', siteName: 'https://propertyguru.atlassian.net/'],
            [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '15', artifactNumToKeepStr: '15']],
            disableConcurrentBuilds(),
            parameters(PipelineParams)
    ])

    StepExecutor.currentBuild().changeSets.clear()

    (new GaugePipeline()).execute()

//    def text = libraryResource('resources/default_values.yaml')
//    Log.error(text)
//    def a = new YamlSlurper().parseText(text)
//    println(a)
//    def jobs = a['pipeline']['JOBS']
//    println(jobs)
//    println('guruland-guruland' in jobs)

}