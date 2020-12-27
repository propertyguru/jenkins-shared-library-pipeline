package org.pg.common.slack

class Modal {

    def get(title, blocks, callback_id, submit="Submit", close="Cancel") {
        return [
            "type": "modal",
            "callback_id": callback_id,
            "submit": [
                "type": "plain_text",
                "text": submit,
                "emoji": True
            ],
            "close": [
                "type": "plain_text",
                "text": close,
                "emoji": True
            ],
            "title": [
                "type": "plain_text",
                "text": title,
                "emoji": True
            ],
            "blocks": blocks
        ]
    }

}
