package org.dakl.exceptions;

/**
 * Created by dankle on 08/02/15.
 */
public class IncorrectNumberOfAlternativeAllelesException extends Throwable{
    public IncorrectNumberOfAlternativeAllelesException(String message, String source){
        super(message);
        System.out.println();
        System.out.println("ERROR: Number of alternative alleles must be one. ");
        System.out.println("Multiallelic variants should be split to multiple records (with monsoo/vcf_parser)");
        System.out.println("The error occured for variant:");
        System.out.println(message);
        System.out.println("Source of the error: " + source);

    }
}
