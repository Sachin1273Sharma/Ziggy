package com.ziggy

import com.google.inject.{AbstractModule, Provides, Singleton}
import org.apache.pekko.actor.typed.{ActorSystem, Scheduler}
import org.apache.pekko.util.Timeout
import slick.jdbc.PostgresProfile.api.Database

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class AppModule(system: ActorSystem[_]) extends AbstractModule {

  @Provides
  @Singleton
  def provideActorSystem: ActorSystem[_] = system

  @Provides
  def provideScheduler(): Scheduler =
    system.scheduler

  @Provides
  def provideTimeout(): Timeout =
    Timeout(3.seconds)


  @Provides
  @Singleton
  def provideExecutionContext: ExecutionContext = system.executionContext

  @Provides
  @Singleton
  def provideDatabase: Database = Database.forConfig("db-config.db")


}
