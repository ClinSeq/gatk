package org.broadinstitute.gatk.queue.extensions.gatk

import java.io.File
import org.broadinstitute.gatk.queue.function.scattergather.ScatterGatherableFunction
import org.broadinstitute.gatk.utils.commandline.Argument
import org.broadinstitute.gatk.utils.commandline.Gather
import org.broadinstitute.gatk.utils.commandline.Input
import org.broadinstitute.gatk.utils.commandline.Output

class BeagleOutputToVCF extends org.broadinstitute.gatk.queue.extensions.gatk.CommandLineGATK with ScatterGatherableFunction {
analysisName = "BeagleOutputToVCF"
analysis_type = "BeagleOutputToVCF"
scatterClass = classOf[LocusScatterFunction]

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

/** Comparison VCF file */
@Input(fullName="comp", shortName="comp", doc="Comparison VCF file", required=false, exclusiveOf="", validation="")
var comp: File = _

/** Dependencies on the index of comp */
@Input(fullName="compIndex", shortName="", doc="Dependencies on the index of comp", required=false, exclusiveOf="", validation="")
private var compIndex: Seq[File] = Nil

/** Beagle-produced .r2 file containing R^2 values for all markers */
@Input(fullName="beagleR2", shortName="beagleR2", doc="Beagle-produced .r2 file containing R^2 values for all markers", required=true, exclusiveOf="", validation="")
var beagleR2: File = _

/** Dependencies on the index of beagleR2 */
@Input(fullName="beagleR2Index", shortName="", doc="Dependencies on the index of beagleR2", required=false, exclusiveOf="", validation="")
private var beagleR2Index: Seq[File] = Nil

/** Beagle-produced .probs file containing posterior genotype probabilities */
@Input(fullName="beagleProbs", shortName="beagleProbs", doc="Beagle-produced .probs file containing posterior genotype probabilities", required=true, exclusiveOf="", validation="")
var beagleProbs: File = _

/** Dependencies on the index of beagleProbs */
@Input(fullName="beagleProbsIndex", shortName="", doc="Dependencies on the index of beagleProbs", required=false, exclusiveOf="", validation="")
private var beagleProbsIndex: Seq[File] = Nil

/** Beagle-produced .phased file containing phased genotypes */
@Input(fullName="beaglePhased", shortName="beaglePhased", doc="Beagle-produced .phased file containing phased genotypes", required=true, exclusiveOf="", validation="")
var beaglePhased: File = _

/** Dependencies on the index of beaglePhased */
@Input(fullName="beaglePhasedIndex", shortName="", doc="Dependencies on the index of beaglePhased", required=false, exclusiveOf="", validation="")
private var beaglePhasedIndex: Seq[File] = Nil

/** VCF File to which variants should be written */
@Output(fullName="out", shortName="o", doc="VCF File to which variants should be written", required=false, exclusiveOf="", validation="")
@Gather(classOf[CatVariantsGatherer])
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

/** Don't output the usual VCF header tag with the command line. FOR DEBUGGING PURPOSES ONLY. This option is required in order to pass integration tests. */
@Argument(fullName="no_cmdline_in_header", shortName="no_cmdline_in_header", doc="Don't output the usual VCF header tag with the command line. FOR DEBUGGING PURPOSES ONLY. This option is required in order to pass integration tests.", required=false, exclusiveOf="", validation="")
var no_cmdline_in_header: Boolean = _

/** Just output sites without genotypes (i.e. only the first 8 columns of the VCF) */
@Argument(fullName="sites_only", shortName="sites_only", doc="Just output sites without genotypes (i.e. only the first 8 columns of the VCF)", required=false, exclusiveOf="", validation="")
var sites_only: Boolean = _

/** force BCF output, regardless of the file's extension */
@Argument(fullName="bcf", shortName="bcf", doc="force BCF output, regardless of the file's extension", required=false, exclusiveOf="", validation="")
var bcf: Boolean = _

/** If provided, we won't filter sites that beagle tags as monomorphic.  Useful for imputing a sample's genotypes from a reference panel */
@Argument(fullName="dont_mark_monomorphic_sites_as_filtered", shortName="keep_monomorphic", doc="If provided, we won't filter sites that beagle tags as monomorphic.  Useful for imputing a sample's genotypes from a reference panel", required=false, exclusiveOf="", validation="")
var dont_mark_monomorphic_sites_as_filtered: Boolean = _

/**
 * Short name of dont_mark_monomorphic_sites_as_filtered
 * @return Short name of dont_mark_monomorphic_sites_as_filtered
 */
def keep_monomorphic = this.dont_mark_monomorphic_sites_as_filtered

/**
 * Short name of dont_mark_monomorphic_sites_as_filtered
 * @param value Short name of dont_mark_monomorphic_sites_as_filtered
 */
def keep_monomorphic_=(value: Boolean) { this.dont_mark_monomorphic_sites_as_filtered = value }

/** Threshold of confidence at which a genotype won't be called */
@Argument(fullName="nocall_threshold", shortName="ncthr", doc="Threshold of confidence at which a genotype won't be called", required=false, exclusiveOf="", validation="")
var nocall_threshold: Option[Double] = None

/**
 * Short name of nocall_threshold
 * @return Short name of nocall_threshold
 */
def ncthr = this.nocall_threshold

/**
 * Short name of nocall_threshold
 * @param value Short name of nocall_threshold
 */
def ncthr_=(value: Option[Double]) { this.nocall_threshold = value }

/** Format string for nocall_threshold */
@Argument(fullName="nocall_thresholdFormat", shortName="", doc="Format string for nocall_threshold", required=false, exclusiveOf="", validation="")
var nocall_thresholdFormat: String = "%s"

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
if (comp != null)
  compIndex :+= new File(comp.getPath + ".idx")
if (beagleR2 != null)
  beagleR2Index :+= new File(beagleR2.getPath + ".idx")
if (beagleProbs != null)
  beagleProbsIndex :+= new File(beagleProbs.getPath + ".idx")
if (beaglePhased != null)
  beaglePhasedIndex :+= new File(beaglePhased.getPath + ".idx")
if (out != null && !org.broadinstitute.gatk.utils.io.IOUtils.isSpecialFile(out))
  if (!org.broadinstitute.gatk.engine.io.stubs.VCFWriterArgumentTypeDescriptor.isCompressed(out.getPath))
    outIndex = new File(out.getPath + ".idx")
}

override def commandLine = super.commandLine + required(TaggedFile.formatCommandLineParameter("-V", variant), variant, spaceSeparated=true, escape=true, format="%s") + optional(TaggedFile.formatCommandLineParameter("-comp", comp), comp, spaceSeparated=true, escape=true, format="%s") + required(TaggedFile.formatCommandLineParameter("-beagleR2", beagleR2), beagleR2, spaceSeparated=true, escape=true, format="%s") + required(TaggedFile.formatCommandLineParameter("-beagleProbs", beagleProbs), beagleProbs, spaceSeparated=true, escape=true, format="%s") + required(TaggedFile.formatCommandLineParameter("-beaglePhased", beaglePhased), beaglePhased, spaceSeparated=true, escape=true, format="%s") + optional("-o", out, spaceSeparated=true, escape=true, format="%s") + conditional(no_cmdline_in_header, "-no_cmdline_in_header", escape=true, format="%s") + conditional(sites_only, "-sites_only", escape=true, format="%s") + conditional(bcf, "-bcf", escape=true, format="%s") + conditional(dont_mark_monomorphic_sites_as_filtered, "-keep_monomorphic", escape=true, format="%s") + optional("-ncthr", nocall_threshold, spaceSeparated=true, escape=true, format=nocall_thresholdFormat) + conditional(filter_reads_with_N_cigar, "-filterRNC", escape=true, format="%s") + conditional(filter_mismatching_base_and_quals, "-filterMBQ", escape=true, format="%s") + conditional(filter_bases_not_stored, "-filterNoBases", escape=true, format="%s")
}
