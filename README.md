# The Genome Analysis Toolkit

This is my public version of the (MIT licensed) GATK source tree with added custom walkers. 

## SomaticPindelFilter

### TL;DR
    
    java -jar GenomeAnalysisTK.jar -T SomaticPindelFilter -V pindel_variants.vcf -o out.vcf -TID TUMOR_ID -NID NORMAL_ID -R reference.fa
   
### Parameters
    
    -T SomaticPindelFilter    Name of walker to run
    -V pindel_variants.vcf    Variants from pindel, converted with pindel2vcf
    -TID TUMOR_ID             Tumor ID as used as input for input to pindel
    -NID NORMAL_ID            Normal ID as used as input for input to pindel
    -R reference.fa           Reference genome used for alignment and pindel variant calling     
   
### Where does the variants come from?

I run pindel with a config file like so:

    pindel -f reference.fa -i configfile.txt -o pindelPrefix
    pindel2vcf -P pindelPrefix --gatk_compatible -r reference.fa \
               -R human_g1k_v37_decoy -d 20140127 -v pindel_variants.vcf --compact_output_limit 15

Please refer to the [pindel website](http://gmt.genome.wustl.edu/pindel/current/) for further information on how to run pindel. 

### Not working as expected?
Please report any bugs in the issue tracker on this site.
    
## See also

See http://www.broadinstitute.org/gatk/
