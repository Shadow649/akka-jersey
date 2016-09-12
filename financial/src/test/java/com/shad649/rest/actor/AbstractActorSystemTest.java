package com.shad649.rest.actor;

import org.junit.After;
import org.junit.Before;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import akka.testkit.TestProbe;

public abstract class AbstractActorSystemTest {
  private ActorSystem system;
  
  @Before
  public void setupActorSystem() {
    system = ActorSystem.create("test");
  }
  
  @After
  public void teardownActorSystem() {
    if (system != null) {
      system.terminate();
    }
  }
  
  protected ActorSystem getActorSystem() {
    return system;
  }
  
  protected TestProbe testProbe() {
    return new TestProbe(getActorSystem());
  }
  
  protected <T extends Actor> TestActorRef<T> actorOf(Props props) {
    return TestActorRef.apply(props, getActorSystem());
  }
  
  protected <T extends Actor> TestActorRef<T> actorOf(Class<T> actorClass) {
    return actorOf(Props.create(actorClass));
  }
  
  protected void subscribe(ActorRef ref, Class<?> eventClass) {
    getActorSystem().eventStream().subscribe(ref, eventClass);
  }
  
  protected void unsubscribe(ActorRef ref) {
    getActorSystem().eventStream().unsubscribe(ref);
  }
  
  protected void publish(Object event) {
    TestProbe probe = testProbe();
    subscribe(probe.ref(), event.getClass());
    getActorSystem().eventStream().publish(event);
    probe.expectMsg(event);
    unsubscribe(probe.ref());
    getActorSystem().stop(probe.ref());
  }
}