package com.ziggy.api

import io.circe.parser.decode
import io.circe.{Decoder, Encoder, Printer}
import io.circe.syntax.*
import org.apache.pekko.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import org.apache.pekko.http.scaladsl.model.MediaTypes.`application/json`
import org.apache.pekko.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}

import java.sql.Time
import scala.util.Try


trait JsonSupport {
  given Decoder[Time] = Decoder.decodeString.emapTry(value => Try(Time.valueOf(value)))

  given Encoder[Time] = Encoder.encodeString.contramap(_.toLocalTime.toString)

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
