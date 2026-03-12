package com.ziggy

import com.google.inject.{AbstractModule, Provides, Singleton}
import org.apache.pekko.actor.typed.ActorSystem
import slick.jdbc.PostgresProfile.api.Database

import scala.concurrent.ExecutionContext

class AppModule(system: ActorSystem[_]) extends AbstractModule {

  @Provides
  @Singleton
  def provideActorSystem: ActorSystem[_] = system

  @Provides
  @Singleton
  def provideExecutionContext: ExecutionContext = system.executionContext

  @Provides
  @Singleton
  def provideDatabase: Database = Database.forConfig("db-config.db")
}
