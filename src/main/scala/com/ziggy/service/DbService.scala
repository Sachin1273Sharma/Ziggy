package com.ziggy.service

import com.ziggy.database.model.{Customer, Order, Partner}
import com.ziggy.database.table.{CustomerTable, OrderTable, PartnerTable}

import java.util.UUID
import scala.concurrent.Future

class DbService(customerTable: CustomerTable,
                orderTable: OrderTable,
                partnerTable: PartnerTable) {
  
  def findCustomerByEmail(email : String): Future[Option[Customer]] = {
    customerTable.findByEmail(email)
  }

  def findCustomerById(id : String) : Future[Option[Customer]] = {
    customerTable.findById(id)
  }

  def register(data: Customer): Future[UUID] = {
    customerTable.insert(data)
  }


  /* Order */

  def createOrder(order : Order): Future[String] = {
      orderTable.insert(order)
  }

  /* Partner */

  def createPartner(partner: Partner): Future[String] = {
    partnerTable.insert(partner)
  }

  def findPartnerById(id: String): Future[Option[Partner]] = {
    partnerTable.findById(id)
  }

  def findPartnerByEmail(email: String): Future[Option[Partner]] = {
    partnerTable.findByEmail(email)
  }

  def listPartners(): Future[Seq[Partner]] = {
    partnerTable.listAll
  }

  def listAvailablePartners(): Future[Seq[Partner]] = {
    partnerTable.listAvailable
  }

  def updatePartner(id: String, partner: Partner): Future[Int] = {
    partnerTable.update(id, partner)
  }

  def assignPartnerOrder(id: String, orderId: String): Future[Int] = {
    partnerTable.assignOrder(id, orderId)
  }

  def clearPartnerOrder(id: String): Future[Int] = {
    partnerTable.clearOrder(id)
  }

  def deletePartner(id: String): Future[Int] = {
    partnerTable.delete(id)
  }
}
