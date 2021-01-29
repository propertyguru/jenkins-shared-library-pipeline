package org.common.slack

class StageBlock implements Serializable {
    private Map heading
    private Map steps
    private Map<String, String> emoji = [
            "running": ":waiting:",
            "success": ":white_check_mark:",
            "failed": ":x:"
    ]

    StageBlock() {
        this.steps = [:]
    }

    Map addHeading(String text, String status) {
        if (status == "skipped") {
            this.heading = Block.markdownText("~*" + text + "*~")
        } else {
            this.heading = Block.markdownText(this.emoji[status] + " *" + text + "*")
        }
    }

    Map addSteps(String text) {
        String currSteps = this.steps.get("text", [:]).get("text", "")
        text = "• ${text}"
        if (currSteps != "") {
            currSteps += "\n${text}"
        } else {
            currSteps = text
        }
        this.steps = Block.markdownText(currSteps)
    }

    ArrayList toBlock() {
        ArrayList blocks = []
        blocks.add(this.heading)
        if (this.steps.size() > 0) {
            blocks.add(this.steps)
        }
        return blocks
    }

}