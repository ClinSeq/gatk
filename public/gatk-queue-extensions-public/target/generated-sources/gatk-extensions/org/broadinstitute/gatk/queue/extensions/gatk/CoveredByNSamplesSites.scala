package org.broadinstitute.gatk.queue.extensions.gatk

import java.io.File
import org.broadinstitute.gatk.queue.function.scattergather.ScatterGatherableFunction
import org.broadinstitute.gatk.utils.commandline.Argument
import org.broadinstitute.gatk.utils.commandline.Gather
import org.broadinstitute.gatk.utils.commandline.Input
import org.broadinstitute.gatk.utils.commandline.Output

class CoveredByNSamplesSites extends org.broadinstitute.gatk.queue.extensions.gatk.CommandLineGATK with ScatterGatherableFunction {
analysisName = "CoveredByNSamplesSites"
analysis_type = "CoveredByNSamplesSites"
scatterClass = classOf[LocusScatterFunction]

/** Name of file for output intervals */
@Output(fullName="OutputIntervals", shortName="out", doc="Name of file for output intervals", required=false, exclusiveOf="", validation="")
@Gather(classOf[org.broadinstitute.gatk.queue.function.scattergather.SimpleTextGatherFunction])
var OutputIntervals: File = _

/**
 * Short name of OutputIntervals
 * @return Short name of OutputIntervals
 */
def out = this.OutputIntervals

/**
 * Short name of OutputIntervals
 * @param value Short name of OutputIntervals
 */
def out_=(value: File) { this.OutputIntervals = value }

/** Input VCF file */
@Input(fullName="variant", shortName="V", doc="Input VCF file", required=true, exclusiveOf="", validation="")
var variant: File = _

/**
 * Short name of variant
 * @return Short name of variant
 */
def V = this.variant

/**
 * Short name of variant
 * @param value Short name of variant
 */
def V_=(value: File) { this.variant = value }

/** Dependencies on the index of variant */
@Input(fullName="variantIndex", shortName="", doc="Dependencies on the index of variant", required=false, exclusiveOf="", validation="")
private var variantIndex: Seq[File] = Nil

/** only samples that have coverage bigger than minCoverage will be counted */
@Argument(fullName="minCoverage", shortName="minCov", doc="only samples that have coverage bigger than minCoverage will be counted", required=false, exclusiveOf="", validation="")
var minCoverage: Option[Int] = None

/**
 * Short name of minCoverage
 * @return Short name of minCoverage
 */
def minCov = this.minCoverage

/**
 * Short name of minCoverage
 * @param value Short name of minCoverage
 */
def minCov_=(value: Option[Int]) { this.minCoverage = value }

/** only sites where at least percentageOfSamples of the samples have good coverage, will be emitted */
@Argument(fullName="percentageOfSamples", shortName="percentage", doc="only sites where at least percentageOfSamples of the samples have good coverage, will be emitted", required=false, exclusiveOf="", validation="")
var percentageOfSamples: Option[Double] = None

/**
 * Short name of percentageOfSamples
 * @return Short name of percentageOfSamples
 */
def percentage = this.percentageOfSamples

/**
 * Short name of percentageOfSamples
 * @param value Short name of percentageOfSamples
 */
def percentage_=(value: Option[Double]) { this.percentageOfSamples = value }

/** Format string for percentageOfSamples */
@Argument(fullName="percentageOfSamplesFormat", shortName="", doc="Format string for percentageOfSamples", required=false, exclusiveOf="", validation="")
var percentageOfSamplesFormat: String = "%s"

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

override def freezeFieldValues() {
super.freezeFieldValues()
if (variant != null)
  variantIndex :+= new File(variant.getPath + ".idx")
}

override def commandLine = super.commandLine + optional("-out", OutputIntervals, spaceSeparated=true, escape=true, format="%s") + required(TaggedFile.formatCommandLineParameter("-V", variant), variant, spaceSeparated=true, escape=true, format="%s") + optional("-minCov", minCoverage, spaceSeparated=true, escape=true, format="%s") + optional("-percentage", percentageOfSamples, spaceSeparated=true, escape=true, format=percentageOfSamplesFormat) + conditional(filter_reads_with_N_cigar, "-filterRNC", escape=true, format="%s") + conditional(filter_mismatching_base_and_quals, "-filterMBQ", escape=true, format="%s") + conditional(filter_bases_not_stored, "-filterNoBases", escape=true, format="%s")
}
