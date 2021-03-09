package org.common

@Singleton
class BuildArgs {

    static String jobName() {
        return StepExecutor.env('JOB_NAME')
    }

    static String component(){
        if (jobName().startsWith("devtools-")) {
            return jobName().split("/")[0].split('-')[1]
        } else if (jobName().startsWith("devtoolsqa-")) {
            return jobName().split("-")[1]
        }
    }

    static String subcomponent(){
        if (jobName().startsWith("devtools-")) {
            return jobName().split("/")[1]
        } else if (jobName().startsWith("devtoolsqa-")) {
            return jobName().split("-")[2]
        }
    }

    static def name() {
        return "${component()}-${subcomponent()}"
    }

    static String appname() {
        return "pg_${component()}_${subcomponent()}"
    }

    static String jobType() {
        return buildURL().split('/')[-2]
    }

    static String buildURL() {
        return StepExecutor.env('BUILD_URL')
    }

    static Integer buildNumber() {
        return StepExecutor.env('BUILD_NUMBER') as Integer
    }

    static String buildUser() {
        return "Prince Tyagi"
    }

    static Boolean isPRJob() {
        if (StepExecutor.env('ghprbSourceBranch')) {
            return true
        }
        return false
    }

}
