import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by lhwarthas on 19/1/18.
 */

public class RemoteActorMain {

    public static void main(String[] args) {
        Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 1551).withFallback(ConfigFactory.load());
        ActorSystem actorSystem = ActorSystem.create("demo2System", config);
        ActorRef remoteRef = actorSystem.actorOf(Props.create(RemoteActor.class), "remoteActor");
    }

}
