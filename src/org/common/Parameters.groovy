package org.common

class Parameters {
    def context

    Parameters(context) {
        this.context = context
    }

    def choice(name, values, defaultValue, description) {
        return this.context.extendedChoice(
            defaultValue: defaultValue,
            description: description,
            multiSelectDelimiter: ',',
            name: name,
            quoteValue: false,
            saveJSONParameterToFile: false,
            type: 'PT_CHECKBOX',
            value: values,
            visibleItemCount: 10
        )
    }
}
