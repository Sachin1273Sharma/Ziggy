package com.stream.service

import com.stream.database.model.{Customer, RegisterRequest}
import com.stream.database.table.CustomerTable
import io.circe.JsonObject

import java.util.UUID
import scala.concurrent.Future

class DbService(val customerTable : CustomerTable) {
  
  def findCustomerByEmail(data : RegisterRequest): Future[Option[Customer]] = {
    customerTable.findByEmail(data.email)
  }

  def findCustomerById(id : String) : Future[Option[Customer]] = {
    customerTable.findById(id)
  }

  def register(data: Customer): Future[UUID] = {
    customerTable.insert(data)
  }
  
}