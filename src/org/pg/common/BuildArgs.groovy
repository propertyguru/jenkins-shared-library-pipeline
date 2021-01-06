package org.pg.common

@Singleton
class BuildArgs {
    private static def _context

    static void setup() {
        _context = Context.get()
    }

    static String jobName() {
        return _context.env.JOB_NAME
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

    static String jobType() {
        if (jobName().startsWith("devtools-")) {
            return jobName().split("/")[-1]
        } else if (jobName().startsWith("devtoolsqa-")) {
            return "qa"
        }
    }

    static String buildURL() {
        return _context.env.BUILD_URL
    }

    static String buildNumber() {
        return _context.env.BUILD_NUMBER
    }

    static String buildUser() {
        return "Prince Tyagi"
    }

    static Boolean isPRJob() {
        if (_context.env.ghprbSourceBranch) {
            return true
        }
        return false
    }

}
