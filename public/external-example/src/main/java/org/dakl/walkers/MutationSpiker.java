package org.dakl.walkers;

import htsjdk.samtools.*;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import org.apache.commons.lang.ArrayUtils;
import org.broadinstitute.gatk.engine.arguments.StandardVariantContextInputArgumentCollection;
import org.broadinstitute.gatk.engine.contexts.AlignmentContext;
import org.broadinstitute.gatk.engine.contexts.ReferenceContext;
import org.broadinstitute.gatk.engine.refdata.RefMetaDataTracker;
import org.broadinstitute.gatk.engine.walkers.Allows;
import org.broadinstitute.gatk.engine.walkers.By;
import org.broadinstitute.gatk.engine.walkers.DataSource;
import org.broadinstitute.gatk.engine.walkers.RefWalker;
import org.broadinstitute.gatk.utils.GenomeLoc;
import org.broadinstitute.gatk.utils.commandline.Argument;
import org.broadinstitute.gatk.utils.commandline.ArgumentCollection;
import org.broadinstitute.gatk.utils.commandline.Output;
import org.broadinstitute.gatk.utils.pileup.PileupElement;
import org.broadinstitute.gatk.utils.pileup.ReadBackedPileup;
import org.broadinstitute.gatk.utils.sam.GATKSAMRecord;

import java.io.File;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Walker that spikes in mutations in a BAM file
 */
@Allows(value={DataSource.READS, DataSource.REFERENCE})
@By(DataSource.REFERENCE)
public class MutationSpiker extends RefWalker<Integer, Integer> {
    @Argument(fullName="out", shortName="o", doc="Mutated BAM file")
    private String bamFileName;

    /*@Output(doc="VCF with mutated positions")
    private VariantContextWriter vcf; */

    @ArgumentCollection
    protected StandardVariantContextInputArgumentCollection variantCollection = new StandardVariantContextInputArgumentCollection();

    /*
   * Optional parameters
   * */
    @Argument(fullName="mindepth", shortName="md", doc="Minimum BAM depth of a position to be considered (default 30)", required=false)
    protected int minDepth = 30;

    @Argument(fullName="mutation_fraction", shortName="m", doc="Mutation fraction (default 0.3)", required=false)
    protected double mutationFraction = 0.3;

    @Argument(fullName="indel_overhang", shortName="io", doc="Overhang required after indel to mutate read", required=false)
    protected int indelOverhang = 2;

    //@Output
    //SAMFileWriter output;

    private SAMFileWriter bam;

    @Override
    public void initialize() {
        super.initialize();
        SAMFileWriterFactory samFactory = new SAMFileWriterFactory();
        bam = samFactory.makeBAMWriter(this.getToolkit().getSAMFileHeader(), false, new File(bamFileName) );
    }

    @Override
    public Integer map(RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
        if ( tracker == null ){ return 0; }

        Collection<VariantContext> VCs = tracker.getValues(variantCollection.variants, context.getLocation());
        if ( VCs.size() == 0 ){ return 0; }

        ReadBackedPileup pileup = context.getBasePileup();

        // Skip position if coverage is insufficient, ignoring MAPQ0 reads
        if ( pileup.depthOfCoverage() < minDepth ){ return 0; }
        System.out.println("POS=" + context.getLocation().getStart());

        for ( VariantContext vc : VCs ){

            // for each read that covers the position,
            //   in X % of reads
            //     mutate base to ALT
            for(PileupElement p : pileup){
                int delSize = vc.getReference().getBases().length - 1;
                // require overhang after deletion in order to mutate
                if(p.getOffset()+delSize+indelOverhang <= p.getRead().getReadLength() ) {
                    if( Math.random() < mutationFraction ) { // modify base if random double is smaller than mutationFraction
                        GATKSAMRecord mutatedRead = mutateRead(p, vc);
                        bam.addAlignment(mutatedRead);
                    }
                }
            }
        }
        System.out.println("-Done");

        return 1;
    }

    @Override
    public Integer reduceInit() {
        return null;
    }

    @Override
    public Integer reduce(Integer value, Integer sum) {
        return null;
    }

    @Override
    public void onTraversalDone(Integer result) {
        bam.close();
    }

    public GATKSAMRecord mutateRead(PileupElement p, VariantContext vc){

        GATKSAMRecord read = (GATKSAMRecord)p.getRead().clone();
        int baseOffset = p.getOffset();
        byte[] readBases = read.getReadBases();

        if( vc.isSNP() ) {
            System.out.print("M");

            readBases[baseOffset] = vc.getAltAlleleWithHighestAlleleCount().getBases()[0];
            read.setReadBases(readBases);

        }
        else if( vc.isSimpleDeletion() ){
            System.out.print("D");

            int delSize = vc.getReference().getBases().length - 1;
            List<Byte> readBaseList = byteArrayToByteObjectArray(readBases);
            List<Byte> baseQualList = byteArrayToByteObjectArray(read.getBaseQualities());

            for( int i=0; i<delSize; i++ ){
                if(baseOffset < readBaseList.size()) {
                    // example: i size = 5, last base offset = 4
                    readBaseList.remove(baseOffset);
                    baseQualList.remove(baseOffset);
                }
            }
            read.setReadBases(byteArrayToPrimitive(readBaseList));
            read.setBaseQualities(byteArrayToPrimitive(baseQualList));

        }
        else if( vc.isSimpleInsertion() ){
            System.out.print("I");

            List<Byte> readBaseList = byteArrayToByteObjectArray(readBases);
            List<Byte> baseQualList = byteArrayToByteObjectArray(read.getBaseQualities());
            List<Byte> basesToInsert         = byteArrayToByteObjectArray( vc.getAltAlleleWithHighestAlleleCount().getBases() );

            int currentOffset = baseOffset + 1;
            Byte qualToInsert = baseQualList.get(baseOffset);
            basesToInsert.remove(0);

            for(Byte baseToInsert : basesToInsert ){
                readBaseList.add(currentOffset, baseToInsert);
                baseQualList.add(currentOffset, qualToInsert);
                currentOffset++;

            }
            read.setReadBases(byteArrayToPrimitive(readBaseList));
            read.setBaseQualities(byteArrayToPrimitive(baseQualList));

        } else {
            System.out.print("-");
        }



        return read;
    }

    private List<Byte> byteArrayToByteObjectArray(byte[] bytes){
        Byte[] byteObjects = new Byte[bytes.length];

        int i=0;
        // Associating Byte array values with bytes. (byte[] to Byte[])
        for(byte b: bytes)
            byteObjects[i++] = b;  // Autoboxing.

        // returns a LinkedList which is mutable, List is immutable
        return new LinkedList<Byte>(Arrays.asList(byteObjects));
    }

    private byte[] byteArrayToPrimitive(List<Byte> array){
        Byte[] byteArray = array.toArray(new Byte[array.size()]);
        byte[] bytes = ArrayUtils.toPrimitive( byteArray );
        return bytes;
    }
}

/*
*             Cigar cigar = read.getCigar();
            List<CigarElement> cigarElements = new ArrayList<CigarElement>();
            for(CigarElement ce : cigar.getCigarElements() ){
                CigarElement newCe = new CigarElement(1, ce.getOperator() );
                cigarElements.add(newCe);
            }
            System.out.println(cigarElements.toString());
            int delSize = vc.getAltAlleleWithHighestAlleleCount().getBases().length - 1;
            System.out.print("CIGAR=" + cigarElements.get(offset) + ", ");

* */