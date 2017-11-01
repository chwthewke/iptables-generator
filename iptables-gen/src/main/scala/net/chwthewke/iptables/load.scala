package net.chwthewke.iptables

import cats.Foldable
import cats.data.NonEmptyList
import cats.instances.list._
import cats.syntax.either._
import cats.syntax.traverse._
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.nio.file.Path

object load {

  def wrapConfigFactory( cfg: => Config ): Either[NonEmptyList[IoError], Config] =
    Either.catchNonFatal( cfg ).leftMap( e => NonEmptyList.of( IoError( e ) ) )

  def fromConfig( cfg: Config ): Either[NonEmptyList[PureconfigError], IptablesConfig] =
    config.fromConfig( cfg ).leftMap( _.map( PureconfigError ) ).toEither

  def fromString( string: String ): ConfigLoadResult =
    (for {
      cfg    <- wrapConfigFactory( ConfigFactory.parseString( string ) )
      result <- fromConfig( cfg )
    } yield result).toValidated

  def fromPath( path: Path ): ConfigLoadResult =
    (for {
      cfg    <- wrapConfigFactory( ConfigFactory.parseFile( path.toFile ) )
      result <- fromConfig( cfg )
    } yield result).toValidated

  def fromPaths( paths: Path* ): ConfigLoadResult =
    paths.toList.traverse( fromPath ).map( fa => Foldable[List].fold( fa ) )

}
