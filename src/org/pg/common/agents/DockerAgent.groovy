package org.pg.common.agents

class DockerAgent implements IAgent {
    def context
    def image
    def args
    def label
    def environment
    def stage

    DockerAgent(context, environment, stage) {
        this.context = context
        this.image = "pgjenkins:slave1"
        this.args = "-u root"
        this.environment = environment
        this.stage = stage
        this.label = "env:${this.environment}"
    }

    def withSlave(body) {
        this.context.node(this.label) {
            this.context.wrap([$class: 'TimestamperBuildWrapper']) {
                this.context.wrap([$class: 'AnsiColorBuildWrapper']) {
                    this.context.wrap([$class: 'BuildUser']) {
                        this.context.docker.image(this.image).inside(this.args) {
                            body()
                        }
                    }
                }
            }
        }
    }

}
