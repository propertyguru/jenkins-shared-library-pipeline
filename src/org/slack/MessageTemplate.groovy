package org.slack

@Singleton
class MessageTemplate implements Serializable {
    static String heading
    static String startedBy
    static String branch
    static String jenkinsJobURL
    private static ArrayList<ArrayList> stageBlocks = []
    static Map inputBlock = null
    static Map errorBlock = null

    static ArrayList builder() {
        ArrayList block =  []
        block.add(headerBlock())
        block.add(sectionBlock())
        block.add(dividerBlock())
        block += stageBlocks
        if (inputBlock != null) {
            block.add(inputBlock)
        }
        if (errorBlock != null) {
            block.add(errorBlock)
        }
        return block
    }

    private static Map headerBlock() {
        return [
            "type": "header",
            "text": [
                "type": "plain_text",
                "text": heading,
                "emoji": true
            ]
        ]
    }

    private static Map sectionBlock() {
        return [
            "type": "section",
            "fields": [
                [
                    "type": "mrkdwn",
                    "text": "*Started By:*\n${startedBy}"
                ], [
                    "type": "mrkdwn",
                    "text": "*Branch:*\n${branch}"
                ], [
                    "type": "mrkdwn",
                    "text": "*Jenkins URL:*\n${jenkinsJobURL}"
                ]
            ]
        ]
    }

    static void addStageBlock(ArrayList stageBlock) {
        stageBlocks.add(stageBlock)
    }

    private static Map dividerBlock() {
        return [
            "type": "divider"
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

    static ArrayList<Map> buttonBlock(String msg, ArrayList<String> buttons, String block_id) {
        ArrayList elements = []
        buttons.each { String button ->
            elements.add([
                "type": "button",
                "text": [
                    "type": "plain_text",
                    "text": button,
                    "emoji": true
                ],
                "value": button,
                "action_id": block_id + button
            ])
        }
        return [
            markdownText(msg),
            [
                "type": "actions",
                "block_id": block_id,
                "elements": elements
            ]
        ]
    }

}
