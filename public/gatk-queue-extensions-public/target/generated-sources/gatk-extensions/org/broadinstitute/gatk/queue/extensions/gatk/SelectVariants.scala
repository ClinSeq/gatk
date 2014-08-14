package org.broadinstitute.gatk.queue.extensions.gatk

import java.io.File
import org.broadinstitute.gatk.queue.function.scattergather.ScatterGatherableFunction
import org.broadinstitute.gatk.utils.commandline.Argument
import org.broadinstitute.gatk.utils.commandline.Gather
import org.broadinstitute.gatk.utils.commandline.Input
import org.broadinstitute.gatk.utils.commandline.Output

class SelectVariants extends org.broadinstitute.gatk.queue.extensions.gatk.CommandLineGATK with ScatterGatherableFunction {
analysisName = "SelectVariants"
analysis_type = "SelectVariants"
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

/** Output variants that were not called in this comparison track */
@Input(fullName="discordance", shortName="disc", doc="Output variants that were not called in this comparison track", required=false, exclusiveOf="", validation="")
var discordance: File = _

/**
 * Short name of discordance
 * @return Short name of discordance
 */
def disc = this.discordance

/**
 * Short name of discordance
 * @param value Short name of discordance
 */
def disc_=(value: File) { this.discordance = value }

/** Dependencies on the index of discordance */
@Input(fullName="discordanceIndex", shortName="", doc="Dependencies on the index of discordance", required=false, exclusiveOf="", validation="")
private var discordanceIndex: Seq[File] = Nil

/** Output variants that were also called in this comparison track */
@Input(fullName="concordance", shortName="conc", doc="Output variants that were also called in this comparison track", required=false, exclusiveOf="", validation="")
var concordance: File = _

/**
 * Short name of concordance
 * @return Short name of concordance
 */
def conc = this.concordance

/**
 * Short name of concordance
 * @param value Short name of concordance
 */
def conc_=(value: File) { this.concordance = value }

/** Dependencies on the index of concordance */
@Input(fullName="concordanceIndex", shortName="", doc="Dependencies on the index of concordance", required=false, exclusiveOf="", validation="")
private var concordanceIndex: Seq[File] = Nil

/** File to which variants should be written */
@Output(fullName="out", shortName="o", doc="File to which variants should be written", required=false, exclusiveOf="", validation="")
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

/** Include genotypes from this sample. Can be specified multiple times */
@Argument(fullName="sample_name", shortName="sn", doc="Include genotypes from this sample. Can be specified multiple times", required=false, exclusiveOf="", validation="")
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

/** Regular expression to select many samples from the ROD tracks provided. Can be specified multiple times */
@Argument(fullName="sample_expressions", shortName="se", doc="Regular expression to select many samples from the ROD tracks provided. Can be specified multiple times", required=false, exclusiveOf="", validation="")
var sample_expressions: Seq[String] = Nil

/**
 * Short name of sample_expressions
 * @return Short name of sample_expressions
 */
def se = this.sample_expressions

/**
 * Short name of sample_expressions
 * @param value Short name of sample_expressions
 */
def se_=(value: Seq[String]) { this.sample_expressions = value }

/** File containing a list of samples (one per line) to include. Can be specified multiple times */
@Input(fullName="sample_file", shortName="sf", doc="File containing a list of samples (one per line) to include. Can be specified multiple times", required=false, exclusiveOf="", validation="")
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

/** Exclude genotypes from this sample. Can be specified multiple times */
@Argument(fullName="exclude_sample_name", shortName="xl_sn", doc="Exclude genotypes from this sample. Can be specified multiple times", required=false, exclusiveOf="", validation="")
var exclude_sample_name: Seq[String] = Nil

/**
 * Short name of exclude_sample_name
 * @return Short name of exclude_sample_name
 */
def xl_sn = this.exclude_sample_name

/**
 * Short name of exclude_sample_name
 * @param value Short name of exclude_sample_name
 */
def xl_sn_=(value: Seq[String]) { this.exclude_sample_name = value }

/** File containing a list of samples (one per line) to exclude. Can be specified multiple times */
@Input(fullName="exclude_sample_file", shortName="xl_sf", doc="File containing a list of samples (one per line) to exclude. Can be specified multiple times", required=false, exclusiveOf="", validation="")
var exclude_sample_file: Seq[File] = Nil

/**
 * Short name of exclude_sample_file
 * @return Short name of exclude_sample_file
 */
def xl_sf = this.exclude_sample_file

/**
 * Short name of exclude_sample_file
 * @param value Short name of exclude_sample_file
 */
def xl_sf_=(value: Seq[File]) { this.exclude_sample_file = value }

/** One or more criteria to use when selecting the data */
@Argument(fullName="select_expressions", shortName="select", doc="One or more criteria to use when selecting the data", required=false, exclusiveOf="", validation="")
var select_expressions: Seq[String] = Nil

/**
 * Short name of select_expressions
 * @return Short name of select_expressions
 */
def select = this.select_expressions

/**
 * Short name of select_expressions
 * @param value Short name of select_expressions
 */
def select_=(value: Seq[String]) { this.select_expressions = value }

/** Don't include loci found to be non-variant after the subsetting procedure */
@Argument(fullName="excludeNonVariants", shortName="env", doc="Don't include loci found to be non-variant after the subsetting procedure", required=false, exclusiveOf="", validation="")
var excludeNonVariants: Boolean = _

/**
 * Short name of excludeNonVariants
 * @return Short name of excludeNonVariants
 */
def env = this.excludeNonVariants

/**
 * Short name of excludeNonVariants
 * @param value Short name of excludeNonVariants
 */
def env_=(value: Boolean) { this.excludeNonVariants = value }

/** Don't include filtered loci in the analysis */
@Argument(fullName="excludeFiltered", shortName="ef", doc="Don't include filtered loci in the analysis", required=false, exclusiveOf="", validation="")
var excludeFiltered: Boolean = _

/**
 * Short name of excludeFiltered
 * @return Short name of excludeFiltered
 */
def ef = this.excludeFiltered

/**
 * Short name of excludeFiltered
 * @param value Short name of excludeFiltered
 */
def ef_=(value: Boolean) { this.excludeFiltered = value }

/** Select only variants of a particular allelicity. Valid options are ALL (default), MULTIALLELIC or BIALLELIC */
@Argument(fullName="restrictAllelesTo", shortName="restrictAllelesTo", doc="Select only variants of a particular allelicity. Valid options are ALL (default), MULTIALLELIC or BIALLELIC", required=false, exclusiveOf="", validation="")
var restrictAllelesTo: org.broadinstitute.gatk.tools.walkers.variantutils.SelectVariants.NumberAlleleRestriction = _

/** Store the original AC, AF, and AN values in the INFO field after selecting (using keys AC_Orig, AF_Orig, and AN_Orig) */
@Argument(fullName="keepOriginalAC", shortName="keepOriginalAC", doc="Store the original AC, AF, and AN values in the INFO field after selecting (using keys AC_Orig, AF_Orig, and AN_Orig)", required=false, exclusiveOf="", validation="")
var keepOriginalAC: Boolean = _

/** output mendelian violation sites only */
@Argument(fullName="mendelianViolation", shortName="mv", doc="output mendelian violation sites only", required=false, exclusiveOf="", validation="")
var mendelianViolation: Boolean = _

/**
 * Short name of mendelianViolation
 * @return Short name of mendelianViolation
 */
def mv = this.mendelianViolation

/**
 * Short name of mendelianViolation
 * @param value Short name of mendelianViolation
 */
def mv_=(value: Boolean) { this.mendelianViolation = value }

/** Minimum genotype QUAL score for each trio member required to accept a site as a violation */
@Argument(fullName="mendelianViolationQualThreshold", shortName="mvq", doc="Minimum genotype QUAL score for each trio member required to accept a site as a violation", required=false, exclusiveOf="", validation="")
var mendelianViolationQualThreshold: Option[Double] = None

/**
 * Short name of mendelianViolationQualThreshold
 * @return Short name of mendelianViolationQualThreshold
 */
def mvq = this.mendelianViolationQualThreshold

/**
 * Short name of mendelianViolationQualThreshold
 * @param value Short name of mendelianViolationQualThreshold
 */
def mvq_=(value: Option[Double]) { this.mendelianViolationQualThreshold = value }

/** Format string for mendelianViolationQualThreshold */
@Argument(fullName="mendelianViolationQualThresholdFormat", shortName="", doc="Format string for mendelianViolationQualThreshold", required=false, exclusiveOf="", validation="")
var mendelianViolationQualThresholdFormat: String = "%s"

/** Selects a fraction (a number between 0 and 1) of the total variants at random from the variant track */
@Argument(fullName="select_random_fraction", shortName="fraction", doc="Selects a fraction (a number between 0 and 1) of the total variants at random from the variant track", required=false, exclusiveOf="", validation="")
var select_random_fraction: Option[Double] = None

/**
 * Short name of select_random_fraction
 * @return Short name of select_random_fraction
 */
def fraction = this.select_random_fraction

/**
 * Short name of select_random_fraction
 * @param value Short name of select_random_fraction
 */
def fraction_=(value: Option[Double]) { this.select_random_fraction = value }

/** Format string for select_random_fraction */
@Argument(fullName="select_random_fractionFormat", shortName="", doc="Format string for select_random_fraction", required=false, exclusiveOf="", validation="")
var select_random_fractionFormat: String = "%s"

/** Selects a fraction (a number between 0 and 1) of the total genotypes at random from the variant track and sets them to nocall */
@Argument(fullName="remove_fraction_genotypes", shortName="fractionGenotypes", doc="Selects a fraction (a number between 0 and 1) of the total genotypes at random from the variant track and sets them to nocall", required=false, exclusiveOf="", validation="")
var remove_fraction_genotypes: Option[Double] = None

/**
 * Short name of remove_fraction_genotypes
 * @return Short name of remove_fraction_genotypes
 */
def fractionGenotypes = this.remove_fraction_genotypes

/**
 * Short name of remove_fraction_genotypes
 * @param value Short name of remove_fraction_genotypes
 */
def fractionGenotypes_=(value: Option[Double]) { this.remove_fraction_genotypes = value }

/** Format string for remove_fraction_genotypes */
@Argument(fullName="remove_fraction_genotypesFormat", shortName="", doc="Format string for remove_fraction_genotypes", required=false, exclusiveOf="", validation="")
var remove_fraction_genotypesFormat: String = "%s"

/** Select only a certain type of variants from the input file. Valid types are INDEL, SNP, MIXED, MNP, SYMBOLIC, NO_VARIATION. Can be specified multiple times */
@Argument(fullName="selectTypeToInclude", shortName="selectType", doc="Select only a certain type of variants from the input file. Valid types are INDEL, SNP, MIXED, MNP, SYMBOLIC, NO_VARIATION. Can be specified multiple times", required=false, exclusiveOf="", validation="")
var selectTypeToInclude: Seq[htsjdk.variant.variantcontext.VariantContext.Type] = Nil

/**
 * Short name of selectTypeToInclude
 * @return Short name of selectTypeToInclude
 */
def selectType = this.selectTypeToInclude

/**
 * Short name of selectTypeToInclude
 * @param value Short name of selectTypeToInclude
 */
def selectType_=(value: Seq[htsjdk.variant.variantcontext.VariantContext.Type]) { this.selectTypeToInclude = value }

/** Only emit sites whose ID is found in this file (one ID per line) */
@Argument(fullName="keepIDs", shortName="IDs", doc="Only emit sites whose ID is found in this file (one ID per line)", required=false, exclusiveOf="", validation="")
var keepIDs: File = _

/**
 * Short name of keepIDs
 * @return Short name of keepIDs
 */
def IDs = this.keepIDs

/**
 * Short name of keepIDs
 * @param value Short name of keepIDs
 */
def IDs_=(value: File) { this.keepIDs = value }

/** If true, the incoming VariantContext will be fully decoded */
@Argument(fullName="fullyDecode", shortName="", doc="If true, the incoming VariantContext will be fully decoded", required=false, exclusiveOf="", validation="")
var fullyDecode: Boolean = _

/** If true, the incoming VariantContext will have its genotypes forcibly decoded by computing AC across all genotypes.  For efficiency testing only */
@Argument(fullName="forceGenotypesDecode", shortName="", doc="If true, the incoming VariantContext will have its genotypes forcibly decoded by computing AC across all genotypes.  For efficiency testing only", required=false, exclusiveOf="", validation="")
var forceGenotypesDecode: Boolean = _

/** If true, we won't actually write the output file.  For efficiency testing only */
@Argument(fullName="justRead", shortName="", doc="If true, we won't actually write the output file.  For efficiency testing only", required=false, exclusiveOf="", validation="")
var justRead: Boolean = _

/** indel size select */
@Argument(fullName="maxIndelSize", shortName="", doc="indel size select", required=false, exclusiveOf="", validation="")
var maxIndelSize: Option[Int] = None

/** Allow samples other than those in the VCF to be specified on the command line. These samples will be ignored. */
@Argument(fullName="ALLOW_NONOVERLAPPING_COMMAND_LINE_SAMPLES", shortName="", doc="Allow samples other than those in the VCF to be specified on the command line. These samples will be ignored.", required=false, exclusiveOf="", validation="")
var ALLOW_NONOVERLAPPING_COMMAND_LINE_SAMPLES: Boolean = _

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
if (discordance != null)
  discordanceIndex :+= new File(discordance.getPath + ".idx")
if (concordance != null)
  concordanceIndex :+= new File(concordance.getPath + ".idx")
if (out != null && !org.broadinstitute.gatk.utils.io.IOUtils.isSpecialFile(out))
  if (!org.broadinstitute.gatk.engine.io.stubs.VCFWriterArgumentTypeDescriptor.isCompressed(out.getPath))
    outIndex = new File(out.getPath + ".idx")
}

override def commandLine = super.commandLine + required(TaggedFile.formatCommandLineParameter("-V", variant), variant, spaceSeparated=true, escape=true, format="%s") + optional(TaggedFile.formatCommandLineParameter("-disc", discordance), discordance, spaceSeparated=true, escape=true, format="%s") + optional(TaggedFile.formatCommandLineParameter("-conc", concordance), concordance, spaceSeparated=true, escape=true, format="%s") + optional("-o", out, spaceSeparated=true, escape=true, format="%s") + conditional(no_cmdline_in_header, "-no_cmdline_in_header", escape=true, format="%s") + conditional(sites_only, "-sites_only", escape=true, format="%s") + conditional(bcf, "-bcf", escape=true, format="%s") + repeat("-sn", sample_name, spaceSeparated=true, escape=true, format="%s") + repeat("-se", sample_expressions, spaceSeparated=true, escape=true, format="%s") + repeat("-sf", sample_file, spaceSeparated=true, escape=true, format="%s") + repeat("-xl_sn", exclude_sample_name, spaceSeparated=true, escape=true, format="%s") + repeat("-xl_sf", exclude_sample_file, spaceSeparated=true, escape=true, format="%s") + repeat("-select", select_expressions, spaceSeparated=true, escape=true, format="%s") + conditional(excludeNonVariants, "-env", escape=true, format="%s") + conditional(excludeFiltered, "-ef", escape=true, format="%s") + optional("-restrictAllelesTo", restrictAllelesTo, spaceSeparated=true, escape=true, format="%s") + conditional(keepOriginalAC, "-keepOriginalAC", escape=true, format="%s") + conditional(mendelianViolation, "-mv", escape=true, format="%s") + optional("-mvq", mendelianViolationQualThreshold, spaceSeparated=true, escape=true, format=mendelianViolationQualThresholdFormat) + optional("-fraction", select_random_fraction, spaceSeparated=true, escape=true, format=select_random_fractionFormat) + optional("-fractionGenotypes", remove_fraction_genotypes, spaceSeparated=true, escape=true, format=remove_fraction_genotypesFormat) + repeat("-selectType", selectTypeToInclude, spaceSeparated=true, escape=true, format="%s") + optional("-IDs", keepIDs, spaceSeparated=true, escape=true, format="%s") + conditional(fullyDecode, "--fullyDecode", escape=true, format="%s") + conditional(forceGenotypesDecode, "--forceGenotypesDecode", escape=true, format="%s") + conditional(justRead, "--justRead", escape=true, format="%s") + optional("--maxIndelSize", maxIndelSize, spaceSeparated=true, escape=true, format="%s") + conditional(ALLOW_NONOVERLAPPING_COMMAND_LINE_SAMPLES, "--ALLOW_NONOVERLAPPING_COMMAND_LINE_SAMPLES", escape=true, format="%s") + conditional(filter_reads_with_N_cigar, "-filterRNC", escape=true, format="%s") + conditional(filter_mismatching_base_and_quals, "-filterMBQ", escape=true, format="%s") + conditional(filter_bases_not_stored, "-filterNoBases", escape=true, format="%s")
}
