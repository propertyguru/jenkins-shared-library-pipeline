package org.stages

import org.common.Blueprint
import org.common.BuildArgs
import org.common.Log
import org.common.StepExecutor

import java.lang.reflect.Array

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
        this.step("Triggering tests", {
            // TODO: is this catchError needed??
//            StepExecutor.catchError("SUCCESS", "FAILURE", {
            if (BuildArgs.name() == "guruland-guruland") {
                testGuruland()
            } else if (BuildArgs.name() == "users-api") {
                testUsersAPI()
            } else {
                testService()
            }
//            })
        })
    }

    @Override
    Boolean skip() {
        if (Blueprint.qaJob() && StepExecutor.env('ENVIRONMENT').tokenize(',')) {
            return false
        }
        return true
    }

    private void testService() {
        String testJobName = "devtoolsqa-${BuildArgs.name()}"
        ArrayList parameters = [
                [$class: 'StringParameterValue', name: 'BRANCH', value: branch],
                [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: this.environment],
                [$class: 'StringParameterValue', name: 'COUNTRY', value: countryParam],
                [$class: 'StringParameterValue', name: 'TEST', value: testParam],
                [$class: 'StringParameterValue', name: 'TYPE', value: typeParam]
        ]
        StepExecutor.build(testJobName, parameters)
    }

    private void testUsersAPI() {
        ArrayList parameters = [
                [$class: 'StringParameterValue', name: 'TEST_ENVIRONMENT', value: this.environment]
        ]
        StepExecutor.build("Users_API_TESTS", parameters)
    }

    private void testGuruland() {
        StepExecutor.parallel([
            "Validator tests": {
                ArrayList parameters = [
                        [$class: 'StringParameterValue', name: 'BRANCH', value: this.branch],
                        [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: this.environment],
                        [$class: 'StringParameterValue', name: 'COUNTRY', value: this.countryParam],
                        [$class: 'StringParameterValue', name: 'TEST', value: "${testParam}"],
                        [$class: 'StringParameterValue', name: 'TYPE', value: "api,ui"]
                ]
                StepExecutor.build("devtoolsqa-qa-validator", parameters)
            },
            "Mobile API tests": {
                ArrayList parameters = [
                        [$class: 'StringParameterValue', name: 'BRANCH', value: branch],
                        [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: this.environment],
                        [$class: 'StringParameterValue', name: 'COUNTRY', value: this.countryParam],
                        [$class: 'StringParameterValue', name: 'TEST', value: "${testParam}"],
                        [$class: 'StringParameterValue', name: 'TYPE', value: "api,ui"]
                ]
                StepExecutor.build("devtoolsqa-qa-mobilevalidator", parameters)
            }
        ])
        StepExecutor.parallel([
            "Validator tests": {
                ArrayList parameters = [
                        [$class: 'StringParameterValue', name: 'BRANCH', value: this.branch],
                        [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: this.environment],
                        [$class: 'StringParameterValue', name: 'COUNTRY', value: this.countryParam],
                        [$class: 'StringParameterValue', name: 'TEST', value: "${testParam}"],
                        [$class: 'StringParameterValue', name: 'TYPE', value: "api,ui"]
                ]
                StepExecutor.build("devtoolsqa-qa-validator", parameters)
            },
            "Mobile API tests": {
                ArrayList parameters = [
                        [$class: 'StringParameterValue', name: 'BRANCH', value: branch],
                        [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: this.environment],
                        [$class: 'StringParameterValue', name: 'COUNTRY', value: this.countryParam],
                        [$class: 'StringParameterValue', name: 'TEST', value: "${testParam}"],
                        [$class: 'StringParameterValue', name: 'TYPE', value: "api,ui"]
                ]
                StepExecutor.build("devtoolsqa-qa-mobilevalidator", parameters)
            }
        ])
    }
}