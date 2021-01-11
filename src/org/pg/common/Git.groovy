package org.pg.common

@Singleton
class Git {

    private static def _context

    static void setup() {
        _context = Context.get()
    }

    static void checkout() {
        Blueprint.load()
        def branch = _context.BRANCH
        def repo = Blueprint.repository()
        println("repo is: ${repo}")
        def extensions = [
                [$class: 'UserIdentity' , name:'Jenkins', email:'jenkins@propertyguru.com.sg'],
                [$class: 'LocalBranch', localBranch: "**"],
                [$class: 'CheckoutOption', timeout: 5],
                [$class: 'CloneOption', depth: 0, honorRefspec: true, noTags: false, reference: '', shallow: false, timeout: 5],
                [$class: 'CleanBeforeCheckout'],
                [$class: 'PruneStaleBranch']
        ]

        _context.retry(3) {
            _context.checkout([
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
