import org.pg.common.Parameters

def call(body) {
    def Parameters = [
        extendedChoice(
            defaultValue: 'integration',
            description: 'Select environments to deploy',
            multiSelectDelimiter: ',',
            name: 'ENVIRONMENT',
            quoteValue: false,
            saveJSONParameterToFile: false,
            type: 'PT_CHECKBOX',
            value: 'integration,staging,production',
            visibleItemCount: 10
        )
    ]

//    println(env.ENVIRONMENT)

//    properties([
//            [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator',
//                                                          numToKeepStr: '15', artifactNumToKeepStr: '15']],
//            disableConcurrentBuilds(),
//            parameters(PipelineParams)
//    ])
//
//    Log.setup(this)
//    BuildArgs.setup(this)
//    Blueprint.setup(this)
//    PGbuild.setup(this)
//    Git.setup(this)
//
//    // Clean up the changesets to remove jenkins-pipeline changesets
//    currentBuild.changeSets.clear()
//
//    //setting builddescription
//    if (!env.ghprbSourceBranch){
//        currentBuild.description = "Deploying in ${ENVIRONMENT}"
//    }
//
//    if (BuildArgs.component() == "guruland") {
//        (new GurulandBuild(this, "integration")).execute()
//    } else {
//        (new Build(this, "integration")).execute()
//    }
//
//    def envs = ['integration', 'staging', 'production']
//    envs.each {
//        (new Deploy(this, "${it}")).execute()
//        (new PostDeploy(this, "${it}")).execute()
//    }
}