package hu.vandra.webformz.helpers

import akka.actor.{ActorRefFactory, ActorLogging}
import org.webjars.{MultipleMatchesException, WebJarAssetLocator}
import spray.http.StatusCodes
import spray.routing.Directives
import spray.routing.directives.ContentTypeResolver
import scala.collection.JavaConverters._

import scala.util._

trait WebJarSupport extends Directives { this: ActorLogging =>

  val webJarLocator: WebJarAssetLocator

  def getFromWebJar(asset: String)(implicit resolver: ContentTypeResolver, refFactory: ActorRefFactory) = {
    webJarLocator.getWebJars


    Try { webJarLocator.getFullPath(asset) } match {
      case Success(path) => getFromResource(path)
      case Failure(e: MultipleMatchesException) => log.error(s"${e.getMessage} ${e.getMatches}"); complete { StatusCodes.InternalServerError }
      case Failure(e) => log.error(e.getMessage); reject
    }
  }

  def getFromWebJar(webJar: String, asset: String)(implicit resolver: ContentTypeResolver, refFactory: ActorRefFactory) = {
    Try { webJarLocator.getFullPath(webJar, asset) } match {
      case Success(path) => getFromResource(path)
      case Failure(e: MultipleMatchesException) => log.warning(s"${e.getMessage} ${e.getMatches}"); getFromResource(e.getMatches.asScala.head)
      case Failure(e) => log.error(e.getMessage); reject
    }
  }

}
