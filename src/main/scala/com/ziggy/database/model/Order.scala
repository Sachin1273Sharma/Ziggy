package com.ziggy.database.model


import java.util.UUID

final case class Order(
                        id: Option[String] = None,
                        customerId: String,
                        restaurantId: String,
                        isPartnerAssigned: Boolean = false,
                        partnerId: Option[String] = None,
                        totalAmount: BigDecimal,
                        status: OrderStatus
                      )

final case class OrderItem(
                            item: Item,
                            quantity: Int,
                            orderPrice: Double
                          )

final case class OrderRequest(
                               items: Array[OrderItem],
                               total: Double,
                               restaurantId: String,
                               customerId: String
                             )

enum OrderStatus:
  case Created, Confirmed, Assigned, OutForDelivery, Cancelled, Delivered