package org.broadinstitute.gatk.queue.extensions.gatk

import java.io.File
import org.broadinstitute.gatk.queue.function.scattergather.ScatterGatherableFunction
import org.broadinstitute.gatk.utils.commandline.Argument
import org.broadinstitute.gatk.utils.commandline.Gather
import org.broadinstitute.gatk.utils.commandline.Input
import org.broadinstitute.gatk.utils.commandline.Output

class CombineVariants extends org.broadinstitute.gatk.queue.extensions.gatk.CommandLineGATK with ScatterGatherableFunction {
analysisName = "CombineVariants"
analysis_type = "CombineVariants"
scatterClass = classOf[LocusScatterFunction]

/** Input VCF file */
@Input(fullName="variant", shortName="V", doc="Input VCF file", required=true, exclusiveOf="", validation="")
var variant: Seq[File] = Nil

/**
 * Short name of variant
 * @return Short name of variant
 */
def V = this.variant

/**
 * Short name of variant
 * @param value Short name of variant
 */
def V_=(value: Seq[File]) { this.variant = value }

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

/** Determines how we should merge genotype records for samples shared across the ROD files */
@Argument(fullName="genotypemergeoption", shortName="genotypeMergeOptions", doc="Determines how we should merge genotype records for samples shared across the ROD files", required=false, exclusiveOf="", validation="")
var genotypemergeoption: org.broadinstitute.gatk.utils.variant.GATKVariantContextUtils.GenotypeMergeType = _

/**
 * Short name of genotypemergeoption
 * @return Short name of genotypemergeoption
 */
def genotypeMergeOptions = this.genotypemergeoption

/**
 * Short name of genotypemergeoption
 * @param value Short name of genotypemergeoption
 */
def genotypeMergeOptions_=(value: org.broadinstitute.gatk.utils.variant.GATKVariantContextUtils.GenotypeMergeType) { this.genotypemergeoption = value }

/** Determines how we should handle records seen at the same site in the VCF, but with different FILTER fields */
@Argument(fullName="filteredrecordsmergetype", shortName="filteredRecordsMergeType", doc="Determines how we should handle records seen at the same site in the VCF, but with different FILTER fields", required=false, exclusiveOf="", validation="")
var filteredrecordsmergetype: org.broadinstitute.gatk.utils.variant.GATKVariantContextUtils.FilteredRecordMergeType = _

/**
 * Short name of filteredrecordsmergetype
 * @return Short name of filteredrecordsmergetype
 */
def filteredRecordsMergeType = this.filteredrecordsmergetype

/**
 * Short name of filteredrecordsmergetype
 * @param value Short name of filteredrecordsmergetype
 */
def filteredRecordsMergeType_=(value: org.broadinstitute.gatk.utils.variant.GATKVariantContextUtils.FilteredRecordMergeType) { this.filteredrecordsmergetype = value }

/** Determines how we should handle records seen at the same site in the VCF, but with different allele types (for example, SNP vs. indel) */
@Argument(fullName="multipleallelesmergetype", shortName="multipleAllelesMergeType", doc="Determines how we should handle records seen at the same site in the VCF, but with different allele types (for example, SNP vs. indel)", required=false, exclusiveOf="", validation="")
var multipleallelesmergetype: org.broadinstitute.gatk.utils.variant.GATKVariantContextUtils.MultipleAllelesMergeType = _

/**
 * Short name of multipleallelesmergetype
 * @return Short name of multipleallelesmergetype
 */
def multipleAllelesMergeType = this.multipleallelesmergetype

/**
 * Short name of multipleallelesmergetype
 * @param value Short name of multipleallelesmergetype
 */
def multipleAllelesMergeType_=(value: org.broadinstitute.gatk.utils.variant.GATKVariantContextUtils.MultipleAllelesMergeType) { this.multipleallelesmergetype = value }

/** A comma-separated string describing the priority ordering for the genotypes as far as which record gets emitted */
@Argument(fullName="rod_priority_list", shortName="priority", doc="A comma-separated string describing the priority ordering for the genotypes as far as which record gets emitted", required=false, exclusiveOf="", validation="")
var rod_priority_list: String = _

/**
 * Short name of rod_priority_list
 * @return Short name of rod_priority_list
 */
def priority = this.rod_priority_list

/**
 * Short name of rod_priority_list
 * @param value Short name of rod_priority_list
 */
def priority_=(value: String) { this.rod_priority_list = value }

/** Print out interesting sites requiring complex compatibility merging */
@Argument(fullName="printComplexMerges", shortName="printComplexMerges", doc="Print out interesting sites requiring complex compatibility merging", required=false, exclusiveOf="", validation="")
var printComplexMerges: Boolean = _

/** If true, then filtered VCFs are treated as uncalled, so that filtered set annotations don't appear in the combined VCF */
@Argument(fullName="filteredAreUncalled", shortName="filteredAreUncalled", doc="If true, then filtered VCFs are treated as uncalled, so that filtered set annotations don't appear in the combined VCF", required=false, exclusiveOf="", validation="")
var filteredAreUncalled: Boolean = _

/** If true, then the output VCF will contain no INFO or genotype FORMAT fields */
@Argument(fullName="minimalVCF", shortName="minimalVCF", doc="If true, then the output VCF will contain no INFO or genotype FORMAT fields", required=false, exclusiveOf="", validation="")
var minimalVCF: Boolean = _

/** Don't include loci found to be non-variant after the combining procedure */
@Argument(fullName="excludeNonVariants", shortName="env", doc="Don't include loci found to be non-variant after the combining procedure", required=false, exclusiveOf="", validation="")
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

/** Key used in the INFO key=value tag emitted describing which set the combined VCF record came from */
@Argument(fullName="setKey", shortName="setKey", doc="Key used in the INFO key=value tag emitted describing which set the combined VCF record came from", required=false, exclusiveOf="", validation="")
var setKey: String = _

/** If true, assume input VCFs have identical sample sets and disjoint calls */
@Argument(fullName="assumeIdenticalSamples", shortName="assumeIdenticalSamples", doc="If true, assume input VCFs have identical sample sets and disjoint calls", required=false, exclusiveOf="", validation="")
var assumeIdenticalSamples: Boolean = _

/** Combine variants and output site only if the variant is present in at least N input files. */
@Argument(fullName="minimumN", shortName="minN", doc="Combine variants and output site only if the variant is present in at least N input files.", required=false, exclusiveOf="", validation="")
var minimumN: Option[Int] = None

/**
 * Short name of minimumN
 * @return Short name of minimumN
 */
def minN = this.minimumN

/**
 * Short name of minimumN
 * @param value Short name of minimumN
 */
def minN_=(value: Option[Int]) { this.minimumN = value }

/** If true, do not output the header containing the command line used */
@Argument(fullName="suppressCommandLineHeader", shortName="suppressCommandLineHeader", doc="If true, do not output the header containing the command line used", required=false, exclusiveOf="", validation="")
var suppressCommandLineHeader: Boolean = _

/** If true, when VCF records overlap the info field is taken from the one with the max AC instead of only taking the fields which are identical across the overlapping records. */
@Argument(fullName="mergeInfoWithMaxAC", shortName="mergeInfoWithMaxAC", doc="If true, when VCF records overlap the info field is taken from the one with the max AC instead of only taking the fields which are identical across the overlapping records.", required=false, exclusiveOf="", validation="")
var mergeInfoWithMaxAC: Boolean = _

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
  if (!org.broadinstitute.gatk.engine.io.stubs.VCFWriterArgumentTypeDescriptor.isCompressed(out.getPath))
    outIndex = new File(out.getPath + ".idx")
}

override def commandLine = super.commandLine + repeat("-V", variant, spaceSeparated=true, escape=true, format="%s") + optional("-o", out, spaceSeparated=true, escape=true, format="%s") + conditional(no_cmdline_in_header, "-no_cmdline_in_header", escape=true, format="%s") + conditional(sites_only, "-sites_only", escape=true, format="%s") + conditional(bcf, "-bcf", escape=true, format="%s") + optional("-genotypeMergeOptions", genotypemergeoption, spaceSeparated=true, escape=true, format="%s") + optional("-filteredRecordsMergeType", filteredrecordsmergetype, spaceSeparated=true, escape=true, format="%s") + optional("-multipleAllelesMergeType", multipleallelesmergetype, spaceSeparated=true, escape=true, format="%s") + optional("-priority", rod_priority_list, spaceSeparated=true, escape=true, format="%s") + conditional(printComplexMerges, "-printComplexMerges", escape=true, format="%s") + conditional(filteredAreUncalled, "-filteredAreUncalled", escape=true, format="%s") + conditional(minimalVCF, "-minimalVCF", escape=true, format="%s") + conditional(excludeNonVariants, "-env", escape=true, format="%s") + optional("-setKey", setKey, spaceSeparated=true, escape=true, format="%s") + conditional(assumeIdenticalSamples, "-assumeIdenticalSamples", escape=true, format="%s") + optional("-minN", minimumN, spaceSeparated=true, escape=true, format="%s") + conditional(suppressCommandLineHeader, "-suppressCommandLineHeader", escape=true, format="%s") + conditional(mergeInfoWithMaxAC, "-mergeInfoWithMaxAC", escape=true, format="%s") + conditional(filter_reads_with_N_cigar, "-filterRNC", escape=true, format="%s") + conditional(filter_mismatching_base_and_quals, "-filterMBQ", escape=true, format="%s") + conditional(filter_bases_not_stored, "-filterNoBases", escape=true, format="%s")
}
