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

package github4s.algebras

import github4s.GithubResponses.GHResponse
import github4s.domain._

trait Checks[F[_]] {

  def createCheckRun(
      owner: String,
      repo: String,
      run: CheckRun,
      headers: Map[String, String] = Map.empty
  ): F[GHResponse[CheckRun]]

  def updateCheckRun(
      owner: String,
      repo: String,
      id: Int,
      run: CheckRun,
      headers: Map[String, String] = Map.empty
  ): F[GHResponse[CheckRun]]

  def listCheckRunsBySuite(
      owner: String,
      repo: String,
      suiteId: Int,
      check_name: Option[String] = None,
      status: Option[CheckRunStatus] = None,
      pagination: Option[Pagination] = None,
      headers: Map[String, String] = Map.empty
  ): F[GHResponse[List[CheckRun]]]

  def listCheckRunsByRef(
      owner: String,
      repo: String,
      ref: String,
      check_name: Option[String] = None,
      status: Option[CheckRunStatus] = None,
      pagination: Option[Pagination] = None,
      headers: Map[String, String] = Map.empty
  ): F[GHResponse[List[CheckRun]]]
}
