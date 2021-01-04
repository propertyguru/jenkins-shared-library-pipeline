package org.pg.stages

import org.pg.common.Blueprint

class Build extends Base {
    String stage
    String description

    Build(environment) {
        super(environment)
        this.stage = "checkout & build"
        this.description = "Building the code"
    }

    def body() {
        this.step("checking out code from github", {
//            checkout()
        })
//        def deployPath = Blueprint.deployPath()
//        Log.info("Changing directory ${deployPath}")
//        this.context.dir(deployPath) {
//            this.context.stage('build') {
        this.step("Stashing dirs", {})
        this.step("Running pre-build steps from pgbuild.", {})
        this.step("Running build steps from pgbuild.", {})


//                stashDir("infra", "${Blueprint.appConfig()}/*.*")
//                stash("pgbuild", Blueprint.pgbuild())
//                PGbuild.instance.executeSteps("pre-build")
//                PGbuild.instance.executeSteps("build")
//            }

//            this.context.stage('unit tests') {
//                Log.info('unit tests')
//                PGbuild.instance.executeSteps('unit_tests')
//            }
//        }
    }

    private def checkout() {
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
