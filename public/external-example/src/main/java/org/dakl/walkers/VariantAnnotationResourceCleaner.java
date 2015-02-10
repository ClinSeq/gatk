package org.dakl.walkers;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.CommonInfo;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.*;
import org.broadinstitute.gatk.engine.arguments.StandardVariantContextInputArgumentCollection;
import org.broadinstitute.gatk.engine.contexts.AlignmentContext;
import org.broadinstitute.gatk.engine.contexts.ReferenceContext;
import org.broadinstitute.gatk.engine.refdata.RefMetaDataTracker;
import org.broadinstitute.gatk.engine.walkers.RodWalker;
import org.broadinstitute.gatk.utils.SampleUtils;
import org.broadinstitute.gatk.utils.commandline.Argument;
import org.broadinstitute.gatk.utils.commandline.ArgumentCollection;
import org.broadinstitute.gatk.utils.commandline.Output;
import org.broadinstitute.gatk.utils.variant.GATKVCFUtils;

import java.util.*;

/**
 * Created by dankle on 08/02/15.
 */
public class VariantAnnotationResourceCleaner extends RodWalker<Integer, Integer>{
    @ArgumentCollection
    protected StandardVariantContextInputArgumentCollection variantCollection = new StandardVariantContextInputArgumentCollection();

    @Argument(doc="INFO keys to keep when outputting variants", shortName = "k", fullName = "keys")
    protected List<String> infoToKeep = null;

    @Output(doc="File to which variants should be written")
    protected VariantContextWriter vcfWriter = null;

    VCFHeader vcfHeader = null;

    public void initialize() {
        Map<String, VCFHeader> vcfRods = GATKVCFUtils.getVCFHeadersFromRods(getToolkit());

        // get the list of all sample names from the variant VCF input rod, if applicable
        final List<String> rodName = Arrays.asList(variantCollection.variants.getName());
        final Set<String> samples = SampleUtils.getUniqueSamplesFromRods(getToolkit(), rodName);

        // setup the header fields
        // note that if any of the definitions conflict with our new ones, then we want to overwrite the old ones
        final Set<VCFHeaderLine> hInfo = new HashSet<VCFHeaderLine>();
        for ( final VCFHeaderLine line : GATKVCFUtils.getHeaderFields(getToolkit(), Arrays.asList(variantCollection.variants.getName())) ) {
            if(line.toString().contains("DP_HIST") && line.toString().contains("Number=A")){
                VCFHeaderLine newLine = new VCFHeaderLine("INFO", "<ID=DP_HIST,Number=R,Type=String,Description=\"Histogram for DP; Mids: 2.5|7.5|12.5|17.5|22.5|27.5|32.5|37.5|42.5|47.5|52.5|57.5|62.5|67.5|72.5|77.5|82.5|87.5|92.5|97.5\">");
                hInfo.add(newLine);
            }else if(line.toString().contains("GQ_HIST") && line.toString().contains("Number=A")) {
                VCFHeaderLine newLine = new VCFHeaderLine("INFO", "<ID=GQ_HIST,Number=R,Type=String,Description=\"Histogram for DP; Mids: 2.5|7.5|12.5|17.5|22.5|27.5|32.5|37.5|42.5|47.5|52.5|57.5|62.5|67.5|72.5|77.5|82.5|87.5|92.5|97.5\">");
                hInfo.add(newLine);
            } else{
                hInfo.add(line);
            }
        }

        hInfo.add(new VCFHeaderLine(VCFHeader.SOURCE_KEY, "VariantAnnotationResourceCleaner"));

        this.vcfHeader = new VCFHeader(hInfo, samples);
        vcfWriter.writeHeader(this.vcfHeader);

    }


    @Override
    public Integer map(RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
        if ( tracker == null )
            return 0;

        Collection<VariantContext> VCs = tracker.getValues(variantCollection.variants, context.getLocation());
        if ( VCs.size() == 0 )
            return 0;

        for(VariantContext vc : VCs ){
            List<VariantContext> splitVCs = this.splitVariantContext(vc);
            for( VariantContext splitVC : splitVCs){
                vcfWriter.add( splitVC );
            }
        }


        return 1;
    }

    @Override
    public Integer reduceInit() {
        return 0;
    }

    @Override
    public Integer reduce(Integer value, Integer sum) {
        return sum+value;
    }

    private List<VariantContext> splitVariantContext( VariantContext vc ){
        CommonInfo ci = vc.getCommonInfo();
        List<VariantContext> newVCs = new ArrayList<VariantContext>();

        for(Allele altallele: vc.getAlternateAlleles()) {

            List<Allele> newAlleles = new ArrayList<Allele>();
            newAlleles.add(vc.getReference());
            newAlleles.add(altallele);

            VariantContextBuilder vcb = new VariantContextBuilder(vc.getSource(),
                    vc.getChr(), vc.getStart(), vc.getEnd(), newAlleles);

            Map<String, Object> attributes = new HashMap<String, Object>();

            for (String attributeName : this.infoToKeep ) {
                VCFHeaderLineCount countType = this.vcfHeader.getInfoHeaderLine(attributeName).getCountType();
                List<String> attributeValues = new ArrayList<String>();
                try{
                    attributeValues = (List)vc.getAttribute(attributeName);
                }catch(ClassCastException e){
                    attributeValues.add(vc.getAttribute(attributeName).toString());
                }
                if(countType == VCFHeaderLineCount.A){ // one per alt allele
                    if( vc.getAlternateAlleles().size() != attributeValues.size() ){
                        logger.info("Number of items listed for attribute " + attributeName + " does not match the number of alternative alleles for variant " + vc.toString() + "\nInput file is malformed.");
                        // could fail here. Instead, the workaround is to ignore the annotation.
                        //throw new IncorrectNumberOfAlternativeAllelesException("Number of items listed for attribute " + attributeName + " does not match the number of alternative alleles for variant " + vc.toString() + "\nInput file is malformed.", "variant");
                    }else{
                        int idx = vc.getAlternateAlleles().indexOf(altallele);
                        if(idx >= 0){
                            String attributeValue = attributeValues.get(idx).toString();
                            attributes.put(attributeName, attributeValue);
                        }
                    }

                    int idx = vc.getAlternateAlleles().indexOf(altallele);
                    if(idx >= 0){
                        String attributeValue = attributeValues.get(idx).toString();
                        attributes.put(attributeName, attributeValue);
                    }

                } else if(countType == VCFHeaderLineCount.INTEGER){
                    int count = this.vcfHeader.getInfoHeaderLine(attributeName).getCount();
                    if( count == 0 ){
                        attributes.put(attributeName, attributeName);
                    }else {
                        attributes.put(attributeName, vc.getAttribute(attributeName).toString());
                    }
                }

            }
            if(attributes.size() > 0){
                vcb.attributes(attributes);
                newVCs.add(vcb.make());
            }
        }

        return newVCs;

    }
}
