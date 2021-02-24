package org.common

import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

@NonCPS
static Map toJson(String data){
    return new JsonSlurper().parseText(data) as Map
}

@NonCPS
static String toString(data) {
    return new JsonBuilder(data).toString()
}
