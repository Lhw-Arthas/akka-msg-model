package lhw.akka.cluster;

import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by lhwarthas on 19/1/22.
 */

public class Subscriber extends UntypedAbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public Subscriber(){

        ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
        mediator.tell(new DistributedPubSubMediator.Subscribe("events", getSelf()), getSelf());

    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof String) {
            log.info("Got: {}", msg);
        } else if (msg instanceof DistributedPubSubMediator.SubscribeAck) {
            log.info("subscribing");
        } else {
            unhandled(msg);
        }
    }

}
