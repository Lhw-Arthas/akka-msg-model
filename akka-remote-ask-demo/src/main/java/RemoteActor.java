import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by lhwarthas on 19/1/18.
 */

public class RemoteActor extends UntypedAbstractActor {

    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    @Override
    public void preStart() throws Exception {
        logger.info("remote actor start");
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof String) {
            getSender().tell(message + " got something", getSelf());
        }
    }
}
