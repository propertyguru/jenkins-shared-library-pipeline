package org.pg.stages

import org.pg.common.Log

class Serverless extends Base{

    Serverless(context, environment) {
        super(context, environment)
    }

    def body() {
        println("I am going to do something but i dont know what!")
        Log.info("Inside body of Serverless stage!")
    }

}
