package lhw.demo.akka.msg;

import akka.actor.ActorSelection;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import lhw.demo.akka.msg.entity.Ack;
import lhw.demo.akka.msg.entity.Deregister;
import lhw.demo.akka.msg.entity.Message;
import lhw.demo.akka.msg.entity.Register;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lhwarthas on 19/1/17.
 */

public class MasterActor extends UntypedAbstractActor {

    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private static final Map<String, ActorSelection> multiActorMap = new HashMap<>();

    public MasterActor() {
        logger.info("master actor is start");
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Register) {
            String slaveIp = ((Register) message).getIp();
            logger.info("receive register from " + slaveIp);
            multiActorMap.put(slaveIp, context().actorSelection("akka.tcp://demoSystem@" + slaveIp + ":2554/user/slaveActor"));
            return;
        }
        if (message instanceof Message) {
            multiActorMap.forEach((k, v) -> {
                v.tell(message, getSelf());
            });
            return;
        }
        if (message instanceof Ack) {
            logger.info("receive ack, ip is " + ((Ack) message).getIp() + "  msgId is " + ((Ack) message).getMsgId());
            return;
        }
        if (message instanceof Deregister) {
            logger.info("receive deregister from " + ((Deregister) message).getIp());
            multiActorMap.remove(((Deregister) message).getIp());
            return;
        }
        unhandled(message);
    }
}
