package org.pg.stages

import org.pg.common.Blueprint

class Checkout extends Base {
    def stage

    Checkout(context, environment) {
        super(context, environment)
        this.stage = "checkout"
    }

    def body() {
        this.context.stage(this.stage) {
            Blueprint.load()
            def branch = this.context.BRANCH
            def repo = Blueprint.repository()
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
                    userRemoteConfigs: [[
                            credentialsId: 'github',
                            refspec: "+refs/heads/*:refs/remotes/origin/* +refs/tags/*:refs/remotes/origin/*",
                            url: "${repo}"
                                        ]],
                    submoduleCfg: [],
            ])
        }
    }

}
