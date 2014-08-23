# The Genome Analysis Toolkit

This is my public version of the (MIT licensed) GATK source tree with added custom walkers.

[![Build Status](https://travis-ci.org/dakl/gatk.svg?branch=master)](https://travis-ci.org/dakl/gatk.svg?branch=master)


## SomaticPindelFilter

Filtering of variants from reported from pindel. Performs a Fisher's Exact test for each variants and Benjamini-Hochberg adjusts all p-values. Prints variants with adjusted p < cutoff (settable).

### TL;DR

    java -jar GenomeAnalysisTK.jar -T SomaticPindelFilter -V pindel_variants.vcf -o out.vcf -TID TUMOR_ID -NID NORMAL_ID -R reference.fa

### Parameters

Arguments for SomaticPindelFilter:

     -V,--variant <variant>                                        Select variants from this VCF file
     -TID,--tumorid <tumorid>                                      Sample Name of the tumor
     -NID,--normalid <normalid>                                    Sample Name of the normal
     -o,--out <out>                                                File to which variants should be written
     -MIN_DP_N,--minCoverageNormal <minCoverageNormal>             Minimum depth required in the normal, default 25
     -MIN_DP_T,--minCoverageTumor <minCoverageTumor>               Minimum depth required in the tumor, default 25
     -MAX_DP_N,--maxCoverageNormal <maxCoverageNormal>             Maximum depth required in the normal, default 1000
     -MAX_DP_T,--maxCoverageTumor <maxCoverageTumor>               Maximum depth required in the tumor, default 1000
     -MAX_N_FRAC,--maxNormalFraction <maxNormalFraction>           Maximum fraction allowed in the normal, default 0.15
     -ADJ_P_CUTOFF,--AdjustedPValueCutoff <AdjustedPValueCutoff>   Cutoff for the adjusted p values, default 0.05

Note that with a clean normal sample (like DNA from blood), `-MAX_N_FRAC` can ususally be set way lower than 0.15. The likeliehood that a sequencing error in the normal sample resulting in an indel at the exact same place as a true somatic indel in the tumor is likely very very low. The error rate of the index reads likely sets the limit for this (reads that are misclassified to the tumor sample due to low quality of the index).

### Where does the variants come from?

I run pindel with a config file like so:

    pindel -f reference.fa -i configfile.txt -o pindelPrefix
    pindel2vcf -P pindelPrefix --gatk_compatible -r reference.fa \
               -R human_g1k_v37_decoy -d 20140127 -v pindel_variants.vcf --compact_output_limit 15

Please refer to the [pindel website](http://gmt.genome.wustl.edu/pindel/current/) for further information on how to run pindel. Of note, if `--compact_output_limit 15` is omitted, the REF and ALT fields of the vcf file can be several Mb in size for long inversions, insertions or deletion. That results in a large and possibly unparsable vcf file (baaaaad).

### Not working as expected?
Please report any bugs in the issue tracker on this site.








## SomaticIndelDetectorFilter

Filtering of variants from reported from IndelGenotyperV2 (the now very old Broad Cancer tool). Performs a Fisher's Exact test for each variants and Benjamini-Hochberg adjusts all p-values. Prints variants with adjusted p < cutoff (settable).

### TL;DR

    java -jar GenomeAnalysisTK.jar -T SomaticIndelDetectorFilter -V indelDetector_variants.vcf -o out.vcf -TID TUMOR_ID -NID NORMAL_ID -R reference.fa

### Parameters

Arguments for SomaticIndelDetectorFilter:

    -V,--variant <variant>                                        Select variants from this VCF file
    -TID,--tumorid <tumorid>                                      Sample Name of the tumor
    -NID,--normalid <normalid>                                    Sample Name of the normal
    -o,--out <out>                                                File to which variants should be written
    -MIN_DP_N,--minCoverageNormal <minCoverageNormal>             Minimum depth required in the normal
    -MIN_DP_T,--minCoverageTumor <minCoverageTumor>               Minimum depth required in the tumor
    -MAX_DP_N,--maxCoverageNormal <maxCoverageNormal>             Maximum depth required in the normal
    -MAX_DP_T,--maxCoverageTumor <maxCoverageTumor>               Maximum depth required in the tumor
    -MAX_N_FRAC,--maxNormalFraction <maxNormalFraction>           Maximum fraction allowed in the normal
    -ADJ_P_CUTOFF,--AdjustedPValueCutoff <AdjustedPValueCutoff>   Cutoff for the adjusted p values











## SelectPathogenicVariants

Walker to select pathogenic variants from clinvar.

### TL;DR

To select variants from certain genes, run

     java -jar ./public/external-example/target/external-example-1.0-SNAPSHOT.jar -T SelectPathogenicVariants \
     -V public/testdata/dakl/clinvar_20140807.vcf -R ~/genome/human_g1k_v37_decoy.fasta -o ~/tmp/tmp.vcf -G BRCA1 -G BRCA2

To select pathogenic variants from all genes, run

    java -jar ./public/external-example/target/external-example-1.0-SNAPSHOT.jar -T SelectPathogenicVariants \
    -V public/testdata/dakl/clinvar_20140807.vcf -R ~/genome/human_g1k_v37_decoy.fasta -o ~/tmp/tmp.vcf -G BRCA1 -G BRCA2

### Parameters

Arguments for SelectPathogenicVariants:
 -V,--variant <variant>      Input VCF file
 -o,--out <out>              File to which variants should be written
 -G,--genes <genes>          String of gene names from which variants are selected. Multiple allowed. If not specified,
                             all genes are selected
 -GI,--GENEINFO <GENEINFO>   Name of the field containing gene info.









## TelomereQuant

Walker to quantify the telomeric content in a sample. The walker calculates the following:

- The number of reads that has at least `NR` repeats of the telomere repeat sequence (TTAGGG or CCCTAA) in them ([ref][teloquant]).
- The total number of repeats seen, only counting repeats in reads that has `NR` x teloseq. Reads that have fewer than `NR` x teloseq are not counted to avoid false positives.
- Total bases sequenced

The walker allows for multiple samples to be given within a single BAM file, and will calculate these figures for each sample given (using read groups).

[teloquant]: http://genomebiology.com/2012/13/12/R113

### TL;DR

    -jar ./public/external-example/target/external-example-1.0-SNAPSHOT.jar -T TelomereQuant -R reference.fa -I mybam.bam -o outfile.txt

### Parameters

Arguments for TelomereQuant:

    -o,--out <out>                                An output file created by the walker.  Will overwrite contents if file
                                                  exists
    -NR,--number_of_repeats <number_of_repeats>   Number of repeats of telomere sequence required for a read to be
                                                  classified as telomeric. Default 4.







## Compile me

You need maven to compile the GATK.

    git clone https://github.com/dakl/gatk.git gatk-klevebring
    cd gatk-klevebring

I usually skip compiling Queue:

    mvn verify -P\!queue

The resulting jar file is `./public/external-example/target/external-example-1.0-SNAPSHOT.jar`











## See also

See http://www.broadinstitute.org/gatk/
