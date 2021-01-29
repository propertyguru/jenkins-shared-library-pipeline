package org.common.slack

@Singleton
class Block implements Serializable {

    static Map header(String text) {
        return [
                "type": "header",
                "text": [
                        "type": "plain_text",
                        "text": text,
                        "emoji": true
                ]
        ]
    }

    static Map divider() {
        return [
                "type": "divider"
        ]
    }

    static Map sectionWithFields(ArrayList<String> fields) {
        ArrayList f = []
        fields.each { String field ->
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

    static Map markdownText(String text) {
        return [
                "type": "section",
                "text": [
                        "type": "mrkdwn",
                        "text": text
                ]
        ]
    }

}