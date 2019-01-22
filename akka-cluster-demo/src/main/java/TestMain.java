import akka.actor.*;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by lhwarthas on 19/1/22.
 */

public class TestMain {

    public static class Publisher extends UntypedAbstractActor {
        private final ActorRef mediator =
                DistributedPubSub.get(getContext().system()).mediator();

        @Override
        public void onReceive(Object msg) throws Exception {
            if (msg instanceof String) {
                mediator.tell(new DistributedPubSubMediator.Publish("events", msg), getSelf());
            } else {
                unhandled(msg);
            }
        }

    }

    public static class Subscriber extends UntypedAbstractActor {
        private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        public Subscriber(){

            ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
            mediator.tell(new DistributedPubSubMediator.Subscribe("events", getSelf()), getSelf());

        }

        @Override
        public void onReceive(Object msg) throws Throwable {
            if (msg instanceof String) {
                log.info("Got: {}", msg);
            } else if (msg instanceof DistributedPubSubMediator.SubscribeAck) {
                log.info("subscribing");
            } else {
                unhandled(msg);
            }
        }
    }


    public static void main(String[] args) throws Exception {

        final Config config = ConfigFactory.parseString(
                "akka.actor.provider=cluster\n" +
                        "akka.remote.netty.tcp.port=2551\n" +
                        "akka.remote.netty.tcp.host=127.0.0.1\n" +
                        "akka.cluster.seed-nodes = [ \"akka.tcp://ClusterSystem@127.0.0.1:2551\"]\n");

        ActorSystem node1 = ActorSystem.create("ClusterSystem", config);
        ActorSystem node2 = ActorSystem.create("ClusterSystem",
                ConfigFactory.parseString("akka.remote.netty.tcp.port=2552").withFallback(config));

        // wait a bit for the cluster to form
        Thread.sleep(3000);

        ActorRef subscriber1 = node1.actorOf(Props.create(Subscriber.class), "subscriber1");
        ActorRef subscriber2 = node2.actorOf(Props.create(Subscriber.class), "subscriber2");
        ActorRef publisher1 = node2.actorOf(Props.create(Publisher.class), "publisher1");
        ActorRef publisher2 = node2.actorOf(Props.create(Publisher.class), "publisher2");

        // wait a bit for the subscription to be gossiped
        Thread.sleep(3000);

        publisher1.tell("testMessage1", ActorRef.noSender());
        publisher2.tell("testMessage2", ActorRef.noSender());
    }

}
