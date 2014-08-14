package org.broadinstitute.gatk.queue.extensions.gatk

import java.io.File
import org.broadinstitute.gatk.queue.function.scattergather.ScatterGatherableFunction
import org.broadinstitute.gatk.utils.commandline.Argument
import org.broadinstitute.gatk.utils.commandline.Gather
import org.broadinstitute.gatk.utils.commandline.Output

class PrintReads extends org.broadinstitute.gatk.queue.extensions.gatk.CommandLineGATK with ScatterGatherableFunction {
analysisName = "PrintReads"
analysis_type = "PrintReads"
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

/** Exclude all reads with this read group from the output */
@Argument(fullName="readGroup", shortName="readGroup", doc="Exclude all reads with this read group from the output", required=false, exclusiveOf="", validation="")
var readGroup: String = _

/** Exclude all reads with this platform from the output */
@Argument(fullName="platform", shortName="platform", doc="Exclude all reads with this platform from the output", required=false, exclusiveOf="", validation="")
var platform: String = _

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

/** File containing a list of samples (one per line). Can be specified multiple times */
@Argument(fullName="sample_file", shortName="sf", doc="File containing a list of samples (one per line). Can be specified multiple times", required=false, exclusiveOf="", validation="")
var sample_file: Seq[File] = Nil

/**
 * Short name of sample_file
 * @return Short name of sample_file
 */
def sf = this.sample_file

/**
 * Short name of sample_file
 * @param value Short name of sample_file
 */
def sf_=(value: Seq[File]) { this.sample_file = value }

/** Sample name to be included in the analysis. Can be specified multiple times. */
@Argument(fullName="sample_name", shortName="sn", doc="Sample name to be included in the analysis. Can be specified multiple times.", required=false, exclusiveOf="", validation="")
var sample_name: Seq[String] = Nil

/**
 * Short name of sample_name
 * @return Short name of sample_name
 */
def sn = this.sample_name

/**
 * Short name of sample_name
 * @param value Short name of sample_name
 */
def sn_=(value: Seq[String]) { this.sample_name = value }

/** Simplify all reads. */
@Argument(fullName="simplify", shortName="s", doc="Simplify all reads.", required=false, exclusiveOf="", validation="")
var simplify: Boolean = _

/**
 * Short name of simplify
 * @return Short name of simplify
 */
def s = this.simplify

/**
 * Short name of simplify
 * @param value Short name of simplify
 */
def s_=(value: Boolean) { this.simplify = value }

/**  */
@Argument(fullName="no_pg_tag", shortName="npt", doc="", required=false, exclusiveOf="", validation="")
var no_pg_tag: Boolean = _

/**
 * Short name of no_pg_tag
 * @return Short name of no_pg_tag
 */
def npt = this.no_pg_tag

/**
 * Short name of no_pg_tag
 * @param value Short name of no_pg_tag
 */
def npt_=(value: Boolean) { this.no_pg_tag = value }

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

override def commandLine = super.commandLine + optional("-o", out, spaceSeparated=true, escape=true, format="%s") + optional("-compress", bam_compression, spaceSeparated=true, escape=true, format="%s") + conditional(disable_bam_indexing, "--disable_bam_indexing", escape=true, format="%s") + conditional(generate_md5, "--generate_md5", escape=true, format="%s") + conditional(simplifyBAM, "-simplifyBAM", escape=true, format="%s") + optional("-readGroup", readGroup, spaceSeparated=true, escape=true, format="%s") + optional("-platform", platform, spaceSeparated=true, escape=true, format="%s") + optional("-n", number, spaceSeparated=true, escape=true, format="%s") + repeat("-sf", sample_file, spaceSeparated=true, escape=true, format="%s") + repeat("-sn", sample_name, spaceSeparated=true, escape=true, format="%s") + conditional(simplify, "-s", escape=true, format="%s") + conditional(no_pg_tag, "-npt", escape=true, format="%s") + conditional(filter_reads_with_N_cigar, "-filterRNC", escape=true, format="%s") + conditional(filter_mismatching_base_and_quals, "-filterMBQ", escape=true, format="%s") + conditional(filter_bases_not_stored, "-filterNoBases", escape=true, format="%s")
}
