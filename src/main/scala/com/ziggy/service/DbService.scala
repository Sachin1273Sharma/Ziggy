package com.ziggy.service

import com.ziggy.database.model.{Customer, RegisterRequest}
import com.ziggy.database.table.CustomerTable
import io.circe.JsonObject

import java.util.UUID
import scala.concurrent.Future

class DbService(val customerTable : CustomerTable) {
  
  def findCustomerByEmail(email : String): Future[Option[Customer]] = {
    customerTable.findByEmail(email)
  }

  def findCustomerById(id : String) : Future[Option[Customer]] = {
    customerTable.findById(id)
  }

  def register(data: Customer): Future[UUID] = {
    customerTable.insert(data)
  }

  
}