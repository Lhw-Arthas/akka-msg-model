package lhw.demo.akka.msg;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lhw.demo.akka.msg.entity.Deregister;
import lhw.demo.akka.msg.entity.Message;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by lhwarthas on 19/1/17.
 */

public class Main {

    public static final CuratorFramework zkCurator = getClient();

    public static LeaderLatch leaderLatch;

    public static boolean isLeader;

    private static final String PATH = "/lhw/demo/akka/msg";

    private static ActorRef masterRef;

    private static ActorRef slaveRef;

    public static void main(String[] args) throws Exception {
        String ip = IPUtils.getLocalIntranetIp();
        leaderLatch = new LeaderLatch(zkCurator, PATH, ip);
        leaderLatch.start();
        isLeader = leaderLatch.await(1, TimeUnit.SECONDS);

        Config config = ConfigFactory.parseString("akka.remote.netty.tcp.hostname=" + ip).withFallback(ConfigFactory.load());
        ActorSystem actorSystem = ActorSystem.create("demoSystem", config);

        Runtime.getRuntime().addShutdownHook(new Thread(null, null, "shutdownHook") {
            @Override
            public void run() {
                System.out.println("shutdown!");
                if (!isLeader) {
                    System.out.println("slave shutdown!");
                    slaveRef.tell(new Deregister(IPUtils.getLocalIntranetIp()), ActorRef.noSender());
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("shutdown complete!");
                }
            }
        });

        if (isLeader) {
            System.out.println("You are master!");
            masterRef = actorSystem.actorOf(Props.create(MasterActor.class), "masterActor");
            while (true) {
                Message message = new Message();
                message.setMsgId(UUID.randomUUID().toString());
                message.setContent("Hello");
                masterRef.tell(message, ActorRef.noSender());
                Thread.sleep(1000L);
            }
        } else {
            System.out.println("You are slave!");
            slaveRef = actorSystem.actorOf(Props.create(SlaveActor.class), "slaveActor");
        }
    }

    private static CuratorFramework getClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("192.168.2.23:2181,192.168.2.23:2182,192.168.2.23:2183,192.168.2.23:2184,192.168.2.23:2185")
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(6000)
                .connectionTimeoutMs(3000)
                //.namespace("demo")
                .build();
        client.start();
        return client;
    }

}
