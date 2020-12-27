package org.pg.stages

import org.pg.common.AgentFactory
import org.pg.common.Context
import org.pg.common.Log
import org.pg.common.agents.IAgent
import org.pg.common.slack.Slack

abstract class Base {
    def context
    String environment
    abstract String stage
    abstract Boolean skip = false
    Boolean failed = false
    abstract String slackMessage

    Base(environment) {
        this.context = Context.get()
        this.environment = environment
    }

    def execute() {
        IAgent agent = new AgentFactory(this.environment, this.stage).getAgent()
        agent.withSlave({
            try {
                this.context.stage("${stage}") {
                    Slack.sendMessage("", "running", this.slackMessage)
                    this.body()
                    Slack.sendMessage("", "success", this.slackMessage)
                }
            } catch (Exception e) {
                failed = true
                Slack.sendMessage("", "failed", this.slackMessage)
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