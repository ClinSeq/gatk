package org.broadinstitute.gatk.queue.extensions.gatk

import java.io.File
import org.broadinstitute.gatk.queue.function.scattergather.ScatterGatherableFunction
import org.broadinstitute.gatk.utils.commandline.Argument
import org.broadinstitute.gatk.utils.commandline.Gather
import org.broadinstitute.gatk.utils.commandline.Output

class ReadAdaptorTrimmer extends org.broadinstitute.gatk.queue.extensions.gatk.CommandLineGATK with ScatterGatherableFunction {
analysisName = "ReadAdaptorTrimmer"
analysis_type = "ReadAdaptorTrimmer"
scatterClass = classOf[ReadScatterFunction]

/** Write output to this BAM filename instead of STDOUT */
@Output(fullName="out", shortName="o", doc="Write output to this BAM filename instead of STDOUT", required=false, exclusiveOf="", validation="")
@Gather(classOf[BamGatherFunction])
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

/** Automatically generated index for out */
@Output(fullName="outIndex", shortName="", doc="Automatically generated index for out", required=false, exclusiveOf="", validation="")
@Gather(enabled=false)
private var outIndex: File = _

/** Automatically generated md5 for out */
@Output(fullName="outMD5", shortName="", doc="Automatically generated md5 for out", required=false, exclusiveOf="", validation="")
@Gather(enabled=false)
private var outMD5: File = _

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

/** Turn off on-the-fly creation of indices for output BAM files. */
@Argument(fullName="disable_bam_indexing", shortName="", doc="Turn off on-the-fly creation of indices for output BAM files.", required=false, exclusiveOf="", validation="")
var disable_bam_indexing: Boolean = _

/** Enable on-the-fly creation of md5s for output BAM files. */
@Argument(fullName="generate_md5", shortName="", doc="Enable on-the-fly creation of md5s for output BAM files.", required=false, exclusiveOf="", validation="")
var generate_md5: Boolean = _

/** If provided, output BAM files will be simplified to include just key reads for downstream variation discovery analyses (removing duplicates, PF-, non-primary reads), as well stripping all extended tags from the kept reads except the read group identifier */
@Argument(fullName="simplifyBAM", shortName="simplifyBAM", doc="If provided, output BAM files will be simplified to include just key reads for downstream variation discovery analyses (removing duplicates, PF-, non-primary reads), as well stripping all extended tags from the kept reads except the read group identifier", required=false, exclusiveOf="", validation="")
var simplifyBAM: Boolean = _

/** Print the first n reads from the file, discarding the rest */
@Argument(fullName="number", shortName="n", doc="Print the first n reads from the file, discarding the rest", required=false, exclusiveOf="", validation="")
var number: Option[Int] = None

/**
 * Short name of number
 * @return Short name of number
 */
def n = this.number

/**
 * Short name of number
 * @param value Short name of number
 */
def n_=(value: Option[Int]) { this.number = value }

/** Minimum number of substring matches to detect pair overlaps */
@Argument(fullName="minMatches", shortName="minMatches", doc="Minimum number of substring matches to detect pair overlaps", required=false, exclusiveOf="", validation="")
var minMatches: Option[Int] = None

/** Remove unpaired reads instead of erroring out */
@Argument(fullName="removeUnpairedReads", shortName="removeUnpairedReads", doc="Remove unpaired reads instead of erroring out", required=false, exclusiveOf="", validation="")
var removeUnpairedReads: Boolean = _

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
if (out != null && !org.broadinstitute.gatk.utils.io.IOUtils.isSpecialFile(out))
  if (!disable_bam_indexing)
    outIndex = new File(out.getPath.stripSuffix(".bam") + ".bai")
if (out != null && !org.broadinstitute.gatk.utils.io.IOUtils.isSpecialFile(out))
  if (generate_md5)
    outMD5 = new File(out.getPath + ".md5")
}

override def commandLine = super.commandLine + optional("-o", out, spaceSeparated=true, escape=true, format="%s") + optional("-compress", bam_compression, spaceSeparated=true, escape=true, format="%s") + conditional(disable_bam_indexing, "--disable_bam_indexing", escape=true, format="%s") + conditional(generate_md5, "--generate_md5", escape=true, format="%s") + conditional(simplifyBAM, "-simplifyBAM", escape=true, format="%s") + optional("-n", number, spaceSeparated=true, escape=true, format="%s") + optional("-minMatches", minMatches, spaceSeparated=true, escape=true, format="%s") + conditional(removeUnpairedReads, "-removeUnpairedReads", escape=true, format="%s") + conditional(filter_reads_with_N_cigar, "-filterRNC", escape=true, format="%s") + conditional(filter_mismatching_base_and_quals, "-filterMBQ", escape=true, format="%s") + conditional(filter_bases_not_stored, "-filterNoBases", escape=true, format="%s")
}
