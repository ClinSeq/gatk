package org.dakl.walkers;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCompoundHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLine;
import org.broadinstitute.gatk.engine.arguments.StandardVariantContextInputArgumentCollection;
import org.broadinstitute.gatk.utils.contexts.AlignmentContext;
import org.broadinstitute.gatk.utils.contexts.ReferenceContext;
import org.broadinstitute.gatk.utils.refdata.RefMetaDataTracker;
import org.broadinstitute.gatk.engine.walkers.*;
import org.broadinstitute.gatk.utils.commandline.Argument;
import org.broadinstitute.gatk.utils.commandline.ArgumentCollection;
import org.broadinstitute.gatk.utils.commandline.Output;
import org.broadinstitute.gatk.utils.pileup.ReadBackedPileup;

import java.io.PrintStream;
import java.util.*;

/**
 * Calculates the number and fraction of heterozygote genotypes between a VCF files of genotype calls and reads BAM file
 *
 * Typical usage is to verify the identity of a tumor bam versus a VCF containing hz calls from the normal.
 */

@Allows(value={DataSource.READS, DataSource.REFERENCE})
@By(DataSource.REFERENCE)
public class HeterozygoteConcordance extends RodWalker<Integer, Integer> {

    @Output(doc="File to which output should be written (default stdout)")
    private PrintStream out;

    @Argument(fullName ="VCFSample", shortName = "sid", doc="Sample ID from which to get HZ calls. Must be present in the VCF. (required)", required=true)
    protected  String vcfSample;

    /*
    * Optional parameters
    * */
    @Argument(fullName="mindepth", shortName="md", doc="Minimum BAM depth of a position to be considered (default 30)", required=false)
    protected int minDepth = 30;

    /*
    * Upper limit over which homozygote alt's are called
    */
    @Argument(fullName="upper", shortName="up", doc="Upper limit over which hom alt GTs are called (default .9)", required=false)
    protected double upperLimit = 0.9;

    /**
    * Lower limit under which homozygote ref's are called
    */
    @Argument(fullName="lower", shortName="lo", doc="Lower limit over which hom ref GTs are called (default .1)", required=false)
    protected double lowerLimit = 0.1;

    @ArgumentCollection
    protected StandardVariantContextInputArgumentCollection variantCollection = new StandardVariantContextInputArgumentCollection();

    private Map<String, HeterozygoteConcordanceResult> results = new HashMap<String, HeterozygoteConcordanceResult>();


    @Override
    public Integer map(RefMetaDataTracker refMetaDataTracker, ReferenceContext referenceContext, AlignmentContext alignmentContext) {
        if ( refMetaDataTracker == null ){ return 0; }

        Collection<VariantContext> VCs = refMetaDataTracker.getValues(variantCollection.variants, alignmentContext.getLocation());
        if ( VCs.size() == 0 ){ return 0; }

        // Skip position if coverage is insufficient, ignoring MAPQ0 reads
        ReadBackedPileup pileup = alignmentContext.getBasePileup().getPileupWithoutMappingQualityZeroReads();
        if ( pileup.depthOfCoverage() < minDepth ){ return 0; }

        for ( VariantContext vc : VCs ){
            if(! vc.getSampleNames().contains(vcfSample)){
                continue;
            }
            // only handle biallelic SNPs, no indels or others
            if( ! vc.isBiallelic() ){
                continue;
            }
            if( ! vc.isSNP() ){
                continue;
            }

            for(String bamSample : alignmentContext.getBasePileup().getSamples() ){
                // add result object if absent
                if( ! results.containsKey( bamSample) ) {
                    results.put(bamSample, new HeterozygoteConcordanceResult(vcfSample, bamSample));
                }
                ArrayList<String> bases = basesToStrings(pileup.getPileupForSample(bamSample).getBases());

                Integer refCount = indexOfAll( vc.getReference().getBaseString(), bases ).size();
                Integer altcount = indexOfAll( vc.getAltAlleleWithHighestAlleleCount().toString(), bases).size();

                // increment total
                results.get(bamSample).incrementTotalSnps();

                if( vc.getGenotype(vcfSample).isHet() ){ // increment if hz
                    results.get(bamSample).incrementHzSnps();
                    // increment concordant nbr if alt allele has read support within set limits
                    Double alleleFraction = ((double)altcount.doubleValue()) / (altcount + refCount);
                    if( alleleFraction > lowerLimit && alleleFraction < upperLimit ){
                        results.get(bamSample).incrementConcordantHzSnps();
                    }
                }

            }

        }

        return 1;
    }

    @Override
    public Integer reduceInit() {
        return 0;
    }

    @Override
    public Integer reduce(Integer integer, Integer integer2) {
        return integer + integer2;
    }

    public void onTraversalDone(Integer result) {
        logger.info("Traversed " + result + " variant positions from the VCF.");
        out.println("VARIANTSSAMPLE\tREADSSAMPLE\tTOTAL_SNPS\tHZ_SNP_COUNT\tCONCORDANT_HZ_SNP_COUNT\tCONCORDANT_HZ_SNP_FRACTION");
        for( String sample : results.keySet() ){
            out.println( results.get(sample).toString() );
        }
    }


    /*
    *   Convert a byte[] array of bases to an ArrayList<String> of single characters for more conventient handling.
    * */
    private ArrayList<String> basesToStrings(byte[] baseBytes){
        ArrayList<String> bases = new ArrayList<String>();
        for(int i=0;i < baseBytes.length;i++){
            bases.add( Character.toString( (char)baseBytes[i] ) );
        }
        return bases;
    }

    // get all elements that equal "obj" in an ArrayList (http://stackoverflow.com/questions/13900585)
    static ArrayList<Integer> indexOfAll(Object obj, ArrayList list){
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++)
            if(obj.equals(list.get(i)))
                indexList.add(i);
        return indexList;
    }


    private class HeterozygoteConcordanceResult extends Object{

        private String variantsSample;
        private String readsSample;
        private Integer totalSnps;
        private Integer hzSnpCount;
        private Integer concordantHzSnpCount;
        private Double concordantHzSnpFraction;

        public HeterozygoteConcordanceResult(String variantsSample, String readsSample){
            this.readsSample = readsSample;
            this.variantsSample = variantsSample;
            this.totalSnps = 0;
            this.hzSnpCount = 0;
            this.concordantHzSnpCount = 0;
            this.concordantHzSnpFraction = null;
        }

        public String toString(){
            if(hzSnpCount > 0){
                this.concordantHzSnpFraction = this.concordantHzSnpCount.doubleValue() / this.hzSnpCount.doubleValue();
            }
            return this.variantsSample + "\t" + this.readsSample + "\t" + this.totalSnps + "\t" + this.hzSnpCount +
                    "\t" + this.concordantHzSnpCount + "\t" + this.concordantHzSnpFraction;
        }

        public void incrementTotalSnps(){
            this.totalSnps++;
        }
        public void incrementHzSnps(){
            this.hzSnpCount++;
        }
        public void incrementConcordantHzSnps(){
            this.concordantHzSnpCount++;
        }

    }


    public static boolean isUniqueHeaderLine(VCFHeaderLine line, Set<VCFHeaderLine> currentSet) {
        if ( !(line instanceof VCFCompoundHeaderLine) )
            return true;

        for ( VCFHeaderLine hLine : currentSet ) {
            if ( hLine instanceof VCFCompoundHeaderLine && ((VCFCompoundHeaderLine)line).sameLineTypeAndName((VCFCompoundHeaderLine)hLine) )
                return false;
        }

        return true;
    }


}
