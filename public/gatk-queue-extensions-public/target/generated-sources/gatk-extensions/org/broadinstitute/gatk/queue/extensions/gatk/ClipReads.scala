package org.broadinstitute.gatk.queue.extensions.gatk

import java.io.File
import org.broadinstitute.gatk.queue.function.scattergather.ScatterGatherableFunction
import org.broadinstitute.gatk.utils.commandline.Argument
import org.broadinstitute.gatk.utils.commandline.Gather
import org.broadinstitute.gatk.utils.commandline.Output

class ClipReads extends org.broadinstitute.gatk.queue.extensions.gatk.CommandLineGATK with ScatterGatherableFunction {
analysisName = "ClipReads"
analysis_type = "ClipReads"
scatterClass = classOf[ReadScatterFunction]

/** File to output statistics */
@Output(fullName="outputStatistics", shortName="os", doc="File to output statistics", required=false, exclusiveOf="", validation="")
@Gather(classOf[org.broadinstitute.gatk.queue.function.scattergather.SimpleTextGatherFunction])
var outputStatistics: File = _

/**
 * Short name of outputStatistics
 * @return Short name of outputStatistics
 */
def os = this.outputStatistics

/**
 * Short name of outputStatistics
 * @param value Short name of outputStatistics
 */
def os_=(value: File) { this.outputStatistics = value }

/** Write BAM output here */
@Output(fullName="out", shortName="o", doc="Write BAM output here", required=false, exclusiveOf="", validation="")
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

/** If provided, the Q-score clipper will be applied */
@Argument(fullName="qTrimmingThreshold", shortName="QT", doc="If provided, the Q-score clipper will be applied", required=false, exclusiveOf="", validation="")
var qTrimmingThreshold: Option[Int] = None

/**
 * Short name of qTrimmingThreshold
 * @return Short name of qTrimmingThreshold
 */
def QT = this.qTrimmingThreshold

/**
 * Short name of qTrimmingThreshold
 * @param value Short name of qTrimmingThreshold
 */
def QT_=(value: Option[Int]) { this.qTrimmingThreshold = value }

/** String indicating machine cycles to clip from the reads */
@Argument(fullName="cyclesToTrim", shortName="CT", doc="String indicating machine cycles to clip from the reads", required=false, exclusiveOf="", validation="")
var cyclesToTrim: String = _

/**
 * Short name of cyclesToTrim
 * @return Short name of cyclesToTrim
 */
def CT = this.cyclesToTrim

/**
 * Short name of cyclesToTrim
 * @param value Short name of cyclesToTrim
 */
def CT_=(value: String) { this.cyclesToTrim = value }

/** Remove sequences within reads matching the sequences in this FASTA file */
@Argument(fullName="clipSequencesFile", shortName="XF", doc="Remove sequences within reads matching the sequences in this FASTA file", required=false, exclusiveOf="", validation="")
var clipSequencesFile: String = _

/**
 * Short name of clipSequencesFile
 * @return Short name of clipSequencesFile
 */
def XF = this.clipSequencesFile

/**
 * Short name of clipSequencesFile
 * @param value Short name of clipSequencesFile
 */
def XF_=(value: String) { this.clipSequencesFile = value }

/** Remove sequences within reads matching this sequence */
@Argument(fullName="clipSequence", shortName="X", doc="Remove sequences within reads matching this sequence", required=false, exclusiveOf="", validation="")
var clipSequence: Seq[String] = Nil

/**
 * Short name of clipSequence
 * @return Short name of clipSequence
 */
def X = this.clipSequence

/**
 * Short name of clipSequence
 * @param value Short name of clipSequence
 */
def X_=(value: Seq[String]) { this.clipSequence = value }

/** How should we actually clip the bases? */
@Argument(fullName="clipRepresentation", shortName="CR", doc="How should we actually clip the bases?", required=false, exclusiveOf="", validation="")
var clipRepresentation: org.broadinstitute.gatk.utils.clipping.ClippingRepresentation = _

/**
 * Short name of clipRepresentation
 * @return Short name of clipRepresentation
 */
def CR = this.clipRepresentation

/**
 * Short name of clipRepresentation
 * @param value Short name of clipRepresentation
 */
def CR_=(value: org.broadinstitute.gatk.utils.clipping.ClippingRepresentation) { this.clipRepresentation = value }

/**  */
@Argument(fullName="read", shortName="", doc="", required=false, exclusiveOf="", validation="")
var read: String = _

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

override def commandLine = super.commandLine + optional("-os", outputStatistics, spaceSeparated=true, escape=true, format="%s") + optional("-o", out, spaceSeparated=true, escape=true, format="%s") + optional("-compress", bam_compression, spaceSeparated=true, escape=true, format="%s") + conditional(disable_bam_indexing, "--disable_bam_indexing", escape=true, format="%s") + conditional(generate_md5, "--generate_md5", escape=true, format="%s") + conditional(simplifyBAM, "-simplifyBAM", escape=true, format="%s") + optional("-QT", qTrimmingThreshold, spaceSeparated=true, escape=true, format="%s") + optional("-CT", cyclesToTrim, spaceSeparated=true, escape=true, format="%s") + optional("-XF", clipSequencesFile, spaceSeparated=true, escape=true, format="%s") + repeat("-X", clipSequence, spaceSeparated=true, escape=true, format="%s") + optional("-CR", clipRepresentation, spaceSeparated=true, escape=true, format="%s") + optional("--read", read, spaceSeparated=true, escape=true, format="%s") + conditional(filter_reads_with_N_cigar, "-filterRNC", escape=true, format="%s") + conditional(filter_mismatching_base_and_quals, "-filterMBQ", escape=true, format="%s") + conditional(filter_bases_not_stored, "-filterNoBases", escape=true, format="%s")
}
