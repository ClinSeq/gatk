package org.broadinstitute.gatk.queue.extensions.gatk

import java.io.File
import org.broadinstitute.gatk.queue.function.scattergather.ScatterGatherableFunction
import org.broadinstitute.gatk.utils.commandline.Argument
import org.broadinstitute.gatk.utils.commandline.Gather
import org.broadinstitute.gatk.utils.commandline.Input
import org.broadinstitute.gatk.utils.commandline.Output

class ProduceBeagleInput extends org.broadinstitute.gatk.queue.extensions.gatk.CommandLineGATK with ScatterGatherableFunction {
analysisName = "ProduceBeagleInput"
analysis_type = "ProduceBeagleInput"
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

/** Validation VCF file */
@Input(fullName="validation", shortName="validation", doc="Validation VCF file", required=false, exclusiveOf="", validation="")
var validation: File = _

/** Dependencies on the index of validation */
@Input(fullName="validationIndex", shortName="", doc="Dependencies on the index of validation", required=false, exclusiveOf="", validation="")
private var validationIndex: Seq[File] = Nil

/** File to which BEAGLE input should be written */
@Output(fullName="out", shortName="o", doc="File to which BEAGLE input should be written", required=false, exclusiveOf="", validation="")
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

/** File to which BEAGLE markers should be written */
@Output(fullName="markers", shortName="markers", doc="File to which BEAGLE markers should be written", required=false, exclusiveOf="", validation="")
@Gather(classOf[org.broadinstitute.gatk.queue.function.scattergather.SimpleTextGatherFunction])
var markers: File = _

/** VQSqual calibration file */
@Input(fullName="vqsrcalibrationfile", shortName="cc", doc="VQSqual calibration file", required=false, exclusiveOf="", validation="")
var vqsrcalibrationfile: File = _

/**
 * Short name of vqsrcalibrationfile
 * @return Short name of vqsrcalibrationfile
 */
def cc = this.vqsrcalibrationfile

/**
 * Short name of vqsrcalibrationfile
 * @param value Short name of vqsrcalibrationfile
 */
def cc_=(value: File) { this.vqsrcalibrationfile = value }

/** VQSqual key */
@Argument(fullName="vqslod_key", shortName="vqskey", doc="VQSqual key", required=false, exclusiveOf="", validation="")
var vqslod_key: String = _

/**
 * Short name of vqslod_key
 * @return Short name of vqslod_key
 */
def vqskey = this.vqslod_key

/**
 * Short name of vqslod_key
 * @param value Short name of vqslod_key
 */
def vqskey_=(value: String) { this.vqslod_key = value }

/** Rate (0-1) at which genotype no-calls will be randomly inserted, for testing */
@Argument(fullName="inserted_nocall_rate", shortName="nc_rate", doc="Rate (0-1) at which genotype no-calls will be randomly inserted, for testing", required=false, exclusiveOf="", validation="")
var inserted_nocall_rate: Option[Double] = None

/**
 * Short name of inserted_nocall_rate
 * @return Short name of inserted_nocall_rate
 */
def nc_rate = this.inserted_nocall_rate

/**
 * Short name of inserted_nocall_rate
 * @param value Short name of inserted_nocall_rate
 */
def nc_rate_=(value: Option[Double]) { this.inserted_nocall_rate = value }

/** Format string for inserted_nocall_rate */
@Argument(fullName="inserted_nocall_rateFormat", shortName="", doc="Format string for inserted_nocall_rate", required=false, exclusiveOf="", validation="")
var inserted_nocall_rateFormat: String = "%s"

/** Flat probability to assign to validation genotypes. Will override GL field. */
@Argument(fullName="validation_genotype_ptrue", shortName="valp", doc="Flat probability to assign to validation genotypes. Will override GL field.", required=false, exclusiveOf="", validation="")
var validation_genotype_ptrue: Option[Double] = None

/**
 * Short name of validation_genotype_ptrue
 * @return Short name of validation_genotype_ptrue
 */
def valp = this.validation_genotype_ptrue

/**
 * Short name of validation_genotype_ptrue
 * @param value Short name of validation_genotype_ptrue
 */
def valp_=(value: Option[Double]) { this.validation_genotype_ptrue = value }

/** Format string for validation_genotype_ptrue */
@Argument(fullName="validation_genotype_ptrueFormat", shortName="", doc="Format string for validation_genotype_ptrue", required=false, exclusiveOf="", validation="")
var validation_genotype_ptrueFormat: String = "%s"

/** Proportion of records to be used in bootstrap set */
@Argument(fullName="validation_bootstrap", shortName="bs", doc="Proportion of records to be used in bootstrap set", required=false, exclusiveOf="", validation="")
var validation_bootstrap: Option[Double] = None

/**
 * Short name of validation_bootstrap
 * @return Short name of validation_bootstrap
 */
def bs = this.validation_bootstrap

/**
 * Short name of validation_bootstrap
 * @param value Short name of validation_bootstrap
 */
def bs_=(value: Option[Double]) { this.validation_bootstrap = value }

/** Format string for validation_bootstrap */
@Argument(fullName="validation_bootstrapFormat", shortName="", doc="Format string for validation_bootstrap", required=false, exclusiveOf="", validation="")
var validation_bootstrapFormat: String = "%s"

/** Output a VCF with the records used for bootstrapping filtered out */
@Argument(fullName="bootstrap_vcf", shortName="bvcf", doc="Output a VCF with the records used for bootstrapping filtered out", required=false, exclusiveOf="", validation="")
var bootstrap_vcf: File = _

/**
 * Short name of bootstrap_vcf
 * @return Short name of bootstrap_vcf
 */
def bvcf = this.bootstrap_vcf

/**
 * Short name of bootstrap_vcf
 * @param value Short name of bootstrap_vcf
 */
def bvcf_=(value: File) { this.bootstrap_vcf = value }

/** Don't output the usual VCF header tag with the command line. FOR DEBUGGING PURPOSES ONLY. This option is required in order to pass integration tests. */
@Argument(fullName="no_cmdline_in_header", shortName="no_cmdline_in_header", doc="Don't output the usual VCF header tag with the command line. FOR DEBUGGING PURPOSES ONLY. This option is required in order to pass integration tests.", required=false, exclusiveOf="", validation="")
var no_cmdline_in_header: Boolean = _

/** Just output sites without genotypes (i.e. only the first 8 columns of the VCF) */
@Argument(fullName="sites_only", shortName="sites_only", doc="Just output sites without genotypes (i.e. only the first 8 columns of the VCF)", required=false, exclusiveOf="", validation="")
var sites_only: Boolean = _

/** force BCF output, regardless of the file's extension */
@Argument(fullName="bcf", shortName="bcf", doc="force BCF output, regardless of the file's extension", required=false, exclusiveOf="", validation="")
var bcf: Boolean = _

/** Set to true when Beagle-ing chrX and want to ensure male samples don't have heterozygous calls. */
@Argument(fullName="checkIsMaleOnChrX", shortName="checkIsMaleOnChrX", doc="Set to true when Beagle-ing chrX and want to ensure male samples don't have heterozygous calls.", required=false, exclusiveOf="", validation="")
var checkIsMaleOnChrX: Boolean = _

/** Flat probability prior to assign to variant (not validation) genotypes. Does not override GL field. */
@Argument(fullName="variant_genotype_ptrue", shortName="varp", doc="Flat probability prior to assign to variant (not validation) genotypes. Does not override GL field.", required=false, exclusiveOf="", validation="")
var variant_genotype_ptrue: Option[Double] = None

/**
 * Short name of variant_genotype_ptrue
 * @return Short name of variant_genotype_ptrue
 */
def varp = this.variant_genotype_ptrue

/**
 * Short name of variant_genotype_ptrue
 * @param value Short name of variant_genotype_ptrue
 */
def varp_=(value: Option[Double]) { this.variant_genotype_ptrue = value }

/** Format string for variant_genotype_ptrue */
@Argument(fullName="variant_genotype_ptrueFormat", shortName="", doc="Format string for variant_genotype_ptrue", required=false, exclusiveOf="", validation="")
var variant_genotype_ptrueFormat: String = "%s"

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
if (validation != null)
  validationIndex :+= new File(validation.getPath + ".idx")
}

override def commandLine = super.commandLine + required(TaggedFile.formatCommandLineParameter("-V", variant), variant, spaceSeparated=true, escape=true, format="%s") + optional(TaggedFile.formatCommandLineParameter("-validation", validation), validation, spaceSeparated=true, escape=true, format="%s") + optional("-o", out, spaceSeparated=true, escape=true, format="%s") + optional("-markers", markers, spaceSeparated=true, escape=true, format="%s") + optional("-cc", vqsrcalibrationfile, spaceSeparated=true, escape=true, format="%s") + optional("-vqskey", vqslod_key, spaceSeparated=true, escape=true, format="%s") + optional("-nc_rate", inserted_nocall_rate, spaceSeparated=true, escape=true, format=inserted_nocall_rateFormat) + optional("-valp", validation_genotype_ptrue, spaceSeparated=true, escape=true, format=validation_genotype_ptrueFormat) + optional("-bs", validation_bootstrap, spaceSeparated=true, escape=true, format=validation_bootstrapFormat) + optional("-bvcf", bootstrap_vcf, spaceSeparated=true, escape=true, format="%s") + conditional(no_cmdline_in_header, "-no_cmdline_in_header", escape=true, format="%s") + conditional(sites_only, "-sites_only", escape=true, format="%s") + conditional(bcf, "-bcf", escape=true, format="%s") + conditional(checkIsMaleOnChrX, "-checkIsMaleOnChrX", escape=true, format="%s") + optional("-varp", variant_genotype_ptrue, spaceSeparated=true, escape=true, format=variant_genotype_ptrueFormat) + conditional(filter_reads_with_N_cigar, "-filterRNC", escape=true, format="%s") + conditional(filter_mismatching_base_and_quals, "-filterMBQ", escape=true, format="%s") + conditional(filter_bases_not_stored, "-filterNoBases", escape=true, format="%s")
}
