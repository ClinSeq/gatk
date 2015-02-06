#!/bin/bash

java -jar GenomeAnalysisTK-Klevebring.jar -T VariantAnnotator2000 \
-V public/gatk-engine/src/test/resources/dakl/my.vcf \
-R ~/genome_files/human_g1k_v37_decoy.fasta \
--resource:clinvar public/gatk-engine/src/test/resources/dakl/cv.vcf \
--dbsnp public/gatk-engine/src/test/resources/dakl/db.vcf \
-E clinvar.CLNACC -E clinvar.CLNSIG \
-L public/gatk-engine/src/test/resources/dakl/my.vcf -o ~/tmp/test.vcf

## need to remove line which include run timestamp
HASH=`grep -v VariantAnnotator2000 ~/tmp/test.vcf|md5`
CHECKHASH="5d6100588aa2c1856cf81cb7f2471e48"
if [[ "$HASH" == "$CHECKHASH" ]]; then
  echo "test passed"
else
  echo "test failed"
  echo "expected hash $CHECKHASH, got $HASH"  
fi

