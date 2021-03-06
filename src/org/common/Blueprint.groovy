package org.common

@Singleton
class Blueprint {
    private static def data

    static String component() {
        return BuildArgs.component()
    }

    static String subcomponent() {
        return BuildArgs.subcomponent()
    }

    static String name() {
        return component() + "-" + subcomponent()
    }

    static ArrayList<String> teamEmails() {
        return ["prince@propertyguru.com.sg"]
        return data.get('team_emails', [])
    }

    static ArrayList<String> channels(String environment) {
        return ["prince-test"]
        String jobType = BuildArgs.jobType()
        if (jobType == "qa") {
            return ["alerts-qa-signoff"]
        }
        // if job type isnt qa, we send to different default channels, plus what they put in blueprints.
        ArrayList<String> channels = data.get('slack', [:]).get('channels', [:]).get(environment, [])
        // remove if duplicates
        return channels.unique()
    }

    static String repository() {
        def repo = data.get('deploy', {}).get('repository', '')
        return "git@${repo}.git"
    }

    static String deployPath() {
        return data.get('deploy', {}).get('path', "")
    }

    static String pgbuild() {
        return data.get('metadata', [:]).get('pgbuild', 'pgbuild.yaml')
    }

    static String appConfig() {
        return data.get('metadata', [:]).get('app_config', 'infra')
    }

    static String dockerfile() {
        return data.get('docker', [:]).get('file', 'Dockerfile')
    }

    static String cloud() {
        return data.get('metadata', [:]).get('cloud', 'aws')
    }

    static String dockerArgs() {
        def args = data.get('docker', [:]).get('args', [:])
        return args.collect { "--build-arg $it" }.join(" ")
    }

    static Boolean skipDeployment() {
        return data.get('metadata', [:]).get('skip_deployment', false)
    }

    static String staticContent() {
        return data.get('metadata', [:]).get('static_content', [:])
    }

    static String qaJob() {
        return data.get('metadata', [:]).get('qa-jobs', false)
    }

    static def load() {
        if (data == null) {
            try {
                (new Salt()).sync()
                data = (new Salt()).blueprint()
                data = data.local
            }
            catch(Exception e) {
                throw e
            }
        }
        return data
    }

}
