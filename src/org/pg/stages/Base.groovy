package org.pg.stages

import com.cloudbees.groovy.cps.NonCPS
import org.pg.common.Log
import org.pg.interceptor.Approver
import org.pg.interceptor.SlackNotifier

abstract class Base implements GroovyInterceptable {
    def context
    def environment
    def interceptors

    Base(context, environment) {
        this.context = context
        this.environment = environment
        this.interceptors = [
            new Approver(this.context),
            new SlackNotifier(this.context)
        ]
    }

    def execute() {
        Log.info("Executing Serverless execute function! " + environment)
        // select agent/node
        stage()
    }

    abstract def stage()

    def before() {
        for (interceptor in interceptors) {
            try {
                interceptor.before()
            } catch (Exception e) {
                throw e
            }
        }
    }

    def after() {
        for (interceptor in interceptors) {
            try {
                interceptor.after()
            } catch (Exception e) {
                throw e
            }
        }
    }

    def always() {
        for (interceptor in interceptors) {
            try {
                interceptor.always()
            } catch (Exception e) {
                throw e
            }
        }
    }

    @NonCPS
    @Override
    Object invokeMethod(String name, Object args) {
        if (name == "stage") {
            try {
                Log.info("Called invokeMethod, $name, $args")
                metaClass.getMetaMethod('before').invoke(this)
                metaClass.getMetaMethod(name, args).invoke(this, args)
                metaClass.getMetaMethod('after').invoke(this)
            } catch (Exception e) {
                metaClass.getMetaMethod('always').invoke(this)
                throw e
            }
        } else {
            metaClass.getMetaMethod(name, args).invoke(this, args)
        }
    }

}