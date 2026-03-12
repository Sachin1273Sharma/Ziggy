package com.ziggy.service


import com.ziggy.database.model.OrderStatus.Created
import com.ziggy.database.model.{Order, OrderRequest}
import com.ziggy.utils.Logger

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}


class OrderService @Inject()(dbService: DbService)
                            (using ec: ExecutionContext)
  extends Logger {


  def createOrder(data: OrderRequest): Future[Either[Boolean, String]] = {
    val order = Order(id = Some(UUID.randomUUID.toString),
      customerId = data.customerId,
      restaurantId = data.restaurantId,
      totalAmount = data.total,
      status = Created
    )
    dbService.createOrder(order).map(Right(_)).
      recover {
        case ex : Exception => Left(false)
      }
  }

	def cancelOrder(orderId: String): Future[Boolean] = {
		dbService.cancelOrder(orderId).transform { case 1 => Success(true)
		case 0 => Success(false)
		case _ => Success(false)
		}
	}


}