package org.dakl.walkers;

import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import org.broadinstitute.gatk.engine.arguments.StandardVariantContextInputArgumentCollection;
import org.broadinstitute.gatk.utils.contexts.ReferenceContext;
import org.broadinstitute.gatk.utils.refdata.RefMetaDataTracker;
import org.broadinstitute.gatk.engine.walkers.ReadWalker;
import org.broadinstitute.gatk.utils.commandline.Input;
import org.broadinstitute.gatk.utils.commandline.ArgumentCollection;
import org.broadinstitute.gatk.utils.commandline.Output;
import org.broadinstitute.gatk.utils.sam.GATKSAMRecord;

import java.io.File;
import java.util.HashMap;

/**
 * Created by dankle on 19/08/15.
 */
public class ReplaceReads extends ReadWalker<Integer, Integer> {

    /*
   * Optional parameters
   * */
    @Input(fullName="with_reads", shortName="W", doc="BAM file with replacement reads", required=false)
    protected String replacement_reads_filename;

    @Output
    SAMFileWriter out;

    /*
    * global variables
    * */
    private SamReader replacement_reads_reader;
    private HashMap<String, SAMRecord> replacement_reads;

    @Override
    public void initialize() {
        super.initialize();
        replacement_reads_reader = SamReaderFactory.makeDefault().open(new File(replacement_reads_filename));
        replacement_reads = new HashMap<String, SAMRecord>();
        for(SAMRecord read : replacement_reads_reader ){
            String key = getReadKey(read);
            replacement_reads.put(key, read); // will overwrite if already present
        }
    }


    @Override
    public Integer map(ReferenceContext ref, GATKSAMRecord read, RefMetaDataTracker metaDataTracker) {
        String key = getReadKey(read);
        if( replacement_reads.containsKey(key)){
            SAMRecord replacement_read = replacement_reads.get(key);
            read.setReadBases(replacement_read.getReadBases());
            read.setBaseQualities(replacement_read.getBaseQualities());
        }
        out.addAlignment(read);
        return 1;

    }

    @Override
    public Integer reduceInit() {
        return 0;
    }

    @Override
    public Integer reduce(Integer value, Integer sum) {
        return value+sum;
    }

    private String getReadKey(SAMRecord read){
        String paired = "unpaired";
        if( read.getReadPairedFlag() ){
            paired = read.getFirstOfPairFlag() ? "first" : "second";
        }
        String key = read.getReadName() + "_" + paired;
        return( key );

    }
}
