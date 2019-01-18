package lhw.demo.akka.msg.entity;

import java.io.Serializable;

/**
 * Created by lhwarthas on 19/1/17.
 */

public class Ack implements Serializable {
    String ip;

    String msgId;

    public Ack() {
    }

    public Ack(String ip, String msgId) {
        this.ip = ip;
        this.msgId = msgId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
