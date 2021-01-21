package org.stages

import org.common.Blueprint
import org.common.BuildArgs

class PostDeploy extends Base {

    private String testParam
    private String branch
    private String countryParam
    private String typeParam
    private String environment

    PostDeploy(String environment) {
        super()
        this.environment = environment
        this.stage = "Gauge Tests - ${this.environment}"
        this.description = "Gauge Tests - ${this.environment}"
        this.testParam = "smoke,regression"
        if (this.environment == "production") {
            this.testParam = "smoke"
        }
        branch = "dev"
        countryParam = "SG,MY,MYB,ID,DD,DDE"
        typeParam = "api,ui,mobile-view,tablet"
    }

    @Override
    def body() {
        String testJobName = "devtoolsqa-${BuildArgs.name()}"
        this.step("Triggering test job: ${testJobName}", {
            this._context.build job: "${testJobName}", parameters: [
                    [$class: 'StringParameterValue', name: 'BRANCH', value: branch],
                    [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: this.environment],
                    [$class: 'StringParameterValue', name: 'COUNTRY', value: countryParam],
                    [$class: 'StringParameterValue', name: 'TEST', value: testParam],
                    [$class: 'StringParameterValue', name: 'TYPE', value: typeParam]
            ]
        })

    }

    @Override
    Boolean skip() {
        if (Blueprint.qaJob() && this.environment in this._context.ENVIRONMENT.tokenize(',')) {
            return false
        }
        return true
    }


    private void testUsersAPI() {
        this._context.build(
                job: "Users_API_TESTS",
                parameters: [[$class: 'StringParameterValue', name: 'TEST_ENVIRONMENT', value: this.environment]]
        )
    }

    private void testGuruland() {
        this._context.catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
            this._context.parallel (
                    "Validator tests": {
                        this._context.build job: "devtoolsqa-qa-validator", parameters: [
                                [$class: 'StringParameterValue', name: 'BRANCH', value: this.branch],
                                [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: this.environment],
                                [$class: 'StringParameterValue', name: 'COUNTRY', value: this.countryParam],
                                [$class: 'StringParameterValue', name: 'TEST', value: "${testParam}"],
                                [$class: 'StringParameterValue', name: 'TYPE', value: "api,ui"]
                        ]
                    },
                    "Mobile API tests": {
                        this._context.build job: "devtoolsqa-qa-mobilevalidator", parameters: [
                                [$class: 'StringParameterValue', name: 'BRANCH', value: branch],
                                [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: this.environment],
                                [$class: 'StringParameterValue', name: 'COUNTRY', value: this.countryParam],
                                [$class: 'StringParameterValue', name: 'TEST', value: "${testParam}"],
                                [$class: 'StringParameterValue', name: 'TYPE', value: "api,ui"]
                        ]
                    }
            )
        }
    }
}