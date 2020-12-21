package org.pg.interceptor

import org.pg.common.Log

class SlackNotifier extends Base {
    SlackNotifier(context) {
        super(context)
    }

    def before() {
        Log.info("Executing SlackNotifier interceptor before method")
    }

    def after() {
        Log.info("Executing SlackNotifier interceptor after method")
    }

    def always() {
        Log.info("Executing SlackNotifier interceptor always method")
    }
}
