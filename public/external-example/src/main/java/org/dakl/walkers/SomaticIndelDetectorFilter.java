package org.dakl.walkers;

import htsjdk.variant.variantcontext.*;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.*;
import org.broadinstitute.gatk.engine.contexts.AlignmentContext;
import org.broadinstitute.gatk.engine.contexts.ReferenceContext;
import org.broadinstitute.gatk.engine.refdata.RefMetaDataTracker;
import org.broadinstitute.gatk.engine.walkers.RodWalker;
import org.broadinstitute.gatk.utils.SampleUtils;
import org.broadinstitute.gatk.utils.commandline.Input;
import org.broadinstitute.gatk.utils.commandline.Output;
import org.broadinstitute.gatk.utils.commandline.RodBinding;
import org.broadinstitute.gatk.utils.variant.GATKVCFUtils;
import org.broadinstitute.gatk.utils.variant.GATKVariantContextUtils;
import org.dakl.utils.FisherExact;
import org.dakl.utils.MultipleTestingCorrection.MultipleTestingCorrection;

import java.util.*;

/**
 *
 * Filter Indels from IndelGenotyperV2 (SomaticIndelDetector) based on coverage and Fisher's exact test (BH corrected).
 *
 * This walker reads output from IndelGenotyperV2 and filters based on Fisher's exact test
 * adjusted for multiple testing using the Benjamini Hochberg method for controlling FDR
 *
 * Output is a VCF file with the AD anf FA fields filled in.
 *
 *
 * Created with IntelliJ IDEA.
 * User: dankle
 * Date: 2014-02-19
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
public class SomaticIndelDetectorFilter extends RodWalker<Integer,Integer> {
    @Output(doc="File to which variants should be written")
    protected VariantContextWriter vcfWriter = null;

    @Input(fullName="variant", shortName = "V", doc="Select variants from this VCF file",
            required=true)
    public RodBinding<VariantContext> variants;

    @Input(fullName="tumorid", shortName = "TID", doc="Sample Name of the tumor", required = true)
    public String tumorId;

    @Input(fullName="normalid", shortName = "NID", doc="Sample Name of the normal", required = true)
    public String normalId;

    @Input(fullName="minCoverageNormal", shortName = "MIN_DP_N", doc="Minimum depth required in the normal", required = false)
    public int MIN_DP_N = 25;

    @Input(fullName="minCoverageTumor", shortName = "MIN_DP_T", doc="Minimum depth required in the tumor", required = false)
    public int MIN_DP_T = 25;

    @Input(fullName="maxCoverageNormal", shortName = "MAX_DP_N", doc="Maximum depth required in the normal", required = false)
    public int MAX_DP_N = 1000;

    @Input(fullName="maxCoverageTumor", shortName = "MAX_DP_T", doc="Maximum depth required in the tumor", required = false)
    public int MAX_DP_T = 1000;

    @Input(fullName="maxNormalFraction", shortName = "MAX_N_FRAC", doc="Maximum fraction allowed in the normal", required = false)
    public double MAX_N_FRAC = .15;

    @Input(fullName="AdjustedPValueCutoff", shortName = "ADJ_P_CUTOFF", doc="Cutoff for the adjusted p values", required = false)
    public double ADJ_P_CUTOFF = 0.05;

    private FisherExact fisher = new FisherExact(100000);
    private ArrayList<VariantContext> candidateVariants;

    /**
     * Set up the VCF writer, the sample expressions and regexs, and the JEXL matcher
     */
    public void initialize() {
        // Set up new header including PV INFO field

        Map<String, VCFHeader> vcfRods = GATKVCFUtils.getVCFHeadersFromRods(getToolkit());
        Set<VCFHeaderLine> headerLines = VCFUtils.smartMergeHeaders(vcfRods.values(), true);
        headerLines.add(new VCFHeaderLine(VCFHeader.SOURCE_KEY, "SomaticIndelDetectorFilter"));
        TreeSet<String> vcfSamples = new TreeSet<String>(SampleUtils.getSampleList(vcfRods, GATKVariantContextUtils.GenotypeMergeType.REQUIRE_UNIQUE));
        VCFInfoHeaderLine vcfInfoHeaderLinePV = new VCFInfoHeaderLine("PVI", 1, VCFHeaderLineType.Float, "P value after adjustment for multiple testing using Benjamini Hochberg's method for controlling FDR (from IndelLocatorV2Filter).");
        VCFFormatHeaderLine vcfFormatHeaderLineFA = new VCFFormatHeaderLine("FA", 1, VCFHeaderLineType.Float, "Allele fraction of the alternate allele with regard to reference");
        VCFFormatHeaderLine vcfFormatHeaderLineAD = new VCFFormatHeaderLine("AD", 1, VCFHeaderLineType.String, "Allelic depths for the ref and alt alleles in the order listed");
        headerLines.add( vcfInfoHeaderLinePV );
        headerLines.add( vcfFormatHeaderLineFA );
        headerLines.add( vcfFormatHeaderLineAD );

        //For some reason, the FORMAT fields GT and GQ are missing from the output of IndelLocatorV2 and hence need to be added here
        VCFFormatHeaderLine vcfFormatHeaderLineGQ = new VCFFormatHeaderLine("GQ", 1, VCFHeaderLineType.Integer, "Genotype Quality");
        VCFFormatHeaderLine vcfFormatHeaderLineGT = new VCFFormatHeaderLine("GT", 1, VCFHeaderLineType.String, "Genotype");
        headerLines.add( vcfFormatHeaderLineGQ );
        headerLines.add( vcfFormatHeaderLineGT );

        VCFHeader vcfHeader = new VCFHeader(headerLines, vcfSamples);
        vcfWriter.writeHeader(vcfHeader);

        // set up list of candidate variants. These variants will be tested with Fishers exact test to check for AF T and N
        candidateVariants = new ArrayList<VariantContext>();
    }

    @Override
    public Integer map(RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
        if ( tracker == null )
            return 0;

        Collection<VariantContext> VCs = tracker.getValues(variants, context.getLocation());
        for ( VariantContext vc : VCs ){
            double n_frac =  getAlleleFractionForNormal(vc);
            // keep variants that have normal fraction is <= MAX_N_FRAC
            if( n_frac <= MAX_N_FRAC){
                candidateVariants.add(vc);
            }
        }
        return 0;
    }

    @Override
    public Integer reduceInit() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer reduce(Integer value, Integer sum) {
        return value + sum;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onTraversalDone(Integer result) {
        HashMap<String,Double> pvalues = new HashMap<String,Double>();
        for( VariantContext vc : candidateVariants ){
            double p = fisher.getLeftTailedP(getValuesForFisher(vc));
            pvalues.put( vc.getChr()+":"+vc.getStart()+":"+vc.getReference()+":"+vc.getAltAlleleWithHighestAlleleCount(), p);
        }
        MultipleTestingCorrection mtc = new MultipleTestingCorrection(Double.toString(ADJ_P_CUTOFF), pvalues);
        mtc.calculate();

        for( VariantContext vc : candidateVariants ){
            double padj = Double.parseDouble(mtc.getCorrectionMap().get( vc.getChr()+":"+vc.getStart()+":"+vc.getReference()+":"+vc.getAltAlleleWithHighestAlleleCount() ).toString() );

            if( padj < ADJ_P_CUTOFF ){ // write the variant if it's significant
                Map attributes = vc.getAttributes();
                GenotypesContext genotypesContext = vc.getGenotypes();
                ArrayList<Genotype> newGenotypes = new ArrayList<Genotype>();
                for( Genotype g : genotypesContext ){
                    double allele_frac = 0.0;
                    int[] ad = new int[2];
                    if(g.getSampleName().equals(this.tumorId)){
                        allele_frac = getAlleleFractionForTumor(vc);
                        ad = getAlleleDepthsForTumor(vc);
                    } else if( g.getSampleName().equals(this.normalId)){
                        allele_frac = getAlleleFractionForNormal(vc);
                        ad = getAlleleDepthsForNormal(vc);
                    }

                    GenotypeBuilder genotypeBuilder = new GenotypeBuilder(g);
                    genotypeBuilder.attribute("FA", allele_frac);
                    genotypeBuilder.AD(ad);
                    newGenotypes.add(genotypeBuilder.make());
                }
                VariantContext newVariantContext = new VariantContextBuilder(vc).genotypes(newGenotypes).attribute("PVI", padj).make();
                vcfWriter.add(newVariantContext);
            }
        }
    }

    // easy access to contingency table data for fisher's exact test
    private HashMap<String, Integer> getValuesForFisher(VariantContext vc){
        HashMap<String,Integer> map = new HashMap<String, Integer>();
        String N_AC_str = vc.getAttributeAsString("N_AC", "0,0").replace("[","").replace("]","");
        int n_ac = Integer.parseInt(N_AC_str.split(",")[0]);
        String T_AC_str = vc.getAttributeAsString("T_AC", "0,0").replace("[","").replace("]","");
        int t_ac = Integer.parseInt(T_AC_str.split(",")[0]);
        int n_dp = vc.getAttributeAsInt("N_DP", 0);
        int t_dp = vc.getAttributeAsInt("T_DP", 0);

            /*    2x2 tab for fisher's exact test
            *             REF    ALT
            *      TUMOR    a      b
            *      NORMAL   c      d
            *
            * */
        int a = t_dp - t_ac;
        int b = t_ac;
        int c = n_dp - n_ac;
        int d = n_ac;

        // make sure no values are negative
        if(a < 0 ){ a = 0; }
        if(c < 0 ){ c = 0; }

        map.put("A", a);
        map.put("B", b);
        map.put("C", c);
        map.put("D", d);
        return map;
    }

    // easy access to tumor allele fraction
    private double getAlleleFractionForTumor(VariantContext vc){
        String T_AC_str = vc.getAttributeAsString("T_AC", "0,0").replace("[","").replace("]","");
        int t_ac = Integer.parseInt(T_AC_str.split(",")[0]);
        int t_dp = vc.getAttributeAsInt("T_DP", 0);
        return (double)t_ac/(double)t_dp;
    }

    // easy access to normal allele fraction
    private double getAlleleFractionForNormal(VariantContext vc){
        String N_AC_str = vc.getAttributeAsString("N_AC", "0,0").replace("[","").replace("]","");
        int t_ac = Integer.parseInt(N_AC_str.split(",")[0]);
        int t_dp = vc.getAttributeAsInt("N_DP", 0);
        return (double)t_ac/(double)t_dp;
    }

    // AD for tumor
    private int[] getAlleleDepthsForTumor(VariantContext vc){
        String T_AC_str = vc.getAttributeAsString("T_AC", "0,0").replace("[","").replace("]","");
        int t_ac = Integer.parseInt(T_AC_str.split(",")[0]);
        int t_dp = vc.getAttributeAsInt("T_DP", 0);
        int[] ad = {t_dp-t_ac, t_ac};
        // double check that diff if non-negative
        if(ad[0] < 0 ){ad[0] = 0;}
        return ad;
    }

    // AD for normal
    private int[] getAlleleDepthsForNormal(VariantContext vc){
        String N_AC_str = vc.getAttributeAsString("N_AC", "0,0").replace("[","").replace("]","");
        int n_ac = Integer.parseInt(N_AC_str.split(",")[0]);
        int n_dp = vc.getAttributeAsInt("N_DP", 0);
        int[] ad = {n_dp-n_ac, n_ac};
        // double check that diff if non-negative
        if(ad[0] < 0 ){ad[0] = 0;}
        return ad;
    }


}

