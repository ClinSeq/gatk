package org.dakl.walkers;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.*;
import org.broadinstitute.gatk.engine.contexts.AlignmentContext;
import org.broadinstitute.gatk.engine.contexts.ReferenceContext;
import org.broadinstitute.gatk.engine.refdata.RefMetaDataTracker;
import org.broadinstitute.gatk.engine.walkers.RodWalker;
import org.broadinstitute.gatk.utils.commandline.*;
import org.broadinstitute.gatk.utils.variant.GATKVCFUtils;

import java.util.*;

/**
 * Created by dankle on 20/08/14.
 */
public class SelectPathogenicVariants extends RodWalker<Integer, Integer> {

    //<ID=CLNSIG,Number=.,Type=String,Description="Variant Clinical Significance, 0 - unknown, 1 - untested, 2 - non-pathogenic, 3 - probable-non-pathogenic, 4 - probable-pathogenic, 5 - pathogenic, 6 - drug-response, 7 - histocompatibility, 255 - other">
    final static String CLNSID_PATHOGENIC_CODE = "5";
    final static String CLNSIG_ATTR_KEY = "CLNSIG";

    @Input(fullName="variant", shortName = "V", doc="Input VCF file", required=true)
    final private RodBinding<VariantContext> variants = null;

    @Output(doc="File to which variants should be written")
    protected VariantContextWriter vcfWriter = null;

    @Argument(fullName="genes", shortName="G", doc="String of gene names from which variants are selected. Multiple allowed. If not specified, all genes are selected", required=false)
    public List<String> genesToSelect = null;

    @Argument(fullName="GENEINFO", shortName="GI", doc="Name of the field containing gene info. ", required=false)
    public String GENEINFO_ATTR_KEY = "GENEINFO";
    //##INFO=<ID=GENEINFO,Number=1,Type=String,Description="Pairs each of gene symbol:gene id.  The gene symbol and id are delimited by a colon (:) and each pair is delimited by a vertical bar (|)">

    @Override
    public void initialize() {
        super.initialize();

        // set up header, merge from input file(s)
        Map<String, VCFHeader> vcfRods = GATKVCFUtils.getVCFHeadersFromRods(getToolkit());
        Set<VCFHeaderLine> headerLines = VCFUtils.smartMergeHeaders(vcfRods.values(), true);
        VCFHeader vcfHeader = new VCFHeader(headerLines);
        vcfWriter.writeHeader(vcfHeader);
    }

    @Override
    public Integer map(RefMetaDataTracker refMetaDataTracker, ReferenceContext referenceContext, AlignmentContext alignmentContext) {
        if ( refMetaDataTracker == null )
            return 0;

        int variantsWritten = 0;
        Collection<VariantContext> vcs = refMetaDataTracker.getValues(variants, alignmentContext.getLocation());
        for( VariantContext vc : vcs ){

            // if genes are unset (==null), write all pathogenic variants
            if( genesToSelect == null && variantIsPathogenic(vc)){
                vcfWriter.add(vc);
                variantsWritten++;

            // if genes are set, write pathogenic variants that map to genes of interest
            } else if ( genesToSelect != null && variantMapsToGeneOfInterest(vc) && variantIsPathogenic(vc)){
                vcfWriter.add(vc);
                variantsWritten++;
            }

        }

        return variantsWritten;
    }

    @Override
    public Integer reduceInit() {
        return 0;
    }

    @Override
    public Integer reduce(Integer lhs, Integer rhs) {
        return lhs + rhs;
    }

    private boolean variantMapsToGeneOfInterest(VariantContext vc){
        if(! vc.hasAttribute(GENEINFO_ATTR_KEY))
            return false;

        // each element will be on the form symbol:gene_id, elements separated by vertical bar ("|")
        List<String> geneElements = Arrays.asList( vc.getAttribute(GENEINFO_ATTR_KEY).toString().split("\\|") );
        List<String> geneSymbols = new ArrayList<String>();
        for( String geneElement : geneElements ){
            // split by comma, first element will be symbol
            String symbol =  Arrays.asList(geneElement.split(":")).get(0);
            geneSymbols.add( symbol );
        }
        //System.out.print("Current gene:"+geneSymbols + ", Looking for " + genesToSelect + "\n");

        return ! Collections.disjoint(geneSymbols, genesToSelect);
    }

    private boolean variantIsPathogenic(VariantContext vc){
        if(! vc.hasAttribute( CLNSIG_ATTR_KEY ))
            return false;

        // CLNSIG can be separated by vertical bar ("|") and/or comma (","), so handle both
        List clnsig_values = Arrays.asList( vc.getAttribute(CLNSIG_ATTR_KEY, 0).toString().split("(,)|(\\|)") ); //
        return clnsig_values.contains( CLNSID_PATHOGENIC_CODE );
    }


}
