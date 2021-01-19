package org.stages

class Setup extends Base {
    String stage

    Setup(context) {
        super(context)
        this.stage = "setup"
    }

    @Override
    def body() {
        def PipelineParams = [
                this._context.string(name: 'BRANCH', defaultValue: "master", description: 'Either put a branch name or tags/[tag-name]'),
                this._context.booleanParam(defaultValue: false, name: 'LOGLEVEL', description: 'Check this to enable debug logs'),
                this._context.booleanParam(defaultValue: true, name: 'TESTS', description: 'Check this to run api and UI tests')
        ]

        this._context.properties([
                [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '15', artifactNumToKeepStr: '15']],
                this._context.disableConcurrentBuilds(),
                this._context.parameters(PipelineParams)
        ])
    }

    @Override
    Boolean skip() {
        return null
    }
}
