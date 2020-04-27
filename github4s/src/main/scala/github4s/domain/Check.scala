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

sealed abstract class CheckRunStatus(val name: String) {
  override def toString: String = name
}
object CheckRunStatus {
  object Queued     extends CheckRunStatus("queued")
  object InProgress extends CheckRunStatus("in_progress")
  object Completed  extends CheckRunStatus("completed")

  val Values = List(Queued, InProgress, Completed)

  def fromString(value: String): Option[CheckRunStatus] = Values.find(_.name == value)
}

sealed abstract class CheckRunConclusion(val name: String) {
  override def toString: String = name
}
object CheckRunConclusion {
  object Success        extends CheckRunConclusion("success")
  object Failure        extends CheckRunConclusion("failure")
  object Neutral        extends CheckRunConclusion("neutral")
  object Cancelled      extends CheckRunConclusion("cancelled")
  object TimedOut       extends CheckRunConclusion("timed_out")
  object ActionRequired extends CheckRunConclusion("action_required")

  val Values = List(
    Success,
    Failure,
    Neutral,
    Cancelled,
    TimedOut,
    ActionRequired
  )

  def fromString(value: String): Option[CheckRunConclusion] = Values.find(_.name == value)
}

case class CheckRun(
    id: Option[Int] = None,
    name: Option[String] = None,
    head_sha: Option[String] = None,
    details_url: Option[String] = None,
    external_id: Option[String] = None,
    external_url: Option[String] = None,
    status: CheckRunStatus,
    started_at: Option[Timestamp] = None,
    conclusion: Option[CheckRunConclusion] = None,
    check_suite: Option[CheckRunSuiteId] = None,
    completed_at: Option[Timestamp] = None,
    output: Option[CheckRunOutput] = None,
    actions: Option[List[CheckRunAction]] = None
)

case class CheckRunSuiteId(id: Int)

case class CheckRunOutput(
    title: Option[String] = None,
    summary: Option[String] = None,
    text: Option[String] = None,
    annotations: Option[List[CheckRunAnnotation]] = None,
    images: Option[List[CheckRunImage]] = None
)

case class CheckRunAnnotation(
    path: String,
    start_line: Int,
    end_line: Int,
    start_column: Option[Int],
    end_column: Option[Int],
    annotation_level: String,
    message: String,
    title: Option[String],
    raw_details: Option[String]
)

case class CheckRunImage(
    alt: String,
    image_url: String,
    caption: Option[String] = None
)

case class CheckRunAction(
    label: String,
    description: String,
    identifier: String
)

case class App(
    id: Int,
    name: String,
    description: String,
    owner: User,
    slug: Option[String]
)

case class CheckSuite(
    id: Int,
    head_branch: String,
    head_sha: String,
    status: CheckRunStatus,
    conclusion: Option[CheckRunConclusion],
    before: String,
    after: String,
    pull_requests: List[CheckSuitePullRequest],
    app: App
)

case class CheckSuitePullRequestBranch(sha: String, ref: String)
case class CheckSuitePullRequest(
    number: Int,
    base: CheckSuitePullRequestBranch,
    head: CheckSuitePullRequestBranch
)

case class ListCheckRunsResponse(total_count: Int, check_runs: List[CheckRun])
