package org.pg.common.slack

@Singleton
class Block {
    static def divider() {
        return [
                "type": "divider"
        ]
    }

    static def header(text) {
        return [
                "type": "header",
                "text": [
                        "type" : "plain_text",
                        "text" : text,
                        "emoji": true
                ]
        ]
    }

    static def markdown(text) {
        return [
                "type": "section",
                "text": [
                        "type": "mrkdwn",
                        "text": text
                ]
        ]
    }

    static def static_select(text, placeholder, action_id) {
        def options = []
        for (svc in ['a', 'b', 'c']) [
                options.add([
                        "text" : [
                                "type" : "plain_text",
                                "text" : svc,
                                "emoji": true
                        ],
                        "value": svc
                ])
        ]

        return [
                "type"     : "section",
                "block_id" : action_id,
                "text"     : [
                        "type": "mrkdwn",
                        "text": text
                ],
                "accessory": [
                        "type"       : "static_select",
                        "placeholder": [
                                "type" : "plain_text",
                                "text" : placeholder,
                                "emoji": true
                        ],
                        "options"    : options,
                        "action_id"  : action_id
                ]
        ]
    }

    static def multi_static_select(text, placeholder, action_id) {
        def options = []
        for (svc in ['a']) {
            options.add([
                    "text" : [
                            "type" : "plain_text",
                            "text" : svc,
                            "emoji": true
                    ],
                    "value": svc
            ])
        }

        return [
                "type"    : "input",
                "block_id": action_id,
                "label"   : [
                        "type" : "plain_text",
                        "text" : text,
                        "emoji": true
                ],
                "element" : [
                        "type"       : "multi_static_select",
                        "placeholder": [
                                "type" : "plain_text",
                                "text" : placeholder,
                                "emoji": true
                        ],
                        "options"    : options,
                        "action_id"  : action_id
                ]
        ]
    }

    static def plain_text_input(text, action_id) {
        return [
                "type"    : "input",
                "block_id": action_id,
                "label"   : [
                        "type" : "plain_text",
                        "text" : text,
                        "emoji": true
                ],
                "element" : [
                        "type"     : "plain_text_input",
                        "multiline": false,
                        "action_id": action_id
                ],
                "optional": false
        ]
    }

    static def radio_buttons(text, opts, action_id) {
        def options = []
        opts.indexed().collect { idx, opt ->
            options.add([
                    "text" : [
                            "type" : "plain_text",
                            "text" : opt,
                            "emoji": true
                    ],
                    "value": opt
            ])
        }

        return [
                "type"    : "input",
                "block_id": action_id,
                "label"   : [
                        "type" : "plain_text",
                        "text" : text,
                        "emoji": true
                ],
                "element" : [
                        "type"     : "radio_buttons",
                        "options"  : options,
                        "action_id": action_id
                ]
        ]
    }
}
