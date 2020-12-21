package org.pg.interceptor

abstract class Base {
    def context

    Base(context) {
        this.context = context
    }

    abstract def before()
    abstract def after()
    abstract def always()
}
