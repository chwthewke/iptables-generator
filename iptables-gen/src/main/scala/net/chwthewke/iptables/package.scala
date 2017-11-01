package net.chwthewke

import cats.data.ValidatedNel
import pureconfig.error.ConfigReaderFailure
import scala.collection.immutable

package object iptables {

  type Seq[+X] = immutable.Seq[X]
  val Seq: immutable.Seq.type = immutable.Seq

  type IndexedSeq[+X] = immutable.IndexedSeq[X]
  val IndexedSeq: immutable.IndexedSeq.type = immutable.IndexedSeq

  type ConfigParseResult = ValidatedNel[ConfigReaderFailure, IptablesConfig]
  type ConfigLoadResult  = ValidatedNel[Error, IptablesConfig]

}
