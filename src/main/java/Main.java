import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;

import java.util.Iterator;

/**
 * Created by cel_w on 5/18/2017.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // Load HDT file NOTE: Use loadIndexedHDT() if you are doing ?P?, ?PO, ??O queries
        HDT hdt = HDTManager.loadHDT("D:/Roderick/Documents/Master/5 KRW/milestone4/rdflearning/src/main/java/mappingbased-dbpedia.en.2015-10.hdt", null);

        // Enumerate all triples. Empty string means "any"
        IteratorTripleString it = hdt.search("", "", "");
        System.out.println("Estimated number of results: "+it.estimatedNumResults());
        while(it.hasNext()) {
            TripleString ts = it.next();
            System.out.println(ts);
        }

        // List all predicates
        System.out.println("Dataset contains "+hdt.getDictionary().getNpredicates()+" predicates:");
        Iterator<? extends CharSequence> itPred = hdt.getDictionary().getPredicates().getSortedEntries();
        while(itPred.hasNext()) {
            CharSequence str = itPred.next();
            System.out.println(str);
        }
    }
}
