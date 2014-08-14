package org.broadinstitute.gatk.queue.extensions.gatk

import org.broadinstitute.gatk.utils.commandline.Argument

trait ReadName extends org.broadinstitute.gatk.queue.function.CommandLineFunction {

/** Filter out all reads except those with this read name */
@Argument(fullName="readName", shortName="rn", doc="Filter out all reads except those with this read name", required=true, exclusiveOf="", validation="")
var readName: String = _

/**
 * Short name of readName
 * @return Short name of readName
 */
def rn = this.readName

/**
 * Short name of readName
 * @param value Short name of readName
 */
def rn_=(value: String) { this.readName = value }

abstract override def commandLine = super.commandLine + required("--read_filter", "ReadName") + required("-rn", readName, spaceSeparated=true, escape=true, format="%s")
}
