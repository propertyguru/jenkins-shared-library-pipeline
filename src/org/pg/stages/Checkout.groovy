package org.pg.stages

class Checkout extends Base {
    def stage

    Checkout(context, environment) {
        super(context, environment)
        this.stage = "checkout"
    }

    def body() {
        this.context.stage(this.stage) {
            def branch = "master"
            def repo = "" // write function
            def extensions = [
                    [$class: 'UserIdentity' , name:'Jenkins', email:'jenkins@propertyguru.com.sg'],
                    [$class: 'LocalBranch', localBranch: "**"],
                    [$class: 'CheckoutOption', timeout: 2],
                    [$class: 'CloneOption', depth: 0, honorRefspec: true, noTags: false, reference: '', shallow: false, timeout: 2],
                    [$class: 'CleanBeforeCheckout'],
                    [$class: 'PruneStaleBranch']
            ]

            this.context.checkout([
                    $class: 'GitSCM',
                    branches: [[name: "${branch}"]],
                    doGenerateSubmoduleConfigurations: false,
                    gitTool: "git",
                    extensions: extensions,
                    submoduleCfg: [],
                    userRemoteConfigs: [[
                            credentialsId: '054e1b3a-dae1-4921-8d44-ad7c5b3bed11',
                            refspec: "+refs/heads/*:refs/remotes/origin/* +refs/tags/*:refs/remotes/origin/*",
                            url: "${repo}"
                                        ]]
            ])
        }
    }

}
