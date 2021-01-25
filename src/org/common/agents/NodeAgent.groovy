package org.common.agents

import org.common.StepExecutor

class NodeAgent implements IAgent {
    String environment
    String label

    NodeAgent(environment) {
        this.environment = environment
        this.label = "android-slave-01"
    }

    def withSlave(def body) {
        StepExecutor.node(this.label, body)
    }

}
