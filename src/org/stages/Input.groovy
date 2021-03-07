package org.stages

import org.common.StepExecutor
import org.slack.MessageTemplate
import org.slack.Slack

class Input extends Base {

    private String msg
    private String block_id
    private ArrayList<String> buttons

    Input(String msg, String block_id, ArrayList<String> buttons) {
        super(true)
        this.stage = "Input"
        this.msg = msg
        this.block_id = block_id
        this.buttons = buttons
    }

    @Override
    def body() {
        MessageTemplate.inputBlock = MessageTemplate.buttonBlock(this.msg, this.buttons, this.block_id)
        Slack.sendMessage()
        StepExecutor.input(this.msg, this.block_id, this.buttons[0])
    }

    @Override
    Boolean skip() {
        return false
    }
}