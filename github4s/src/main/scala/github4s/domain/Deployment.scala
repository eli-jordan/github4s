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
