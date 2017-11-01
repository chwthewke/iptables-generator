package net.chwthewke.iptables

import cats.data.NonEmptyList
import cats.syntax.show._
import com.monovore.decline._
import java.nio.file.Path

object Main
    extends CommandApp(
      name = IptablesgenBuildInfo.name,
      header = "",
      version = IptablesgenBuildInfo.version,
      main = {
        Opts
          .arguments[Path]( metavar = "file" )
          .orNone
          .map( _.fold[List[Path]]( Nil )( _.toList ) )
          .map( run( _ ) )
      }
    )

object run {
  def apply( paths: List[Path] ): Unit = {
    load.fromPaths( paths: _* ).fold( onErrors, onSuccess )
  }

  def onErrors( errors: NonEmptyList[Error] ): Unit =
    Console.err.println( errors.foldLeft( "Errors:" )( ( acc, e ) => acc + "\n  " + e.show + s" ($e)" ) )

  def onSuccess( config: IptablesConfig ): Unit =
    Console.out.println( generate( config ) )

}
