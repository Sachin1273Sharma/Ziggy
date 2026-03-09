package com.ziggy.service

import akka.actor.typed.ActorRef
import com.ziggy.database.model.OrderRequest


sealed trait RestaurantCommand

final case class PlaceOrder(details : OrderRequest, customerRef : ActorRef[CustomerCommand], replyTo
: ActorRef[RestaurantCommand]) extends RestaurantCommand
final case class OrderPrepared(item : String, customerRef : ActorRef[CustomerCommand]) extends RestaurantCommand
final case class PartnerAssigned(orderId : String,customerRef : ActorRef[CustomerCommand]) extends RestaurantCommand
final case class PlaceOrderResponse(information: String) extends RestaurantCommand
case object OrderCreationFailed extends RestaurantCommand

sealed trait CustomerCommand
final case class OrderAccepted(item : String,orderId : String) extends CustomerCommand
final case class OrderConfirmed(item : String) extends CustomerCommand

sealed trait DeliveryCommand
final case class Deliver(item : String,customerRef : ActorRef[CustomerCommand]) extends DeliveryCommand
final case class FindPartner(orderId : String) extends DeliveryCommand
final case class DeliveryPartnerAssigned(order : OrderDetails) extends DeliveryCommand