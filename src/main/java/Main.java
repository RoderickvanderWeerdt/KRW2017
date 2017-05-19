import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;

import java.util.Iterator;
import java.util.Random;
import static java.lang.Math.toIntExact;

/**
 * Created by cel_w on 5/18/2017.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // Load HDT file NOTE: Use loadIndexedHDT() if you are doing ?P?, ?PO, ??O queries
        HDT hdt = HDTManager.loadHDT("../mappingbased-dbpedia.en.2015-10.hdt", null);

        CharSequence newNode = "http://dbpedia.org/resource/Kiss_Kiss_Bang_Bang";
//        newNode = "http://dbpedia.org/resource/Category:Screenplays_by_Shane_Black";
        IteratorTripleString it = hdt.search(newNode, "", "");
        System.out.println("Estimated number of results: "+it.estimatedNumResults());
        Random rand = new Random();
        int next = rand.nextInt(toIntExact(it.estimatedNumResults()));
        int j = 0;
        while(j < 8){
            int i = 0;
        	while(it.hasNext()) {
	        	i++;
	            TripleString ts = it.next();
	//            System.out.println(ts);
	            if(i == next){
	            	newNode = ts.getObject();
	            }
	        }
        	j++;
        	System.out.println(newNode);
        	try{
        		it = hdt.search(newNode.toString(), "", "");
        	}catch (org.rdfhdt.hdt.exceptions.NotFoundException e){
        		System.out.println("Not found.");
        		int old_next = next;
        		while (old_next == next){
                    next = rand.nextInt(toIntExact(it.estimatedNumResults()));
        		}
        	}
            System.out.println("Estimated number of results: "+it.estimatedNumResults());
            next = rand.nextInt(toIntExact(it.estimatedNumResults()));
            System.out.println("----");
        }

//        // List all predicates
//        System.out.println("Dataset contains "+hdt.getDictionary().getNpredicates()+" predicates:");
//        Iterator<? extends CharSequence> itPred = hdt.getDictionary().getPredicates().getSortedEntries();
//        while(itPred.hasNext()) {
//            CharSequence str = itPred.next();
//            System.out.println(str);
//        }
    }
}
