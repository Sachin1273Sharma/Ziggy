package com.stream.api

import akka.http.scaladsl.server.Directives.*
import com.stream.controller.OrderController
import com.stream.service.OrderDetails
import io.circe.generic.auto.*

class routes(controller : OrderController) extends JsonSupport
{
 
    
}