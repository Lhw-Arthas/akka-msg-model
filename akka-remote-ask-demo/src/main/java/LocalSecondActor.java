import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.concurrent.TimeUnit;

/**
 * Created by lhwarthas on 19/1/18.
 */

public class LocalSecondActor extends UntypedAbstractActor {

    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private static final Timeout TIMEOUT = new Timeout(5, TimeUnit.SECONDS);

    private ActorRef remoteActor;

    @Override
    public void preStart() {

        logger.info("localActor start!");

        //Get a reference to the remote actor
        Future<ActorRef> actorRefFuture = getContext().actorSelection("akka.tcp://demo2System@192.168.4.126:1551/user/remoteActor")
                .resolveOne(TIMEOUT);
        try {
            remoteActor = Await.result(actorRefFuture, TIMEOUT.duration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(remoteActor.toString());
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        remoteActor.tell(message, getSelf());
    }

}
