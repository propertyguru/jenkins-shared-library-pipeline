@SuppressWarnings(['EmptyMethod', 'MethodReturnTypeRequired', 'UnusedMethodParameter'])
class SlackResponse implements Serializable {

    String channelId
    String ts
    String threadId

    SlackResponse() {
        this.channelId = "#prince-test"
        this.ts = "ts"
        this.threadId = "threadId"
    }

    String getChannelId() {
        return this.channelId
    }

    String getTs() {
        return this.ts
    }

    String getThreadId() {
        return this.threadId
    }
}