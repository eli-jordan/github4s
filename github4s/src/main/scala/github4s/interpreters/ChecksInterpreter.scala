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

package github4s.interpreters

import cats.Functor
import cats.implicits._
import github4s.Decoders._
import github4s.Encoders._
import github4s.GithubResponses.GHResponse
import github4s.algebras.Checks
import github4s.domain._
import github4s.http.HttpClient

class ChecksInterpreter[F[_]: Functor](implicit client: HttpClient[F], accessToken: Option[String])
    extends Checks[F] {

  // POST /repos/:owner/:repo/check-runs
  override def createCheckRun(
      owner: String,
      repo: String,
      run: CheckRun,
      headers: Map[String, String]
  ): F[GHResponse[CheckRun]] = {
    client.post[CheckRun, CheckRun](
      accessToken = accessToken,
      url = s"repos/$owner/$repo/check-runs",
      data = run,
      headers = Map("Accept" -> "application/vnd.github.antiope-preview+json") ++ headers
    )
  }

  // GET /repos/:owner/:repo/check-suites/:check_suite_id/check-runs
  override def listCheckRunsBySuite(
      owner: String,
      repo: String,
      suiteId: Int,
      check_name: Option[String],
      status: Option[CheckRunStatus],
      pagination: Option[Pagination],
      headers: Map[String, String]
  ): F[GHResponse[List[CheckRun]]] = {
    client
      .get[ListCheckRunsResponse](
        accessToken,
        s"repos/$owner/$repo/check-suites/$suiteId/check-runs",
        Map("Accept" -> "application/vnd.github.antiope-preview+json") ++ headers,
        Map(
          "check_name" -> check_name,
          "status"     -> status.map(_.toString)
        ).collect {
          case (key, Some(value)) => key -> value
        },
        pagination
      )
      .map(res => res.copy(result = res.result.map(_.check_runs)))
  }

  // GET /repos/:owner/:repo/commits/:ref/check-runs
  override def listCheckRunsByRef(
      owner: String,
      repo: String,
      ref: String,
      check_name: Option[String],
      status: Option[CheckRunStatus],
      pagination: Option[Pagination],
      headers: Map[String, String]
  ): F[GHResponse[List[CheckRun]]] = {
    client
      .get[ListCheckRunsResponse](
        accessToken,
        s"repos/$owner/$repo/commits/$ref/check-runs",
        Map("Accept" -> "application/vnd.github.antiope-preview+json") ++ headers,
        Map(
          "check_name" -> check_name,
          "status"     -> status.map(_.toString)
        ).collect {
          case (key, Some(value)) => key -> value
        },
        pagination
      )
      .map(res => res.copy(result = res.result.map(_.check_runs)))
  }

  // PATCH /repos/:owner/:repo/check-runs/:check_run_id
  override def updateCheckRun(
      owner: String,
      repo: String,
      id: Int,
      run: CheckRun,
      headers: Map[String, String]
  ): F[GHResponse[CheckRun]] = {
    client.patch[CheckRun, CheckRun](
      accessToken = accessToken,
      method = s"repos/$owner/$repo/check-runs/$id",
      data = run,
      headers = Map("Accept" -> "application/vnd.github.antiope-preview+json") ++ headers
    )
  }
}
