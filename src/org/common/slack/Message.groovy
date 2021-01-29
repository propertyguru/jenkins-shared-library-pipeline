package org.common.slack

@Singleton
class Message implements Serializable {

    private static Map heading
    private static Map details
    private static ArrayList<StageBlock> stageBlocks = []
    private static Map error

    static void addHeading(String text) {
        heading = Block.header(text)
    }

    static void addDetails(ArrayList<String> fields) {
        details = Block.sectionWithFields(fields)
    }

    static void addError(String text) {
        error = Block.markdownText("```" + text + "```")
    }

    static void addStageBlock(StageBlock sb) {
        stageBlocks.add(sb)
    }

    static ArrayList toBlocks() {
        ArrayList blocks = []
        blocks.add(heading)
        blocks.add(details)
        blocks.add(Block.divider())
        stageBlocks.each { StageBlock sb ->
            blocks += sb.toBlock()
            blocks.add(Block.divider())
        }
        return blocks
    }
}
