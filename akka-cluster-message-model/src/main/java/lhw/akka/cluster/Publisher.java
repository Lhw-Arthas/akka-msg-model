package lhw.akka.cluster;

import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

/**
 * Created by lhwarthas on 19/1/22.
 */

public class Publisher extends UntypedAbstractActor {

    private final ActorRef mediator =
            DistributedPubSub.get(getContext().system()).mediator();

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof String) {
            mediator.tell(new DistributedPubSubMediator.Publish("events", msg), getSelf());
        } else {
            unhandled(msg);
        }
    }

}
