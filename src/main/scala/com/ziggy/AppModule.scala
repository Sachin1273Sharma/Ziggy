package com.ziggy

import com.ziggy.actor.{Delivery, Restaurant}
import com.ziggy.service.{DeliveryCommand, RestaurantCommand}
import com.google.inject.{AbstractModule, Provides, Singleton}
import org.apache.pekko.actor.typed.{ActorRef, ActorSystem, Scheduler}
import org.apache.pekko.util.Timeout
import slick.jdbc.PostgresProfile.api.Database

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class AppModule(system: ActorSystem[?]) extends AbstractModule {

  @Provides
  @Singleton
  def provideActorSystem: ActorSystem[?] = system

  @Provides
  @Singleton
  def provideDeliveryActorRef(delivery: Delivery): ActorRef[DeliveryCommand] =
    system.systemActorOf(delivery.behavior, "delivery")

  @Provides
  @Singleton
  def provideRestaurantActorRef(restaurant: Restaurant): ActorRef[RestaurantCommand] =
    system.systemActorOf(restaurant.behavior(), "restaurant")

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
