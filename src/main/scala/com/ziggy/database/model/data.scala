package com.ziggy.database.model

import java.sql.Time
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

final case class Address(
	                        id: String,
	                        line1: Option[String],
	                        line2: String,
	                        city: String,
	                        country: String,
	                        pincode: String,
	                        latitude: Double,
	                        longitude: Double
                        )

final case class CustomerAddress(
                                  id: Option[String] = None,
                                  customerId: UUID,
                                  addressId: String
                                )

final case class User(
                           id: Option[UUID] = None,
                           name: Option[String] = None,
                           email: Option[String] = None,
                           passwordHash: Option[String] = None,
                           phoneNumber: Option[String] = None,
                           userType : UserType,
                           isProMember: Option[Boolean] = None,
                           isActive: Option[Boolean] = None,
                           dateOfBirth: Option[String] = None,
                           notes: Option[String] = None,
                           createdAt: Option[Instant] = None,
                           updatedAt: Option[Instant] = None
                         )

final case class Partner(
                          id: Option[String] = None,
                          name: String,
                          email: String,
                          phoneNumber: String,
                          vehicle: PartnerVehicle,
                          city : String,
                          isOpenToService: Boolean = true,
                          isAvailable: Boolean = true,
                          isEngagedInOrder: Boolean = false,
                          currentOrderId: Option[String] = None,
                          createdAt: Option[Instant] = None,
                          updatedAt: Option[Instant] = None,
                          pinCodes : List[String]
                        )

enum PartnerVehicle:
  case Cycle, Bike

final case class RegisterRequest(email: String, password: String, name: Option[String])

final case class LoginRequest(email: String, password: String)

final case class RefreshRequest(refreshToken: String)

final case class MessageResponse(message: String)


//Security
sealed trait Security

final case class JwtLoginCred(userId: String, iat: Long) extends Security


final case class Item(
                       id: Option[String] = None,
                       restaurantId: String,
                       name: String,
                       price: Double,
                       rating: Double,
                       isAvailable: Boolean,
                       quick: Boolean = false,
                       quantityLeft: Int,
                       createdAt: Timestamp,
                       updatedAt: Option[Timestamp] = None
                     )



final case class OrderItem(
                            id: Option[String] = None,
                            orderId: String,
                            itemId: String,
                            quantity: Int,
                            priceAtTimeOfOrder: BigDecimal
                          )

final case class Restaurant(
                            id: Option[String] = None,
                            name : String ,
                            addressId: Option[String] = None,
                            joiningDate: Timestamp,
                            openingTime: Time,
                            closingTime: Time,
                            isOpen: Boolean = false,
                            pinCodes :List[String]
                          )
final case class AddRestaurant(
	                              name : String,
                               address: Address,
                               openingTime : Time,
                               closingTime : Time,
                               isOpen : Boolean,
                               pinCodes : List[String])
final case class UpdateRestaurant(
                                 name : Option[String] = None,
                                 address : Option[String] = None,
                                 openingTime : Option[Time]= None,
                                 closingTime : Option[Time] = None,
                                 isOpen : Option[Boolean] = None,
                                 pincodes : Option[List[String]] = None
                                 )

enum UserType :
			case CUSTOMER,DELIVERY_PARTNER,RESTAURANT_ADMIN,ZIGGY_CUSTOMER_SERVICE,ZIGGY_ADMIN_USER



