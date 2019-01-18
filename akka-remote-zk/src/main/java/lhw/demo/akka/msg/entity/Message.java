package lhw.demo.akka.msg.entity;

import java.io.Serializable;

/**
 * Created by lhwarthas on 19/1/17.
 */

public class Message implements Serializable {

    private String msgId;

    private String content;

    public Message() {
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
