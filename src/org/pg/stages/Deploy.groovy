package org.pg.stages

class Deploy extends Base {
    def stage

    Deploy(context, environment) {
        super(context, environment)
        this.stage = "deploy"
    }

    def body() {

    }

}
