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

import cats.data.NonEmptyList
import com.github.marklister.base64.Base64._
import github4s.Decoders._
import github4s.Encoders._
import github4s.GithubResponses.GHResponse
import github4s.algebras.Repositories
import github4s.domain._
import github4s.http.HttpClient
import io.circe.Json

class RepositoriesInterpreter[F[_]](implicit client: HttpClient[F], accessToken: Option[String])
  extends Repositories[F] {
  override def get(
      owner: String,
      repo: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[Repository]] =
    client.get[Repository](accessToken, s"repos/$owner/$repo", headers)

  override def listOrgRepos(
      org: String,
      `type`: Option[String],
      pagination: Option[Pagination],
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Repository]]] =
    client.get[List[Repository]](
      accessToken,
      s"orgs/$org/repos",
      headers,
      `type`.fold(Map.empty[String, String])(t => Map("type" -> t)),
      pagination
    )

  override def listUserRepos(
      user: String,
      `type`: Option[String],
      pagination: Option[Pagination],
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Repository]]] =
    client.get[List[Repository]](
      accessToken,
      s"users/$user/repos",
      headers,
      `type`.fold(Map.empty[String, String])(t => Map("type" -> t)),
      pagination
    )

  override def getContents(
      owner: String,
      repo: String,
      path: String,
      ref: Option[String],
      headers: Map[String, String] = Map()
  ): F[GHResponse[NonEmptyList[Content]]] =
    client.get[NonEmptyList[Content]](
      accessToken,
      s"repos/$owner/$repo/contents/$path",
      headers,
      ref.fold(Map.empty[String, String])(r => Map("ref" -> r))
    )

  override def createFile(
      owner: String,
      repo: String,
      path: String,
      message: String,
      content: Array[Byte],
      branch: Option[String],
      committer: Option[Committer],
      author: Option[Committer],
      headers: Map[String, String] = Map()
  ): F[GHResponse[WriteFileResponse]] =
    client.put[WriteFileRequest, WriteFileResponse](
      accessToken,
      s"repos/$owner/$repo/contents/$path",
      headers,
      WriteFileRequest(message, content.toBase64, None, branch, committer, author)
    )

  override def updateFile(
      owner: String,
      repo: String,
      path: String,
      message: String,
      content: Array[Byte],
      sha: String,
      branch: Option[String],
      committer: Option[Committer],
      author: Option[Committer],
      headers: Map[String, String] = Map()
  ): F[GHResponse[WriteFileResponse]] =
    client.put[WriteFileRequest, WriteFileResponse](
      accessToken,
      s"repos/$owner/$repo/contents/$path",
      headers,
      WriteFileRequest(message, content.toBase64, Some(sha), branch, committer, author)
    )

  override def deleteFile(
      owner: String,
      repo: String,
      path: String,
      message: String,
      sha: String,
      branch: Option[String],
      committer: Option[Committer],
      author: Option[Committer],
      headers: Map[String, String] = Map()
  ): F[GHResponse[WriteFileResponse]] =
    client.deleteWithBody[DeleteFileRequest, WriteFileResponse](
      accessToken,
      s"repos/$owner/$repo/contents/$path",
      headers,
      DeleteFileRequest(message, sha, branch, committer, author)
    )

  override def listCommits(
      owner: String,
      repo: String,
      sha: Option[String],
      path: Option[String],
      author: Option[String],
      since: Option[String],
      until: Option[String],
      pagination: Option[Pagination],
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Commit]]] =
    client.get[List[Commit]](
      accessToken,
      s"repos/$owner/$repo/commits",
      headers,
      Map(
        "sha"    -> sha,
        "path"   -> path,
        "author" -> author,
        "since"  -> since,
        "until"  -> until
      ).collect {
        case (key, Some(value)) => key -> value
      },
      pagination
    )

  override def listBranches(
      owner: String,
      repo: String,
      onlyProtected: Option[Boolean],
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Branch]]] =
    client.get[List[Branch]](
      accessToken,
      s"repos/$owner/$repo/branches",
      headers,
      Map(
        "protected" -> onlyProtected.map(_.toString)
      ).collect {
        case (key, Some(value)) => key -> value
      }
    )

  override def listContributors(
      owner: String,
      repo: String,
      anon: Option[String],
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[User]]] =
    client.get[List[User]](
      accessToken,
      s"repos/$owner/$repo/contributors",
      headers,
      Map(
        "anon" -> anon
      ).collect {
        case (key, Some(value)) => key -> value
      }
    )

  override def listCollaborators(
      owner: String,
      repo: String,
      affiliation: Option[String],
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[User]]] =
    client.get[List[User]](
      accessToken,
      s"repos/$owner/$repo/collaborators",
      headers,
      Map(
        "affiliation" -> affiliation
      ).collect {
        case (key, Some(value)) => key -> value
      }
    )

  override def createRelease(
      owner: String,
      repo: String,
      tagName: String,
      name: String,
      body: String,
      targetCommitish: Option[String],
      draft: Option[Boolean],
      prerelease: Option[Boolean],
      headers: Map[String, String] = Map()
  ): F[GHResponse[Release]] =
    client.post[NewReleaseRequest, Release](
      accessToken,
      s"repos/$owner/$repo/releases",
      headers,
      NewReleaseRequest(tagName, name, body, targetCommitish, draft, prerelease)
    )

  override def getCombinedStatus(
      owner: String,
      repo: String,
      ref: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[CombinedStatus]] =
    client.get[CombinedStatus](accessToken, s"repos/$owner/$repo/commits/$ref/status", headers)

  override def listStatuses(
      owner: String,
      repo: String,
      ref: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Status]]] =
    client.get[List[Status]](accessToken, s"repos/$owner/$repo/commits/$ref/statuses", headers)

  override def createStatus(
      owner: String,
      repo: String,
      sha: String,
      state: String,
      target_url: Option[String],
      description: Option[String],
      context: Option[String],
      headers: Map[String, String] = Map()
  ): F[GHResponse[Status]] =
    client.post[NewStatusRequest, Status](
      accessToken,
      s"repos/$owner/$repo/statuses/$sha",
      headers,
      NewStatusRequest(state, target_url, description, context)
    )

  override def listPullsForCommit(
      owner: String,
      repo: String,
      sha: String,
      headers: Map[String, String] = Map.empty
  ): F[GHResponse[List[PullRequest]]] = {
    client.get[List[PullRequest]](
      accessToken = accessToken,
      method = s"repos/$owner/$repo/commits/$sha/pulls",
      headers = Map("Accept" -> "application/vnd.github.groot-preview+json") ++ headers
    )
  }

  override def createDeployment(
      owner: String,
      repo: String,
      ref: String,
      task: Option[String],
      environment: Option[String],
      auto_merge: Option[Boolean],
      required_contexts: Option[List[String]],
      payload: Option[Json],
      headers: Map[String, String]
  ): F[GHResponse[Deployment]] = {
    client.post[NewDeploymentRequest, Deployment](
      accessToken = accessToken,
      url = s"repos/$owner/$repo/deployments",
      data = NewDeploymentRequest(
        ref = ref,
        task = task,
        environment = environment,
        auto_merge = auto_merge,
        required_contexts = required_contexts,
        payload = payload
      ),
      headers = headers
    )
  }

  override def createDeploymentStatus(
      owner: String,
      repo: String,
      deploymentId: Int,
      environment: String,
      state: DeploymentStatusState,
      description: Option[String],
      target_url: Option[String],
      environment_url: Option[String],
      auto_inactive: Option[Boolean],
      headers: Map[String, String]
  ): F[GHResponse[DeploymentStatus]] = {
    client.post[NewDeploymentStatusRequest, DeploymentStatus](
      accessToken = accessToken,
      url = s"repos/$owner/$repo/deployments/$deploymentId/statuses",
      data = NewDeploymentStatusRequest(
        environment = Some(environment),
        state = state,
        description = description,
        target_url = target_url,
        environment_url = environment_url,
        auto_inactive = auto_inactive
      ),
      headers = Map("Accept" -> "application/vnd.github.flash-preview+ant-man-preview+json") ++ headers
    )
  }

  // /repos/:owner/:repo/deployments
  override def listDeployments(
      owner: String,
      repo: String,
      sha: Option[String],
      ref: Option[String],
      task: Option[String],
      environment: Option[String],
      pagination: Option[Pagination],
      headers: Map[String, String]
  ): F[GHResponse[List[Deployment]]] = {
    client.get[List[Deployment]](
      accessToken,
      s"repos/$owner/$repo/deployments",
      headers,
      Map(
        "sha"         -> sha,
        "ref"         -> ref,
        "task"        -> task,
        "environment" -> environment,
      ).collect {
        case (key, Some(value)) => key -> value
      },
      pagination
    )
  }

  override def listDeploymentStatuses(
      owner: String,
      repo: String,
      deploymentId: Int,
      pagination: Option[Pagination],
      headers: Map[String, String]
  ): F[GHResponse[List[DeploymentStatus]]] = {
    client.get[List[DeploymentStatus]](
      accessToken,
      s"repos/$owner/$repo/deployments/$deploymentId/statuses",
      headers ++ Map("Accept" -> "application/vnd.github.flash-preview+ant-man-preview+json"),
      Map.empty,
      pagination
    )
  }
}
