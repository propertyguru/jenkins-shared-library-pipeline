package org.slack

class StageBlock implements Serializable {
    private String status
    private String stageName
    private ArrayList<String> steps = []
    private Map<String, String> emoji = [
            "success": ":white_check_mark:",
            "running": ":waiting:",
            "failed": ":x:"
    ]

    void setStatus(String text) {
        this.status = text
    }

    void setStageName(String text) {
        this.stageName = text
    }

    void addStep(String text) {
        this.steps.add(text)
    }

    ArrayList<Map> buildMessage() {
        if (this.status == "skipped") {
            return [
                MessageTemplate.markdownText("~* ${this.stageName} *~")
            ]
        } else {
            return [
                    MessageTemplate.markdownText("${this.emoji[this.status]} * ${this.stageName} *"),
                    MessageTemplate.markdownText(this.steps.collect {"• ${it}"}.join("\n"))
            ]
        }
    }
}