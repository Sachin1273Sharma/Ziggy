	package com.ziggy.actor
	
	
	import com.google.inject.{Guice, Injector}
	import com.ziggy.AppModule
	import org.apache.pekko.actor.typed.{ActorRef, ActorSystem, Scheduler}
	import com.ziggy.service.{CustomerCommand, DeliveryCommand, OrderService,
	PartnerService, RestaurantCommand}
	import org.apache.pekko.util.Timeout
	
	import javax.inject.{Inject, Singleton}
	import scala.concurrent.ExecutionContextExecutor
	import scala.concurrent.duration.DurationInt
	
	@Singleton
	class ActorProvider @Inject(
	                           partnerService: PartnerService,
	                           orderService: OrderService
	                         )
	                         (val system: ActorSystem[_]) {
	
	implicit val timeout: Timeout = 3.seconds
	implicit val scheduler: Scheduler = system.scheduler
	implicit val ec: ExecutionContextExecutor = system.executionContext
	val injector: Injector = Guice.createInjector(new AppModule(system))
	private val deliveryInjector: Delivery = injector.getInstance(classOf[Delivery])
	val deliveryActor: ActorRef[DeliveryCommand] =
		system.systemActorOf(deliveryInjector.behavior, "delivery")
	private val restaurantInjector = injector.getInstance(classOf[Restaurant])
	val restaurantActor: ActorRef[RestaurantCommand] = system.systemActorOf(
		restaurantInjector.behavior(),
		"restaurant")
	}
