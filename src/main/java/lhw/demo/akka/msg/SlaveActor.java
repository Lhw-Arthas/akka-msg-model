package lhw.demo.akka.msg;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import lhw.demo.akka.msg.entity.Ack;
import lhw.demo.akka.msg.entity.Deregister;
import lhw.demo.akka.msg.entity.Message;
import lhw.demo.akka.msg.entity.Register;

/**
 * Created by lhwarthas on 19/1/17.
 */

public class SlaveActor extends UntypedAbstractActor {

    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private ActorSelection masterSelection;

    public SlaveActor() {
        logger.info("slave actor is start");
        String masterIp = null;
        try {
            masterIp = Main.leaderLatch.getLeader().getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        masterSelection = getContext().actorSelection("akka.tcp://demoSystem@" + masterIp + ":2554/user/masterActor");
        masterSelection.tell(new Register(IPUtils.getLocalIntranetIp()), ActorRef.noSender());
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Message) {
            logger.info("receive Message, msgId is " + ((Message) message).getMsgId() + "  content is " + ((Message) message).getContent());
            getSender().tell(new Ack(IPUtils.getLocalIntranetIp(), ((Message) message).getMsgId()), getSelf());
            return;
        }
        if (message instanceof Deregister) {
            logger.info("slave deregister!");
            masterSelection.tell(message, getSelf());
            return;
        }
        unhandled(message);
    }
}
