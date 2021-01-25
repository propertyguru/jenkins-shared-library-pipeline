package org.common

/*
Can't use Log class in StepExecutor because Log class hasnt been setup yet.
 */

@Singleton
class StepExecutor implements Serializable {
    private static def _context

    static void setup() {
        _context = Context.get()
    }

    static Boolean fileExists(String filename) {
        return _context.fileExists(filename)
    }

    static void stage(String name, def body) {
        _context.stage(name) {
            body()
        }
    }

    static void step(def body) {
        _context.step(body)
    }

    static void error(String msg) {
        _context.error(msg)
    }

    static void ansiColor(String shell, def body) {
        _context.ansiColor(shell) {
            body()
        }
    }

    static void println(String text) {
        _context.println text
    }

    static void dir(String path, def body) {
        _context.dir(path) {
            body()
        }
    }

    static void sh(String cmd) {
        _context.sh(cmd)
    }

    static String shWithOutput(String cmd) {
        _context.sh(returnStdout: true, script: cmd)
    }

    static void writeFile(String filename, String text) {
        _context.writeFile(file: filename, text: text)
    }

    static void anchore(String file) {
        String anchore_url = 'http://anchore.guruestate.com/v1'
        _context.anchore(
                url: anchore_url,
                engineCredentialsId: 'anchore',
                name: file,
                engineRetries: "900",
                bailOnFail: false,
                bailOnPluginFail: false
        )
    }

    static LinkedHashMap readYaml(String filename) {
        return _context.readYaml(file: filename)
    }

    static void checkout(String repo, ArrayList extensions) {
        _context.checkout([
                $class: 'GitSCM',
                branches: [[name: "${_context.BRANCH}"]],
                doGenerateSubmoduleConfigurations: false,
                gitTool: "git",
                extensions: extensions,
                userRemoteConfigs: [[
                        credentialsId: 'github',
                        refspec: "+refs/heads/*:refs/remotes/origin/* +refs/tags/*:refs/remotes/origin/*",
                        url: repo
                ]],
                submoduleCfg: [],
        ])
    }

    static void retry(Integer times, def body) {
        _context.retry(times) {
            body()
        }
    }

    static void build(String job, ArrayList parameters) {
        _context.build(job: job, parameters: parameters)
    }

    static void stash(String name, String path) {
        if (path == null){
            path = name
        }
        _context.stash(allowEmpty: true, name: "${name}", includes: "${path}")
    }

    static void unstash(String name) {
        _context.unstash(name: name)
    }

    static void node(String label, def body) {
        _context.node(label) {
            wrap([$class: 'TimestamperBuildWrapper'], {
                wrap([$class: 'AnsiColorBuildWrapper'], {
                    wrap([$class: 'BuildUser'],
                            body()
                    )
                })
            })
        }
    }

    static void wrap(Map wrapper, def body) {
        _context.wrap(wrapper) {
            body()
        }
    }

    static void docker(String image, String args, def body) {
        _context.docker.image(image).inside(args) {
            body()
        }
    }

    static String slackUserIdFromEmail(String email) {
        return _context.slackUserIdFromEmail(email: email, tokenCredentialId: 'slack-bot-token')
    }

    static void slackUploadFile(String channel, String path) {
        _context.slackUploadFile(
                channel: channel,
                filePath: path
        )
    }

    static void slackSend(String channel, ArrayList blocks, String timestamp="", String attachments=null) {
        _context.slackSend(
                channel: channel,
                timestamp: timestamp,
                blocks: blocks,
                tokenCredentialId: 'slack-bot-token',
                attachments: attachments
        )
    }

    static void archiveArtifacts(String artifacts) {
        _context.archiveArtifacts(allowEmptyArchive: true, artifacts: artifacts, fingerprint: true)
    }

    static void emailext(String recipients, String subject, String body=null, String attachment=null) {
        if (body == null) {
            body = '''${SCRIPT, template="groovy-html.template"}'''
        }
        _context.emailext(
                body: body,
                attachmentsPattern: attachment,
                mimeType: 'text/html',
                subject: subject,
                to: recipients,
                recipientProviders: [_context.requestor()]
        )
    }

    static void withUsernamePassword(String credentialsID, String user, String password, def body) {
        _context.withCredentials([
            _context.usernamePassword(credentialsId: credentialsID, usernameVariable: user, passwordVariable: password) {
                body()
            }
        ])
    }

    static void parallel(Map branches) {
        _context.parallel branches
    }

    static void catchError(String buildResult, String stageResult, def body) {
        _context.catchError(buildResult: buildResult, stageResult: stageResult){
            body()
        }
    }

    static String env(String name) {
        return _context.env.getProperty(name)
    }

    static void setEnv(String name, String value) {
        _context.env.setProperty(name, value)
    }

    static Map currentBuild() {
        return _context.currentBuild
    }

}