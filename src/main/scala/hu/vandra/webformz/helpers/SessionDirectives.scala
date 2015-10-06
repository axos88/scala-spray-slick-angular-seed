package hu.vandra.webformz.helpers

import java.util.Date

import akka.actor.ActorLogging
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import shapeless.{HNil, ::}
import spray.http.HttpHeaders.RawHeader
import spray.http.StatusCodes
import spray.httpx.marshalling.ToResponseMarshallable
import spray.routing.Directives._
import spray.routing._
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.{Failure, Try}

import scala.language.implicitConversions

case class SessionKey(value: String) extends AnyVal

trait SessionDirectives { logger: ActorLogging =>
  val sessionHeaderName = "X-Session"

  val MissingSessionKeyHandler: RejectionHandler.PF = { case scala.collection.immutable.::(MissingSessionKeyRejection(key), _) => complete(StatusCodes.BadRequest, s"Missing Session Key: $key") }


  case class MissingSessionKeyRejection(key: String) extends Rejection

  case class Session(original: mutable.Map[String, AnyRef], key: SessionKey) extends mutable.Map[String, AnyRef] {
    private var _dirty = false
    def isDirty = _dirty

    override def +=(kv: (String, AnyRef)): Session.this.type = {
      _dirty = true
      original += kv
      this
    }

    override def -=(key: String): Session.this.type = {
      original -= key
      this
    }

    override def get(key: String): Option[AnyRef] = original.get(key)
    override def iterator: Iterator[(String, AnyRef)] = original.iterator

    def dump = Session.dump(this)
  }

  object Session {
    class JWTVerificationError extends RuntimeException


    def empty(key: SessionKey) = Session(mutable.Map(), key)

    def parse(token: String, key: SessionKey): Try[Session] = {
      Try {
        val signedJWT = SignedJWT.parse(token)
        val verifier = new MACVerifier(key.value)

        signedJWT.verify(verifier) match {
          case true =>
            val immutable = signedJWT.getJWTClaimsSet.getCustomClaims.asScala
            val map = mutable.Map(immutable.toList: _*)
            Session(map, key)
          case false =>
            logger.log.warning(s"Unable to verify signature for jwt: $token")
            throw new JWTVerificationError
        }
      }
    }
    def dump(session: Session): String = {
      val signer = new MACSigner(session.key.value)

      // Prepare JWT with claims set
      val claimsSet = new JWTClaimsSet()
      claimsSet.setExpirationTime(new Date(new Date().getTime + 3600 * 1000)) // 1 hour
      claimsSet.setNotBeforeTime(new Date())
      claimsSet.setIssueTime(new Date())
      claimsSet.setCustomClaims(session.asJava)

      val signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet)

      // Apply the HMAC protection
      signedJWT.sign(signer)
      signedJWT.serialize()
    }
  }

  class Magnet[T, U](val value: T, val payload: U)

  object Magnet {
    implicit def magnetize[T, U](value: T)(implicit state: U) = new Magnet(value, state)
  }

  private def optionalSessionHeader = optionalHeaderValueByName(sessionHeaderName)
  private def sessionHeader = headerValueByName(sessionHeaderName)
  private def createSessionHeader(value: String) = RawHeader(sessionHeaderName, value)

  def withSession(key: SessionKey): Directive1[Session] = {
    optionalSessionHeader.map { header =>
      header.flatMap { jwtToken =>
        Session.parse(jwtToken, key).recoverWith {
          case e =>
            logger.log.warning("Unable to parse JWT Token")
            Failure(e)
        }.toOption
      }.getOrElse(Session.empty(key))
    }
  }

  def withOptionalSessionKey[T](key: Magnet[String, Session]): Directive1[Option[T]] = {
    val magnet = key
    provide(magnet.payload.get(magnet.value).map(_.asInstanceOf[T]))
  }

  private def _withOptionalSessionKeys(keys: Magnet[List[String], Session]): Directive1[List[Option[AnyRef]]] = {
    val magnet = keys
    val values = magnet.value.map(magnet.payload.get)
    provide(values)
  }


  private type HListOfOptionAnyRef1 = Option[AnyRef] :: HNil
  private type HListOfOptionAnyRef2 = Option[AnyRef] :: HListOfOptionAnyRef1
  private type HListOfOptionAnyRef3 = Option[AnyRef] :: HListOfOptionAnyRef2
  private type HListOfOptionAnyRef4 = Option[AnyRef] :: HListOfOptionAnyRef3
  private type HListOfOptionAnyRef5 = Option[AnyRef] :: HListOfOptionAnyRef4

  def withOptionalSessionKeys(key0: Magnet[String, Session], key1: String): Directive[HListOfOptionAnyRef2] = {
    implicit val session = key0.payload

    _withOptionalSessionKeys(List(key0.value, key1)).flatMap { values =>
      hprovide(values(0) :: values(1) :: HNil)
    }
  }

  def withOptionalSessionKeys(key0: Magnet[String, Session], key1: String, key2: String): Directive[HListOfOptionAnyRef3] = {
    implicit val session = key0.payload

    _withOptionalSessionKeys(List(key0.value, key1, key2)).flatMap { values =>
      hprovide(values(0) :: values(1) :: values(2) :: HNil)
    }
  }

  def withOptionalSessionKeys(key0: Magnet[String, Session], key1: String, key2: String, key3: String): Directive[HListOfOptionAnyRef4] = {
    implicit val session = key0.payload

    _withOptionalSessionKeys(List(key0.value, key1, key2, key3)).flatMap { values =>
      hprovide(values(0) :: values(1) :: values(2) :: values(3) :: HNil)
    }
  }

  def withOptionalSessionKeys(key0: Magnet[String, Session], key1: String, key2: String, key3: String, key4: String): Directive[HListOfOptionAnyRef5] = {
    implicit val session = key0.payload

    _withOptionalSessionKeys(List(key0.value, key1, key2)).flatMap { values =>
      hprovide(values(0) :: values(1) :: values(2) :: values(3) :: values(4) :: HNil)
    }
  }


  def withSessionKey[T](key: Magnet[String, Session]): Directive1[T] =
    withOptionalSessionKey[T](key).flatMap {
      case None => reject(MissingSessionKeyRejection(key.value))
      case Some(value) => provide(value)
    }

  def completeWithSession(marshallable: ⇒ ToResponseMarshallable)(implicit session: Session): StandardRoute =
    new StandardRoute {
      def apply(ctx: RequestContext): Unit = {
        //Eager execute marshallable to detect changes in session
        val result = marshallable

        if (session.isDirty)
          ctx.withHttpResponseHeadersMapped(createSessionHeader(session.dump) :: _).complete(result)
        else
          ctx.complete(result)
      }
    }
}

