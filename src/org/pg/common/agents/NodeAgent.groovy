package org.pg.common.agents

class NodeAgent implements IAgent {
    def context
    def environment
    def stage
    def label

    NodeAgent(context, environment, stage) {
        this.context = context
        this.environment = environment
        this.stage = stage
        this.label = "android-slave-01"
    }

    def withSlave(body) {
        this.context.node(this.label) {
            this.context.wrap([$class: 'TimestamperBuildWrapper']) {
                this.context.wrap([$class: 'AnsiColorBuildWrapper']) {
                    this.context.wrap([$class: 'BuildUser']) {
                        body()
                    }
                }
            }
        }
    }

}
