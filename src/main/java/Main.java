import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;

import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;
import static java.lang.Math.toIntExact;

/**
 * Created by cel_w on 5/18/2017.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // Load HDT file NOTE: Use loadIndexedHDT() if you are doing ?P?, ?PO, ??O queries
        HDT hdt = HDTManager.loadHDT("../mappingbased-dbpedia.en.2015-10.hdt", null);
        Random rand = new Random();
        
    	CharSequence newNode = "http://dbpedia.org/resource/Kiss_Kiss_Bang_Bang";
    	
        int j = 0;
        ArrayList<CharSequence> randomWalk = new ArrayList<CharSequence>();
        CharSequence predicate = "";
        IteratorTripleString it = hdt.search(newNode.toString(), "", "");
        randomWalk.add(newNode);
    	
        while(j < 8){
        	j++;

        	try{
				it = hdt.search(newNode.toString(), "", "");
	            System.out.println("Estimated number of results: "+it.estimatedNumResults()+" --- "+newNode.toString());
        	}catch (org.rdfhdt.hdt.exceptions.NotFoundException e){
        		System.out.println(newNode.toString() + " has zero outgoing edges.");
        		randomWalk.remove(newNode.toString());
        		randomWalk.remove(predicate);
        	}
            
            int next = rand.nextInt(toIntExact(it.estimatedNumResults()));
            int i = 0;
            it.goToStart();
        	while(it.hasNext()) {
	        	i++;
	            TripleString ts = it.next();
	            if(i == next){
	            	predicate = ts.getPredicate().toString();
	            	newNode = ts.getObject();
	        		randomWalk.add(predicate);
	        		randomWalk.add(newNode.toString());
	                System.out.print("\nTrying ");
	                System.out.println(newNode);
	            }
	        }
        }
        System.out.println("\nThe randomwalk:");
        for(int i = 0; i < randomWalk.size(); i++){
            System.out.println(randomWalk.get(i));	
        }

    }
}
