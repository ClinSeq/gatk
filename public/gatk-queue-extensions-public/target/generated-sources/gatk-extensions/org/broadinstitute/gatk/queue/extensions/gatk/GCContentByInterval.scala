package org.broadinstitute.gatk.queue.extensions.gatk

import java.io.File
import org.broadinstitute.gatk.queue.function.scattergather.ScatterGatherableFunction
import org.broadinstitute.gatk.utils.commandline.Argument
import org.broadinstitute.gatk.utils.commandline.Gather
import org.broadinstitute.gatk.utils.commandline.Output

class GCContentByInterval extends org.broadinstitute.gatk.queue.extensions.gatk.CommandLineGATK with ScatterGatherableFunction {
analysisName = "GCContentByInterval"
analysis_type = "GCContentByInterval"
scatterClass = classOf[LocusScatterFunction]

/** An output file created by the walker.  Will overwrite contents if file exists */
@Output(fullName="out", shortName="o", doc="An output file created by the walker.  Will overwrite contents if file exists", required=false, exclusiveOf="", validation="")
@Gather(classOf[org.broadinstitute.gatk.queue.function.scattergather.SimpleTextGatherFunction])
var out: File = _

/**
 * Short name of out
 * @return Short name of out
 */
def o = this.out

/**
 * Short name of out
 * @param value Short name of out
 */
def o_=(value: File) { this.out = value }

/** filter out reads with CIGAR containing the N operator, instead of stop processing and report an error. */
@Argument(fullName="filter_reads_with_N_cigar", shortName="filterRNC", doc="filter out reads with CIGAR containing the N operator, instead of stop processing and report an error.", required=false, exclusiveOf="", validation="")
var filter_reads_with_N_cigar: Boolean = _

/**
 * Short name of filter_reads_with_N_cigar
 * @return Short name of filter_reads_with_N_cigar
 */
def filterRNC = this.filter_reads_with_N_cigar

/**
 * Short name of filter_reads_with_N_cigar
 * @param value Short name of filter_reads_with_N_cigar
 */
def filterRNC_=(value: Boolean) { this.filter_reads_with_N_cigar = value }

/** if a read has mismatching number of bases and base qualities, filter out the read instead of blowing up. */
@Argument(fullName="filter_mismatching_base_and_quals", shortName="filterMBQ", doc="if a read has mismatching number of bases and base qualities, filter out the read instead of blowing up.", required=false, exclusiveOf="", validation="")
var filter_mismatching_base_and_quals: Boolean = _

/**
 * Short name of filter_mismatching_base_and_quals
 * @return Short name of filter_mismatching_base_and_quals
 */
def filterMBQ = this.filter_mismatching_base_and_quals

/**
 * Short name of filter_mismatching_base_and_quals
 * @param value Short name of filter_mismatching_base_and_quals
 */
def filterMBQ_=(value: Boolean) { this.filter_mismatching_base_and_quals = value }

/** if a read has no stored bases (i.e. a '*'), filter out the read instead of blowing up. */
@Argument(fullName="filter_bases_not_stored", shortName="filterNoBases", doc="if a read has no stored bases (i.e. a '*'), filter out the read instead of blowing up.", required=false, exclusiveOf="", validation="")
var filter_bases_not_stored: Boolean = _

/**
 * Short name of filter_bases_not_stored
 * @return Short name of filter_bases_not_stored
 */
def filterNoBases = this.filter_bases_not_stored

/**
 * Short name of filter_bases_not_stored
 * @param value Short name of filter_bases_not_stored
 */
def filterNoBases_=(value: Boolean) { this.filter_bases_not_stored = value }

override def commandLine = super.commandLine + optional("-o", out, spaceSeparated=true, escape=true, format="%s") + conditional(filter_reads_with_N_cigar, "-filterRNC", escape=true, format="%s") + conditional(filter_mismatching_base_and_quals, "-filterMBQ", escape=true, format="%s") + conditional(filter_bases_not_stored, "-filterNoBases", escape=true, format="%s")
}
