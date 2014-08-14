package org.broadinstitute.gatk.queue.extensions.gatk

import org.broadinstitute.gatk.utils.commandline.Argument

trait MaxInsertSize extends org.broadinstitute.gatk.queue.function.CommandLineFunction {

/** Discard reads with insert size greater than the specified value, defaults to 1000000 */
@Argument(fullName="maxInsertSize", shortName="maxInsert", doc="Discard reads with insert size greater than the specified value, defaults to 1000000", required=false, exclusiveOf="", validation="")
var maxInsertSize: Option[Int] = None

/**
 * Short name of maxInsertSize
 * @return Short name of maxInsertSize
 */
def maxInsert = this.maxInsertSize

/**
 * Short name of maxInsertSize
 * @param value Short name of maxInsertSize
 */
def maxInsert_=(value: Option[Int]) { this.maxInsertSize = value }

abstract override def commandLine = super.commandLine + required("--read_filter", "MaxInsertSize") + optional("-maxInsert", maxInsertSize, spaceSeparated=true, escape=true, format="%s")
}
