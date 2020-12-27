package org.pg.stages

import org.pg.common.AgentFactory
import org.pg.common.Context
import org.pg.common.Log
import org.pg.common.agents.IAgent

abstract class Base {
    def context
    def environment
    abstract def stage
    abstract def skip = false
    def failed = false

    Base(environment) {
        this.context = Context.get()
        this.environment = environment
    }

    def execute() {
        IAgent agent = new AgentFactory(this.environment, this.stage).getAgent()
        agent.withSlave({
            try {
                this.context.stage("${stage}") {
                    this.body()
                }
            } catch (Exception e) {
                failed = true
                throw e
            } finally {
                Log.info("Running final block")
            }
        })
    }

    abstract def body()

//    def before() {
//        for (interceptor in interceptors) {
//            try {
//                interceptor.before()
//            } catch (Exception e) {
//                throw e
//            }
//        }
//    }
//
//    def after() {
//        for (interceptor in interceptors) {
//            try {
//                interceptor.after()
//            } catch (Exception e) {
//                throw e
//            }
//        }
//    }
//
//    def always() {
//        for (interceptor in interceptors) {
//            try {
//                interceptor.always()
//            } catch (Exception e) {
//                throw e
//            }
//        }
//    }
//
//    @NonCPS
//    @Override
//    Object invokeMethod(String name, Object args) {
//        if (name == "stage") {
//            try {
//                Log.info("Called invokeMethod, $name, $args")
//                metaClass.getMetaMethod('before').invoke(this)
//                metaClass.getMetaMethod(name, args).invoke(this, args)
//                metaClass.getMetaMethod('after').invoke(this)
//            } catch (Exception e) {
//                metaClass.getMetaMethod('always').invoke(this)
//                throw e
//            }
//        } else {
//            metaClass.getMetaMethod(name, args).invoke(this, args)
//        }
//    }

}