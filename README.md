# The Genome Analysis Toolkit

This is my public version of the (MIT licensed) GATK source tree with added custom walkers. 

## SomaticPindelFilter

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

## Compile me

You need maven to compile the GATK. 

    git clone https://github.com/dakl/gatk.git gatk-klevebring
    cd gatk-klevebring

I usually skip compiling Queue: 

    mvn verify -P\!queue
    
The resulting jar file is `target/GenomeAnalysisTK.jar`

## See also

See http://www.broadinstitute.org/gatk/
