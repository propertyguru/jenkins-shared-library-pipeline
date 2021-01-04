package org.pg.common.slack

import org.pg.common.Log

class Message {
    private String type
    private String text
    private String status
    private Map<String, String> emoji = [
            "running": ":waiting:",
            "success": ":white_check_mark:",
            "failed": ":x:",
            "skipped": ":red_circle:"
    ]

    Message(String type, String text, String status="running") {
        this.type = type
        this.text = text
        this.status = status
    }

    // @todo princetyagi: maybe change the format function
    def format() {
        // @todo princetyagi: change this to case
        if (this.type == "heading") {
            return headerText(this.text)
        } else if (this.type == "subheading") {
            return markdownText(this.text)
        } else if (this.type == "stage") {
            return markdownText(emoji[this.status] + " *" + this.text + "*")
        } else if (this.type == "step") {
            return markdownText("• " + this.text)
        } else {
            Log.info("Invalid type provided")
            // @todo princetyagi: throw error here or handle this case properly
//            throw Exception("Invalid type provided to slack message format function")
        }
    }

    def update(status) {
        this.status = status
    }

    private static def markdownText(text) {
        return [
                "type": "context",
                "elements": [
                        [
                                "type": "mrkdwn",
                                "text": text
                        ]
                ]
        ]
    }

    private static def headerText(text) {
        return [
                "type": "header",
                "text": [
                        "type": "plain_text",
                        "text": text,
                        "emoji": true
                ]
        ]
    }

}
