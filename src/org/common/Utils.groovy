package org.common

import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

@NonCPS
def static toJson(data){
    return new JsonSlurper().parseText(data)
}

@NonCPS
static String toString(data) {
    return new JsonBuilder(data).toString()
}
