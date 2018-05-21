package com.sag_wedt.brand_safety;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.sag_wedt.brand_safety.googleCloudActors.GoogleCloudActor;
import com.sag_wedt.brand_safety.myClassifierActors.MyClassifierActor;
import com.sag_wedt.brand_safety.mainActor.MainActor;

import java.io.IOException;
import java.util.ArrayList;

public class AkkaQuickstart {
  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("helloakka");
    try {
      //#create-actors
      final ActorRef googleCloudActor =
              system.actorOf(GoogleCloudActor.props(), "googleCloudActor_1");
      final ActorRef myClassifierActor =
              system.actorOf(MyClassifierActor.props(), "myClassifierActor_1");
      final ActorRef mainActor =
        system.actorOf(MainActor.props(myClassifierActor, googleCloudActor), "mainActor");
      //#create-actors

      mainActor.tell(new MainActor.ClassifyWebPage("", new ArrayList<>()), ActorRef.noSender());

      System.out.println(">>> Press ENTER to exit <<<");
      System.in.read();
    } catch (IOException ioe) {
    } finally {
      system.terminate();
    }
  }
}
