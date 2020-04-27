/*
 * Copyright 2016-2020 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github4s.domain

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.{ZoneOffset, ZonedDateTime}

import cats.effect.Sync
import io.circe._

import scala.util.control.NonFatal

case class Timestamp private (time: ZonedDateTime)

object Timestamp {
  def now[F[_]: Sync]: F[Timestamp] =
    Sync[F].delay(unsafeNow)

  def unsafeNow: Timestamp =
    new Timestamp(ZonedDateTime.now(ZoneOffset.UTC))

  implicit val decoder: Decoder[Timestamp] = (c: HCursor) => {
    c.value.asString match {
      case Some(string) => {
        try {
          Right(Timestamp(ZonedDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME)))
        } catch {
          case NonFatal(ex) => Left(DecodingFailure(ex.getMessage, c.history))
        }
      }
      case _ => Left(DecodingFailure(s"Failed to decode Timestamp: ${c.value}", c.history))
    }
  }

  implicit val encoder: Encoder[Timestamp] = (a: Timestamp) => {
    val formatted = a.time
      .truncatedTo(ChronoUnit.SECONDS)
      .format(DateTimeFormatter.ISO_DATE_TIME)
    Json.fromString(formatted)
  }
}
