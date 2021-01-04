package org.pg.common

@Singleton
class BuildArgs {
    private static def context

    static void setup() {
        context = Context.get()
    }

    static String jobName() {
        return context.env.JOB_NAME
    }

    static String component() {
        def jobName = jobName()
        if (jobName.startsWith("devtools-")) {
            return jobName.split("/")[0].split('-')[1]
        } else if (jobName.startsWith("devtoolsqa-")) {
            return jobName.split("-")[1]
        }
    }

    static String subcomponent() {
        def jobName = jobName()
        if (jobName.startsWith("devtools-")) {
            return jobName.split("/")[1]
        } else if (jobName.startsWith("devtoolsqa-")) {
            return jobName.split("-")[2]
        }
    }

    static String jobType() {
        if (jobName().startsWith("devtools-")) {
            return jobName().split("/")[-1]
        } else if (jobName().startsWith("devtoolsqa-")) {
            return "qa"
        }
    }

    static String buildURL() {
        return context.env.BUILD_URL
    }

    static String buildUser() {
        return "Prince Tyagi"
    }

}
