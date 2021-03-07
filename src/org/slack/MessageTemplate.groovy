package org.slack

import hudson.triggers.SCMTrigger

@Singleton
class MessageTemplate implements Serializable {
    static String heading
    static String subheading
    static String startedBy
    static String branch
    static ArrayList<StageBlock> stageBlocks = []
    static ArrayList<Map> inputBlock = null
    static Map errorBlock = null

    static ArrayList builder() {
        ArrayList block =  []
        block.add(headerBlock())
        block.add(markdownText(subheading))
        block.add(sectionBlock())
        block.add(dividerBlock())
        stageBlocks.each { StageBlock sb ->
            ArrayList<Map> msg = sb.buildMessage()
            if (msg.size() > 0) {
                block += msg
            }
        }
        if (inputBlock != null) {
            block += inputBlock
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
                ]
            ]
        ]
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
                "action_id": block_id + "_" + button
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
