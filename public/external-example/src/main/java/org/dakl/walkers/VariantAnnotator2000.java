package org.dakl.walkers;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.*;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.broadinstitute.gatk.engine.GATKVCFUtils;
import org.broadinstitute.gatk.engine.SampleUtils;
import org.broadinstitute.gatk.engine.arguments.StandardVariantContextInputArgumentCollection;
import org.broadinstitute.gatk.engine.walkers.RodWalker;
import org.broadinstitute.gatk.utils.BaseUtils;
import org.broadinstitute.gatk.utils.commandline.*;
import org.broadinstitute.gatk.utils.contexts.AlignmentContext;
import org.broadinstitute.gatk.utils.contexts.AlignmentContextUtils;
import org.broadinstitute.gatk.utils.contexts.ReferenceContext;
import org.broadinstitute.gatk.utils.exceptions.UserException;
import org.broadinstitute.gatk.utils.refdata.RefMetaDataTracker;
import org.dakl.exceptions.IncorrectNumberOfAlternativeAllelesException;

import java.util.*;

/**
 * Created by dankle on 06/02/15.
 */
public class VariantAnnotator2000  extends RodWalker<Integer, Integer> {
    @ArgumentCollection
    protected StandardVariantContextInputArgumentCollection variantCollection = new StandardVariantContextInputArgumentCollection();

    @Input(fullName="resource", shortName = "resource", doc="External resource VCF file", required=false)
    public List<RodBinding<VariantContext>> resources = Collections.emptyList();
    public List<RodBinding<VariantContext>> getResourceRodBindings() { return resources; }

    @Input(fullName="dbsnp", shortName = "dbsnp", doc="dbSNP VCF", required=false)
    RodBinding<VariantContext> dbSNPBinding = null;

    @Argument(fullName="alwaysAppendDbsnpId", shortName="alwaysAppendDbsnpId", doc="Append the dbSNP ID even when the variant VCF already has the ID field populated", required=false)
    protected Boolean ALWAYS_APPEND_DBSNP_ID = false;

    @Output(doc="File to which variants should be written")
    protected VariantContextWriter vcfWriter = null;
    /**
     * This option enables you to add annotations from one VCF to another.
     *
     * For example, if you want to annotate your callset with the AC field value from a VCF file named
     * 'resource_file.vcf', you tag it with '-resource:my_resource resource_file.vcf' (see the -resource argument, also
     * documented on this page) and you specify '-E my_resource.AC'. In the resulting output VCF, any records for
     * which there is a record at the same position in the resource file will be annotated with 'my_resource.AC=N'.
     * Note that if there are multiple records in the resource file that overlap the given position, one is chosen
     * randomly.
     */
    @Argument(fullName="expression", shortName="E", doc="One or more specific expressions to apply to variant calls", required=false)
    protected Set<String> expressionsToUse = new ObjectOpenHashSet();

    private List<VAExpression> requestedExpressions = new ArrayList<VAExpression>();

    public void initialize() {
        this.initializeExpressions(this.expressionsToUse);

        Map<String, VCFHeader> vcfRods = GATKVCFUtils.getVCFHeadersFromRods(getToolkit());

        // get the list of all sample names from the variant VCF input rod, if applicable
        final List<String> rodName = Arrays.asList(variantCollection.variants.getName());
        final Set<String> samples = SampleUtils.getUniqueSamplesFromRods(getToolkit(), rodName);

        // setup the header fields
        // note that if any of the definitions conflict with our new ones, then we want to overwrite the old ones
        final Set<VCFHeaderLine> hInfo = new HashSet<VCFHeaderLine>();
        for ( final VCFHeaderLine line : GATKVCFUtils.getHeaderFields(getToolkit(), Arrays.asList(variantCollection.variants.getName())) ) {
            if ( isUniqueHeaderLine(line, hInfo) )
                hInfo.add(line);
        }

        hInfo.add(new VCFHeaderLine(VCFHeader.SOURCE_KEY, "VariantAnnotator2000"));

        for ( final VCFHeaderLine line : GATKVCFUtils.getHeaderFields(getToolkit(), Arrays.asList(variantCollection.variants.getName())) ) {
            if ( isUniqueHeaderLine(line, hInfo) )
                hInfo.add(line);
        }
        // for the expressions, pull the info header line from the header of the resource rod
        for ( final VAExpression expression : this.requestedExpressions ) {
            logger.info("adding " + expression.fieldName + " INFO fields from " + expression.binding.getSource());

            // special case the ID field
            if ( expression.fieldName.equals("ID") ) {
                hInfo.add(new VCFInfoHeaderLine(expression.fullName, 1, VCFHeaderLineType.String, "ID field transferred from external VCF resource"));
                continue;
            }
            VCFInfoHeaderLine targetHeaderLine = null;
            for ( final VCFHeaderLine line : GATKVCFUtils.getHeaderFields(getToolkit(), Arrays.asList(expression.binding.getName())) ) {
                if ( line instanceof VCFInfoHeaderLine ) {
                    final VCFInfoHeaderLine infoline = (VCFInfoHeaderLine)line;
                    if ( infoline.getID().equals(expression.fieldName) ) {
                        targetHeaderLine = infoline;
                        break;
                    }
                }
            }

            if ( targetHeaderLine != null ) {
                if ( targetHeaderLine.getCountType() == VCFHeaderLineCount.INTEGER )
                    hInfo.add(new VCFInfoHeaderLine(expression.fullName, targetHeaderLine.getCount(), targetHeaderLine.getType(), targetHeaderLine.getDescription()));
                else
                    hInfo.add(new VCFInfoHeaderLine(expression.fullName, targetHeaderLine.getCountType(), targetHeaderLine.getType(), targetHeaderLine.getDescription()));
            } else {
                hInfo.add(new VCFInfoHeaderLine(expression.fullName, VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String, "Value transferred from another external VCF resource"));
            }
        }

        VCFHeader vcfHeader = new VCFHeader(hInfo, samples);
        vcfWriter.writeHeader(vcfHeader);

    }

    @Override
    public Integer map(RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
        if ( tracker == null )
            return 0;

        Collection<VariantContext> VCs = tracker.getValues(variantCollection.variants, context.getLocation());
        if ( VCs.size() == 0 )
            return 0;

        Collection<VariantContext> annotatedVCs = VCs;
        try {
            // if the reference base is not ambiguous, we can annotate
            Map<String, AlignmentContext> stratifiedContexts;
            if (BaseUtils.simpleBaseToBaseIndex(ref.getBase()) != -1) {
                stratifiedContexts = AlignmentContextUtils.splitContextBySampleName(context.getBasePileup());
                annotatedVCs = new ArrayList<VariantContext>(VCs.size());
                for (VariantContext vc : VCs) {
                    logger.info(vc.getChr() + " " + ref.getLocus().getStart() + " " + vc.getReference() + " " + vc.getAlternateAllele(0));

                    HashMap<String, Object> attributesToAdd = new HashMap<String, Object>();

                    if (vc.getAlternateAlleles().size() != 1) {
                        throw new IncorrectNumberOfAlternativeAllelesException(vc.toString(), variantCollection.variants.getName());
                    }

                    for (VAExpression expr : requestedExpressions) {
                        final Collection<VariantContext> expressionVCs = tracker.getValues(expr.binding, ref.getLocus());
                        if (expressionVCs.size() == 0)
                            continue;

                        for (VariantContext expressionVC : expressionVCs) {
                            if (expressionVC.getAlternateAlleles().size() != 1) {
                                throw new IncorrectNumberOfAlternativeAllelesException(vc.toString(), expr.binding.getName());
                            }
                            // require chr, pos,ref and alt alleles to be identical in order to annotate
                            if (expressionVC.getChr().equals(vc.getChr()) &&
                                    expressionVC.getStart() == vc.getStart() &&
                                    expressionVC.getReference().equals(vc.getReference()) &&
                                    expressionVC.getAlternateAllele(0).equals(vc.getAlternateAllele(0))) {



                                String attr = (String) expressionVC.getCommonInfo().getAttribute(expr.fieldName, "");
                                String key = expr.fullName;
                                logger.info("Added INFO: " + key + "=" + attr);
                                if (attributesToAdd.containsKey(key)) {
                                    logger.info("Attribute present. Concatenating value.");
                                    String newAttr = attributesToAdd.get(key) + "|" + attr;
                                    attributesToAdd.put(key, newAttr);

                                } else {
                                    logger.info("Attribute not present. Adding it. ");
                                    attributesToAdd.put(key, attr);
                                }
                            }
                        }
                    }

                    String rsid = vc.getID();
                    final Collection<VariantContext> dbsnpVCs = tracker.getValues(dbSNPBinding, ref.getLocus());
                    for (VariantContext dbsnpVC : dbsnpVCs) {
                        // require chr, pos,ref and alt alleles to be identical in order to annotate
                        if (dbsnpVC.getChr().equals(vc.getChr()) &&
                                dbsnpVC.getStart() == vc.getStart() &&
                                dbsnpVC.getReference().equals(vc.getReference()) &&
                                dbsnpVC.getAlternateAllele(0).equals(vc.getAlternateAllele(0))) {

                            if (!dbsnpVC.emptyID()) { // if there's no ID in dbSNP, use input files ID
                                if (rsid.equals(".")) {
                                    rsid = dbsnpVC.getID();
                                } else if (!rsid.equals(".") && ALWAYS_APPEND_DBSNP_ID) {
                                    rsid = dbsnpVC.getID();
                                }
                            }
                        }
                    }

                    if (attributesToAdd.isEmpty()) {
                        vcfWriter.add(vc);
                    } else {
                        logger.info("Added annotations...");
                        for (String key : vc.getAttributes().keySet()) {
                            attributesToAdd.put(key, vc.getAttribute(key));
                        }
                        VariantContext newVC = new VariantContextBuilder(vc).id(rsid).attributes(attributesToAdd).make();
                        vcfWriter.add(newVC);
                    }

                }

            }
        } catch(IncorrectNumberOfAlternativeAllelesException e )
        {
            System.exit(1);
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

    /**
     * Tell the user the number of loci processed and close out the new variants file.
     *
     * @param result  the number of loci seen.
     */
    public void onTraversalDone(Integer result) {
        logger.info("Processed " + result + " loci.\n");
    }





    // select specific expressions to use
    public void initializeExpressions(Set<String> expressionsToUse) {
        // set up the expressions
        for (final String expression : expressionsToUse)
            requestedExpressions.add(new VAExpression(expression, this.getResourceRodBindings()));
    }


    protected static class VAExpression {

        public String fullName, fieldName;
        public RodBinding<VariantContext> binding;

        public VAExpression(String fullExpression, List<RodBinding<VariantContext>> bindings) {
            final int indexOfDot = fullExpression.lastIndexOf(".");
            if ( indexOfDot == -1 )
                throw new UserException.BadArgumentValue(fullExpression, "it should be in rodname.value format");

            fullName = fullExpression;
            fieldName = fullExpression.substring(indexOfDot+1);

            final String bindingName = fullExpression.substring(0, indexOfDot);
            for ( final RodBinding<VariantContext> rod : bindings ) {
                if ( rod.getName().equals(bindingName) ) {
                    binding = rod;
                    break;
                }
            }
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
