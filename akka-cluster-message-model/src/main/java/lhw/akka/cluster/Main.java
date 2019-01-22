package lhw.akka.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Scanner;

/**
 * Created by lhwarthas on 19/1/22.
 */

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("输入端口：");
        Scanner s = new Scanner(System.in);
        String port = null;
        if (s.hasNext()) {
            port = s.next();
        }
        Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).withFallback(ConfigFactory.load());
        ActorSystem actorSystem = ActorSystem.create("ClusterSystem", config);
        System.out.println(actorSystem.provider().getDefaultAddress().hostPort());

        ActorRef publisher = actorSystem.actorOf(Props.create(Publisher.class), "publisher_" + port);
        ActorRef subscriber = actorSystem.actorOf(Props.create(Subscriber.class), "subscriber_" + port);

        while (true) {
            publisher.tell("message from : " + port, ActorRef.noSender());
            Thread.sleep(5000L);
        }

    }

}
