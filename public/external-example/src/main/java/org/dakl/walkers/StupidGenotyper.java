package org.dakl.walkers;

import htsjdk.samtools.CigarElement;
import htsjdk.variant.variantcontext.*;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.*;
import org.broadinstitute.gatk.engine.CommandLineGATK;
import org.broadinstitute.gatk.engine.arguments.StandardVariantContextInputArgumentCollection;
import org.broadinstitute.gatk.engine.contexts.AlignmentContext;
import org.broadinstitute.gatk.engine.contexts.ReferenceContext;
import org.broadinstitute.gatk.engine.downsampling.DownsampleType;
import org.broadinstitute.gatk.engine.refdata.RefMetaDataTracker;
import org.broadinstitute.gatk.engine.walkers.*;
import org.broadinstitute.gatk.tools.walkers.genotyper.AlleleList;
import org.broadinstitute.gatk.tools.walkers.genotyper.SampleListUtils;
import org.broadinstitute.gatk.utils.BaseUtils;
import org.broadinstitute.gatk.utils.SampleUtils;
import org.broadinstitute.gatk.utils.commandline.Argument;
import org.broadinstitute.gatk.utils.commandline.ArgumentCollection;
import org.broadinstitute.gatk.utils.commandline.Hidden;
import org.broadinstitute.gatk.utils.commandline.Output;
import org.broadinstitute.gatk.utils.help.DocumentedGATKFeature;
import org.broadinstitute.gatk.utils.help.HelpConstants;
import org.broadinstitute.gatk.utils.help.HelpUtils;
import org.broadinstitute.gatk.utils.pileup.PileupElement;
import org.broadinstitute.gatk.utils.pileup.ReadBackedPileup;
import org.broadinstitute.gatk.utils.sam.GATKSAMRecord;
import org.broadinstitute.gatk.utils.variant.GATKVCFUtils;

import java.util.*;

/**
 * Created by dankle on 14/08/14.
 */
@Hidden
@DocumentedGATKFeature( groupName = HelpConstants.DOCS_CAT_VARMANIP, extraDocs = {CommandLineGATK.class} )
@Requires(value={})
@Allows(value={DataSource.READS, DataSource.REFERENCE})
@Reference(window=@Window(start=-50,stop=50))
@Downsample(by= DownsampleType.BY_SAMPLE, toCoverage=250)
@By(DataSource.REFERENCE)
public class StupidGenotyper extends RodWalker<Integer, Integer> {
    @ArgumentCollection
    protected StandardVariantContextInputArgumentCollection variantCollection = new StandardVariantContextInputArgumentCollection();

    @Output(doc="File to which variants should be written")
    protected VariantContextWriter vcfWriter = null;

    @Argument(fullName="min_depth_to_genotype", shortName="mindp", doc="Don't genotype under this coverage", required=false)
    protected int MIN_DEPTH_TO_GENOTYPE = 20 ;

    @Argument(fullName="min_hz_threshold", shortName="min", doc="Minimum ALT fraction to call het", required=false)
    protected double MIN_HZ_THRESHOLD = 0.05 ;

    @Argument(fullName="max_hz_threshold", shortName="max", doc="Maximum ALT fraction to call het", required=false)
    protected double MAX_HZ_THRESHOLD = 0.95 ;

    Set<String> samples;

    public void initialize() {

        this.samples = SampleListUtils.asSet(getToolkit().getSampleList());
        // setup the header fields
        // note that if any of the definitions conflict with our new ones, then we want to overwrite the old ones
        final Set<VCFHeaderLine> hInfo = new HashSet<VCFHeaderLine>();
        for ( final VCFHeaderLine line : GATKVCFUtils.getHeaderFields(getToolkit(), Arrays.asList(variantCollection.variants.getName())) ) {
            if ( isUniqueHeaderLine(line, hInfo) )
                hInfo.add(line);
        }

        hInfo.add(new VCFFormatHeaderLine("GT", 1, VCFHeaderLineType.String, "Called genotype"));
        hInfo.add(new VCFFormatHeaderLine("AD", 2, VCFHeaderLineType.Integer, "Alleleic depths for ref and alt"));
        hInfo.add(new VCFFormatHeaderLine("DP", 1, VCFHeaderLineType.Integer, "REF count + ALT count"));
        VCFHeader vcfHeader = new VCFHeader(hInfo, this.samples);

        vcfWriter.writeHeader(vcfHeader);
        //System.out.println("CHR\tPOS\tSM\tREF\tALT\tDP\tREF_COUNT\tALT_COUNT");
    }

    /**
     * For each site of interest, annotate based on the requested annotation types
     *
     * @param tracker  the meta-data tracker
     * @param ref      the reference base
     * @param context  the context for the given locus
     * @return 1 if the locus was successfully processed, 0 if otherwise
     */
    @Override
    public Integer map(RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
        if ( tracker == null )
            return 0;

        Collection<VariantContext> VCs = tracker.getValues(variantCollection.variants, context.getLocation());
        if ( VCs.size() == 0 )
            return 0;

        for ( VariantContext vc : VCs ){
            // if no annotated variant alleles, skip
            // or , if variant is other than biallelic SNP, skip
            if( vc.getNAlleles() <= 1 || ! vc.isSNP() || ! vc.isBiallelic() ) {
                continue;
            };

            ArrayList<Genotype> newGenotypes = new ArrayList<Genotype>();

            for(String sample : this.samples ){

                ReadBackedPileup pileup = context.getBasePileup().getPileupForSample(sample);
                Allele ref_allele = vc.getReference();
                Allele alt_allele = vc.getAlternateAlleles().get(0);

                int depth = 0;
                int ref_count = 0;
                int alt_count = 0;
                if( pileup == null ){
                    //System.out.println("No coverage for sample " + sample);
                } else {
                    //System.out.println(sample);
                    int[] counts = pileup.getBaseCounts();
                    byte ref_base = ref_allele.getBases()[0];
                    byte alt_base = alt_allele.getBases()[0];
                    ref_count = counts[BaseUtils.simpleBaseToBaseIndex(ref_base)];
                    alt_count = counts[BaseUtils.simpleBaseToBaseIndex(alt_base)];
                    depth     = ref_count + alt_count;
                    /* System.out.println(
                            ref.getLocus().getContig() + "\t" +
                                    ref.getLocus().getStart() + "\t" +
                                    sample + "\t" +
                                    new String(new byte[]{ref_base}) + "\t" +
                                    new String(new byte[]{alt_base}) + "\t" +
                                    depth + "\t" +
                                    ref_count + "\t" +
                                    alt_count

                    ); */

                }

                int[] ad = {ref_count, alt_count};
                double fa = alt_count / (double)(ref_count+alt_count);
                List<Allele> alleles = new ArrayList<Allele>();

                // stupid genotyping, call het if  MIN_HZ_THRESHOLD < FA < MAX_HZ_THRESHOLD
                if(depth >= this.MIN_DEPTH_TO_GENOTYPE){
                    if(fa <= this.MIN_HZ_THRESHOLD ){
                        alleles.add(ref_allele);
                        alleles.add(ref_allele);
                    }else if(fa >= this.MAX_HZ_THRESHOLD ){
                        alleles.add(alt_allele);
                        alleles.add(alt_allele);
                    } else {
                        alleles.add(ref_allele);
                        alleles.add(alt_allele);
                    }
                }else{
                    alleles.add(Allele.NO_CALL);
                    alleles.add(Allele.NO_CALL);
                }

                GenotypeBuilder genotypeBuilder = new GenotypeBuilder(sample, alleles);
                genotypeBuilder.DP(depth);
                genotypeBuilder.AD(ad);
                newGenotypes.add(genotypeBuilder.make());

            }

            VariantContext newVariantContext = new VariantContextBuilder(vc).genotypes(newGenotypes).make();
            vcfWriter.add(newVariantContext);



        }

        return 1;
    }

    @Override
    public Integer reduceInit() { return 0; }

    @Override
    public Integer reduce(Integer value, Integer sum) { return value + sum; }

    /**
     * Tell the user the number of loci processed and close out the new variants file.
     *
     * @param result  the number of loci seen.
     */
    public void onTraversalDone(Integer result) {
        logger.info("Processed " + result + " loci.\n");
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
