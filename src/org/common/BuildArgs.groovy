package org.common

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

    static String appname() {
        return "pg_${component()}_${subcomponent()}"
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

    static Integer buildNumber() {
        return _context.env.BUILD_NUMBER as Integer
    }

    static String buildUser() {
        return "Prince Tyagi"
    }

    static ArrayList<String> getEnvParam() {
        return _context.ENVIRONMENT.tokenize(',')
    }

    static Boolean isPRJob() {
        if (_context.env.ghprbSourceBranch) {
            return true
        }
        return false
    }

}
