package org.dakl.walkers;

import org.broadinstitute.gatk.engine.contexts.ReferenceContext;
import org.broadinstitute.gatk.engine.refdata.RefMetaDataTracker;
import org.broadinstitute.gatk.engine.walkers.NanoSchedulable;
import org.broadinstitute.gatk.engine.walkers.ReadWalker;
import org.broadinstitute.gatk.utils.commandline.Argument;
import org.broadinstitute.gatk.utils.commandline.Output;
import org.broadinstitute.gatk.utils.sam.GATKSAMRecord;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Counts the number of telomeric reads (reads that contain 4xTTAGGG or 4xCCCTAA)
 */

/*
 CMDs:
 To test: java -Xmx2g -jar dist/GenomeAnalysisTK.jar -T TelomereDeerb -I ~/tmp/somaticaller/BRC_T2.bam -R ~/tmp/somaticaller/human_g1k_v37_clean.fasta
 Multiple samples: java -Xmx2g -jar dist/GenomeAnalysisTK.jar -T TelomereDeerb -I ~/tmp/somaticaller/BRC_T2.bam -R ~/tmp/somaticaller/human_g1k_v37_clean.fasta -I ~/tmp/somaticaller/BRC_N2.bam -I ~/tmp/somaticaller/OVC2_OVC_T2.bam
 */

public class TelomereQuant extends ReadWalker<Integer, Long> implements NanoSchedulable {
    private class TelomereQuantResult{
        private int totalReadCount;
        private int telomereReadCount;
        private int totalBases;
        private int telomereSequenceCount;

        public TelomereQuantResult( ){
            this.totalReadCount = 0;
            this.telomereReadCount = 0;
            this.totalBases = 0;
            this.telomereSequenceCount = 0;
        }
        // getters
        public int getTotalReadCount(){
            return( this.totalReadCount );
        }
        public int getTotalBases(){
            return( this.totalBases );
        }
        public int getTelomereReadCount(){
            return( this.telomereReadCount );
        }
        public int getTotalTelomereSequenceCount(){
            return( this.telomereSequenceCount );
        }
        // incrementors
        public int incrementTotalReadCount(){
            return( this.totalReadCount++ );
        }
        public int incrementTelomereReadCount(){
            return( this.telomereReadCount++ );
        }
        public void incrementTelomereSeqCount(Integer count){
            this.telomereSequenceCount += count;
        }
        public void incrementTotalBases(Integer count){
            this.totalBases += count;
        }

    }

    @Output
    private PrintStream out;

    @Argument(fullName = "number_of_repeats", shortName = "NR", doc="Number of repeats of telomere sequence required for a read to be classified as telomeric.", required = false)
    Integer NUMBER_OF_REPEATS = 4;

    private Map<String, TelomereQuantResult> tdResults = new HashMap<String, TelomereQuantResult>();

    private String teloseq_f = "TTAGGG"; //4xTTAGGG
    private String teloseq_r = "CCCTAA"; //4xCCCTAA
    // ref: Assessing telomeric DNA content in pediatric cancers using whole-genome sequencing data
    //      Parker et al. Genome Biology 2012, 13:R113
    private String teloseq_f_rep = repeat( teloseq_f, NUMBER_OF_REPEATS);
    private String teloseq_r_rep = repeat( teloseq_r, NUMBER_OF_REPEATS);

    /*NOTE: map() is NOT thread safe */
    public Integer map(ReferenceContext ref, GATKSAMRecord read, RefMetaDataTracker tracker) {
        String readseq             = read.getReadString();
        String sample              = read.getReadGroup().getSample();

        // add result object if absent
        if( ! tdResults.containsKey( sample ) ) {
            tdResults.put(sample, new TelomereQuantResult());
        }

        int readLength = read.getReadLength();
        int telomereCount = countSubstring(readseq, teloseq_f) + countSubstring(readseq, teloseq_r);
        boolean hasTelomere4Seq = readseq.toUpperCase().contains( teloseq_f_rep ) || readseq.toUpperCase().contains(teloseq_r_rep);

        if( hasTelomere4Seq ) tdResults.get(sample).incrementTelomereReadCount();
        tdResults.get(sample).incrementTelomereSeqCount(telomereCount);
        tdResults.get(sample).incrementTotalBases(readLength);

        if( hasTelomere4Seq ) return 0;
        else return 1;

    }

    @Override public Long reduceInit() {
        return 0L; }

    public Long reduce(Integer value, Long sum) {
        return (long) value + sum;
    }

    public void onTraversalDone(Long result) {
        //logger.info("CountReads counted " + result + " reads in the traversal");
        out.println("BAM\tTOTAL_READ_COUNT\tTOTAL_BASES\tTELOMERE_READ_COUNT\tTELOMERE_SEQ_COUNT");
        for( String sample : tdResults.keySet() ){
            out.println(sample + "\t" +
                    tdResults.get(sample).getTotalReadCount() + "\t" +
                    tdResults.get(sample).getTotalBases()     + "\t" +
                    tdResults.get(sample).getTotalReadCount() + "\t" +
                    tdResults.get(sample).getTotalTelomereSequenceCount() );
        }
    }

    // count occurences of substring in string
    // from http://stackoverflow.com/questions/767759/occurrences-of-substring-in-a-string
    public static int countSubstring(String str, String substring){
        int lastIndex = 0;
        int count =0;

        while(lastIndex != -1){

            lastIndex = str.indexOf(substring,lastIndex);

            if( lastIndex != -1){
                count ++;
                lastIndex+=substring.length();
            }
        }
        return( count );
    }

    public static String repeat(String str, int times){
        return new String(new char[times]).replace("\0", str);
    }
}
