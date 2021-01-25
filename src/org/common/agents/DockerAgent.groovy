package org.common.agents

import org.common.StepExecutor

class DockerAgent implements IAgent {
    String image
    String args
    String label
    String environment

    DockerAgent(environment) {
        this.image = "pgjenkins:slave1"
        this.args = "-u root -v /etc/salt:/etc/salt -v /var/jenkins_home/.aws/:/root/.aws/ " +
                "-v /var/run/docker.sock:/var/run/docker.sock -v \$HOME/.ssh:/root/.ssh"
        this.environment = environment
        this.label = "env:${this.environment}"
    }

    def withSlave(body) {
        StepExecutor.node(this.label, {
            StepExecutor.wrap([$class: 'TimestamperBuildWrapper'], {
                StepExecutor.wrap([$class: 'AnsiColorBuildWrapper'], {
                    StepExecutor.wrap([$class: 'BuildUser'], {
                        String hostname = StepExecutor.shWithOutput("hostname")
                        this.args += " --hostname ${hostname}"
                        StepExecutor.docker(this.image, this.args, body())
                    })
                })
            })
        })
    }

}
