package org.pg.interceptor

import org.pg.common.Log
import org.pg.interceptor.Base

class Approver extends Base {

    Approver(context) {
        super(context)
    }

    def before() {
        Log.info("Executing Approver interceptor before method")
    }

    def after() {
        Log.info("Executing Approver interceptor after method")
    }

    def always() {
        Log.info("Executing Approver interceptor always method")
    }
}
