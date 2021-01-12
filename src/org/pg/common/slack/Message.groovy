package org.pg.common.slack

import org.pg.common.Log

class Message {
    private String type
    private String text
    private ArrayList<String> fields
    private String status
    private Map<String, String> emoji = [
            "running": ":waiting:",
            "success": ":white_check_mark:",
            "failed": ":x:"
    ]

    Message(String type, String text="", String status="running", ArrayList<String> fields=[]) {
        this.type = type
        this.text = text
        this.status = status
        this.fields = fields
    }

    // @todo princetyagi: maybe change the format function
    def format() {
        // @todo princetyagi: change this to case
        if (this.type == "heading") {
            return headerText(this.text)
        } else if (this.type == "subheading") {
            return markdownText(this.text)
        } else if (this.type == "sectionWithFields") {
            return sectionWithFields(this.fields)
        } else if (this.type == "divider") {
            return divider()
        } else if (this.type == "stage") {
            if (this.status == "skipped") {
                return markdownText("~*" + this.text + "*~")
            } else {
                return markdownText(emoji[this.status] + " *" + this.text + "*")
            }
        } else if (this.type == "step") {
            if (this.text != "") {
                this.text = "• "  + this.text
            }
            return markdownText(this.text)
        } else if (this.type == "error") {
            return markdownText("```" + this.text + "```")
        } else {
            Log.info("Invalid type provided")
            // @todo princetyagi: throw error here or handle this case properly
//            throw Exception("Invalid type provided to slack message format function")
        }
    }

    def update(status) {
        this.status = status
    }

    def addStep(text) {
        if (this.text != "") {
            this.text += "\n"
        }
        this.text += "• ${text}"
    }

    private static def markdownText(text) {
        return [
                "type": "section",
                "text": [
                        "type": "mrkdwn",
                        "text": text
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

    private static def sectionWithFields(ArrayList<String> fields) {
        ArrayList f = []
        fields.each { field ->
            f.add([
                "type": "mrkdwn",
                "text": field
            ])
        }
        return [
                "type": "section",
                "fields": f
        ]
    }

    private static def divider() {
        return [
                "type": "divider"
        ]
    }

}
