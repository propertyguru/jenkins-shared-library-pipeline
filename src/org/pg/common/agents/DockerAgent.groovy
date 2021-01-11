package org.pg.common.agents

import org.pg.common.Context

class DockerAgent implements IAgent {
    def context
    String image
    String args
    String label
    String environment

    DockerAgent(environment) {
        this.context = Context.get()
        this.image = "pgjenkins:slave1"
        this.args = "-u root -v /etc/salt:/etc/salt -v /var/jenkins_home/.aws/:/root/.aws/ " +
                "-v /var/run/docker.sock:/var/run/docker.sock -v \$HOME/.ssh:/root/.ssh"
        this.environment = environment
        this.label = "env:${this.environment}"
    }

    def withSlave(body) {
        this.context.node(this.label) {
            this.context.wrap([$class: 'TimestamperBuildWrapper']) {
                this.context.wrap([$class: 'AnsiColorBuildWrapper']) {
                    this.context.wrap([$class: 'BuildUser']) {
                        def hostname = this.context.sh(returnStdout: true, script: "hostname")
                        this.args += " --hostname ${hostname}"

                        this.context.docker.image(this.image).inside(this.args) {
                            body()
                        }
                    }
                }
            }
        }
    }

}
