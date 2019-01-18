package lhw.demo.akka.msg.entity;

import java.io.Serializable;

/**
 * Created by lhwarthas on 19/1/17.
 */

public class Register implements Serializable {

    private String ip;

    public Register() {
    }

    public Register(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
