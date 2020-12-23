package org.pg.stages

class Setup extends Base {
    def stage

    Setup(context, environment) {
        super(context, environment, false)
        this.stage = "setup"
    }

    def body() {
        def PipelineParams = [
                this.context.string(name: 'BRANCH', defaultValue: "master", description: 'Either put a branch name or tags/[tag-name]'),
                this.context.booleanParam(defaultValue: false, name: 'LOGLEVEL', description: 'Check this to enable debug logs'),
                this.context.booleanParam(defaultValue: true, name: 'TESTS', description: 'Check this to run api and UI tests')
        ]

        this.context.properties([
                [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '15', artifactNumToKeepStr: '15']],
                disableConcurrentBuilds(),
                parameters(PipelineParams)
        ])
    }

}
