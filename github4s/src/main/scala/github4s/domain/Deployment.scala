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

import io.circe.Json

case class Deployment(
    id: Int,
    sha: String,
    ref: String,
    task: String,
    description: Option[String],
    payload: Option[Json] = None,
    original_environment: String,
    environment: String,
    creator: User,
    created_at: String,
    updated_at: String
)

sealed abstract class DeploymentStatusState(val name: String) {
  override def toString: String = name
}

object DeploymentStatusState {
  val Values = List(Pending, Queued, InProgress, Success, Error, Failure, Inactive)

  def fromString(value: String): Option[DeploymentStatusState] =
    Values.find(_.name == value)

  case object Pending    extends DeploymentStatusState("pending")
  case object Queued     extends DeploymentStatusState("queued")
  case object InProgress extends DeploymentStatusState("in_progress")
  case object Success    extends DeploymentStatusState("success")
  case object Error      extends DeploymentStatusState("error")
  case object Failure    extends DeploymentStatusState("failure")
  case object Inactive   extends DeploymentStatusState("inactive")
}

case class DeploymentStatus(
    id: Int,
    state: DeploymentStatusState,
    environment: String,
    description: Option[String],
    environment_url: Option[String],
    target_url: Option[String],
    creator: User,
    created_at: String,
    updated_at: String
)

case class NewDeploymentRequest(
    ref: String,
    task: Option[String],
    environment: Option[String],
    auto_merge: Option[Boolean],
    required_contexts: Option[List[String]],
    payload: Option[Json]
)

case class NewDeploymentStatusRequest(
    state: DeploymentStatusState,
    environment: Option[String],
    description: Option[String],
    target_url: Option[String],
    environment_url: Option[String],
    auto_inactive: Option[Boolean]
)
