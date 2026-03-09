package com.ziggy.api

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.http.scaladsl.model.MediaTypes.`application/json`
import io.circe.syntax._
import io.circe.parser.decode
import io.circe.{Decoder, Encoder, Printer}


trait JsonSupport {
  // JSON to Scala (Unmarshalling)
  implicit def unmarshaller[T: Decoder]: FromEntityUnmarshaller[T] =
    Unmarshaller.stringUnmarshaller.forContentTypes(`application/json`).map { str =>
      decode[T](str) match {
        case Right(value) => value
        case Left(error)  => throw new IllegalArgumentException(error.getMessage)
      }
    }

  // Scala to JSON (Marshalling)
  implicit def marshaller[T: Encoder]: ToEntityMarshaller[T] =
    Marshaller.stringMarshaller(`application/json`).compose(_.asJson.printWith(Printer.noSpaces))
}