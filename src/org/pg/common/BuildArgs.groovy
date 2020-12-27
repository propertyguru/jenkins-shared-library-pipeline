package org.pg.common

@Singleton
class BuildArgs {
    private static def context

    static def setup() {
        this.context = Context.get()
    }

    static def jobName() {
        return this.context.env.JOB_NAME
    }

    static def component() {
        def component
        def jobName = jobName()
        if (jobName.startsWith("devtools-")) {
            return jobName.split("/")[0].split('-')[1]
        } else if (jobName.startsWith("devtoolsqa-")) {
            return jobName.split("-")[1]
        }
    }

    static def subcomponent() {
        def subcomponent
        def jobName = jobName()
        if (jobName.startsWith("devtools-")) {
            return jobName.split("/")[1]
        } else if (jobName.startsWith("devtoolsqa-")) {
            return jobName.split("-")[2]
        }
    }

    static def jobType() {
        if (jobName().startsWith("devtools-")) {
            return jobName().split("/")[-1]
        } else if (jobName().startsWith("devtoolsqa-")) {
            return "qa"
        }
    }

}
