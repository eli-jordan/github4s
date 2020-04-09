package github4s.interpreters

import cats.Functor
import cats.implicits._
import github4s.Decoders._
import github4s.Encoders._
import github4s.GithubResponses.GHResponse
import github4s.algebras.Checks
import github4s.domain._
import github4s.http.HttpClient

class ChecksInterpreter[F[_]: Functor](implicit client: HttpClient[F], accessToken: Option[String]) extends Checks[F] {
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
          "status"     -> status.map(_.toString),
        ).collect {
          case (key, Some(value)) => key -> value
        },
        pagination
      )
      .map { res =>
        res.copy(result = res.result.map(_.check_runs))
      }
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
          "status"     -> status.map(_.toString),
        ).collect {
          case (key, Some(value)) => key -> value
        },
        pagination
      )
      .map { res =>
        res.copy(result = res.result.map(_.check_runs))
      }
  }
}
