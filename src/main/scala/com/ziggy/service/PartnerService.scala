package com.ziggy.service

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class PartnerService @Inject()()(using ec : ExecutionContext) {

	private def findPartner(orderId)

}