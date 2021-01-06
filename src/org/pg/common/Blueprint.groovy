package org.pg.common

@Singleton
class Blueprint {
    private static def _context
    private static def data

    static void setup() {
        _context = Context.get()
    }

    static def component() {
        return BuildArgs.component()
    }

    static def subcomponent() {
        return BuildArgs.subcomponent()
    }

    static String repository() {
        def repo = data.get('deploy', {}).get('repository', '')
        return "git@${repo}.git"
    }

    static String deployPath() {
        return data.get('deploy', {}).get('path', '.')
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
