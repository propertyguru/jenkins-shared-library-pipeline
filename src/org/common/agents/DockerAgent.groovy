package org.common.agents

import com.cloudbees.groovy.cps.impl.CpsClosure
import org.common.Log
import org.common.StepExecutor

class DockerAgent implements IAgent {
    String image
    String args
    String label
    String environment

    DockerAgent(environment) {
        this.image = "pgjenkins:slave"
        this.args = "-v /etc/salt:/etc/salt -v /var/jenkins_home/.aws/:\$HOME/.aws/ " +
                "-v /var/run/docker.sock:/var/run/docker.sock -v \$HOME/.ssh:\$HOME/.ssh"
        this.environment = environment
        this.label = "env:new_${this.environment}"
    }

    def withSlave(body) {
        StepExecutor.node(this.label, {
            String hostname = StepExecutor.shWithOutput("hostname")
            this.args += " --hostname ${hostname}"
            StepExecutor.docker(this.image, this.args, body)
        })
    }

}
