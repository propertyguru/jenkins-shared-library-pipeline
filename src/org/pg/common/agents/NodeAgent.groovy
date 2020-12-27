package org.pg.common.agents

import org.pg.common.Context

class NodeAgent implements IAgent {
    def context
    def environment
    def stage
    def label

    NodeAgent(environment, stage) {
        this.context = Context.get()
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
