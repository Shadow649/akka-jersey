package com.shad649.rest.actor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
/**
 * Starts actor systems to compute the transformations on transactions.
 * Two singleton actors are created using the Pattern Singleton.
 * http://doc.akka.io/docs/akka/current/java/cluster-singleton.html
 * @author Emanuele Lombardi
 *
 */
public class Main {
    //There is no validation here. Should be added.
    public static void main(String[] args) {
        if(args.length == 0) {
            startup("2551");
        } else {
            startup(args);
        }
    }

    private static void startup(String... ports) {
        for (String port : ports) {
            Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
                    .withFallback(ConfigFactory.parseString("akka.cluster.roles = [compute]"))
                    .withFallback(ConfigFactory.load("financial"));
            ActorSystem system = ActorSystem.create("ClusterSystem", config);

            //#create-singleton-manager
            ClusterSingletonManagerSettings settings = ClusterSingletonManagerSettings.create(system)
                .withRole("compute");
            system.actorOf(ClusterSingletonManager.props(
                Props.create(MasterFinancialActor.class), PoisonPill.getInstance(), settings),
                "financialService");
            ClusterSingletonManagerSettings settings2 = ClusterSingletonManagerSettings.create(system)
                    .withRole("compute");
                system.actorOf(ClusterSingletonManager.props(
                    Props.create(MockStorageActor.class), PoisonPill.getInstance(), settings2),
                    "financialStorage");
            
            //#singleton-proxy
            ClusterSingletonProxySettings proxySettings =
                ClusterSingletonProxySettings.create(system).withRole("compute");
            system.actorOf(ClusterSingletonProxy.props("/user/financialService",
                proxySettings), "financialServiceProxy");
            ClusterSingletonProxySettings proxySettings2 =
                    ClusterSingletonProxySettings.create(system).withRole("compute");
                system.actorOf(ClusterSingletonProxy.props("/user/financialStorage",
                    proxySettings2), "financialStorageProxy");
            //#singleton-proxy
        }

    }

}
