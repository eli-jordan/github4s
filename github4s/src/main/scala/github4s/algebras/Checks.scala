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
