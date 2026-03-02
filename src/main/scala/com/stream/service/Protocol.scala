package com.stream.service

import akka.actor.typed.ActorRef

case class OrderDetails(
                       name : String,
                       item : Seq[(Int,(Int,Int))],
                       address : String,
                       phone : String,
                       email : String,
                       totalPrice : Int
                       )


sealed trait RestaurantCommand

final case class PlaceOrder(details : OrderDetails, customerRef : ActorRef[CustomerCommand], replyTo : ActorRef[RestaurantCommand]) extends RestaurantCommand
final case class OrderPrepared(item : String, customerRef : ActorRef[CustomerCommand]) extends RestaurantCommand
final case class PartnerAssigned(orderId : String,customerRef : ActorRef[CustomerCommand]) extends RestaurantCommand
final case class PlaceOrderResponse(information: String) extends RestaurantCommand

sealed trait CustomerCommand
final case class OrderAccepted(item : String,orderId : String) extends CustomerCommand
final case class Order(item : String) extends CustomerCommand
final case class OrderConfirmed(item : String) extends CustomerCommand

sealed trait DeliveryCommand
final case class Deliver(item : String,customerRef : ActorRef[CustomerCommand]) extends DeliveryCommand
final case class FindPartner(item : String, orderId : String, restaurantRef : ActorRef[RestaurantCommand]) extends DeliveryCommand
final case class DeliveryPartnerAssigned(order : OrderDetails) extends DeliveryCommand