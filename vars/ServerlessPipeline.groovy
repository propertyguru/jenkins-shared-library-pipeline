import org.pg.common.Log
import org.pg.common.Parameters
import org.pg.stages.Serverless

def call(body) {
    def params = new Parameters(this)

    properties([
        [$class: 'BuildDiscarderProperty', strategy: [
                $class: 'LogRotator', numToKeepStr: '15', artifactNumToKeepStr: '15']
        ],
        disableConcurrentBuilds(),
        parameters([
                params.choice('ENVIRONMENT', 'integration,staging,production',
                        'integration', 'Select environments to deploy')
        ])
    ])

    Log.setup(this)

    "integration,staging,production".tokenize(',').each { env ->
        (new Serverless(this, "$env")).execute()
    }
}