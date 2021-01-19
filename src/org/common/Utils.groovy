package org.common

import com.cloudbees.groovy.cps.NonCPS
import com.cwctravel.hudson.plugins.extended_choice_parameter.ExtendedChoiceParameterDefinition
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

@NonCPS
def static toJson(data){
    return new JsonSlurper().parseText(data)
}

@NonCPS
def static toString(data) {
    return new JsonBuilder(data).toString()
}

def static checkBox(String name, String values, String defaultValue,
                    String description, String delimiter=',') {
    // default same as number of values
    Integer visibleItemCnt = values.split(',').size()
    return new ExtendedChoiceParameterDefinition(
            "${name}",
            "PT_CHECKBOX",
            "${values}",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "${defaultValue}",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            false,
            false,
            "visibleItemCnt",
            "${description}",
            "${delimiter}"
    )
}