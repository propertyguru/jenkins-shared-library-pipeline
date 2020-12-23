import org.pg.Pipeline
import org.pg.common.BuildArgs

def call(body) {


//        def envParam = PGUtils.checkBox("ENVIRONMENT", // name
//                "integration,staging,production", // values
//                "integration", //default value
//                0, //visible item cnt
//                "Select environments to deploy")

    def PipelineParams = [
//                string(name: 'BRANCH', defaultValue: "master", description: 'Either put a branch name or tags/[tag-name]'),
//                envParam,
//                booleanParam(defaultValue: false, name: 'LOGLEVEL', description: 'Check this to enable debug logs'),
//                booleanParam(defaultValue: true, name: 'TESTS', description: 'Check this to run api and UI tests')
    ]

    properties([
            [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '15', artifactNumToKeepStr: '15']],
            disableConcurrentBuilds(),
            parameters(PipelineParams)
    ])

    BuildArgs.setup(this)
    new Pipeline(this).execute()

}