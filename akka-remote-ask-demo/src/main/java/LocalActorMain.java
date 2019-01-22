import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by lhwarthas on 19/1/18.
 */

public class LocalActorMain {

    public static void main(String[] args) throws InterruptedException {
        Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 1552).withFallback(ConfigFactory.load());
        ActorSystem actorSystem = ActorSystem.create("demo2System", config);
        ActorRef localActorRef = actorSystem.actorOf(Props.create(LocalActor.class), "localActor");
        for (int i = 0; i < 10000; i++) {
            localActorRef.tell("hello", ActorRef.noSender());
            Thread.sleep(1000L);
        }
    }

}
