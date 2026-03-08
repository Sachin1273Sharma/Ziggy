package com.ziggy

import com.google.inject.{AbstractModule, Provides, Singleton}
import akka.actor.typed.ActorSystem
import scala.concurrent.ExecutionContext
import javax.inject.Inject


class AppModule(system: ActorSystem[_]) extends AbstractModule {

  // Akka ki internal cheezein jo Guice khud nahi bana sakta
  @Provides
  @Singleton
  def provideActorSystem: ActorSystem[_] = system

  @Provides
  @Singleton
  def provideExecutionContext: ExecutionContext = system.executionContext
}