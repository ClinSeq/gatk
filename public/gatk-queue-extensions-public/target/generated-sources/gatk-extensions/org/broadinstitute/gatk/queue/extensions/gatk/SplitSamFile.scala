package org.broadinstitute.gatk.queue.extensions.gatk

import org.broadinstitute.gatk.queue.function.scattergather.ScatterGatherableFunction
import org.broadinstitute.gatk.utils.commandline.Argument

class SplitSamFile extends org.broadinstitute.gatk.queue.extensions.gatk.CommandLineGATK with ScatterGatherableFunction {
analysisName = "SplitSamFile"
analysis_type = "SplitSamFile"
scatterClass = classOf[ReadScatterFunction]

/** output BAM file */
@Argument(fullName="outputRoot", shortName="", doc="output BAM file", required=false, exclusiveOf="", validation="")
var outputRoot: String = _

/** Compression level to use for writing BAM files */
@Argument(fullName="bam_compression", shortName="compress", doc="Compression level to use for writing BAM files", required=false, exclusiveOf="", validation="")
var bam_compression: Option[Int] = None

/**
 * Short name of bam_compression
 * @return Short name of bam_compression
 */
def compress = this.bam_compression

/**
 * Short name of bam_compression
 * @param value Short name of bam_compression
 */
def compress_=(value: Option[Int]) { this.bam_compression = value }

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

override def commandLine = super.commandLine + optional("--outputRoot", outputRoot, spaceSeparated=true, escape=true, format="%s") + optional("-compress", bam_compression, spaceSeparated=true, escape=true, format="%s") + conditional(filter_reads_with_N_cigar, "-filterRNC", escape=true, format="%s") + conditional(filter_mismatching_base_and_quals, "-filterMBQ", escape=true, format="%s") + conditional(filter_bases_not_stored, "-filterNoBases", escape=true, format="%s")
}
