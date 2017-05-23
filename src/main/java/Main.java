import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;

//import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;
import static java.lang.Math.toIntExact;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

/**
 * Created by cel_w on 5/18/2017.
 * Load HDT file NOTE: Use loadIndexedHDT() if you are doing ?P?, ?PO, ??O queries
 * CharSequence movieTitle looks like: "http://dbpedia.org/resource/Kiss_Kiss_Bang_Bang";
 */
public class Main {
	private static boolean printInfo = false;
	
	//code from: http://javadevnotes.com/java-read-text-file-examples
    public static ArrayList<CharSequence> readFile() {
        ArrayList<CharSequence> movieURIs = new ArrayList<CharSequence>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("../mc-movies.pruned.train.tsv"));
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
            	CharSequence movieTitle = line.split("\t")[1];
                movieURIs.add(movieTitle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		return movieURIs;
    }
    
	
    private static void writeRandomWalksToFile(String fileName, ArrayList<ArrayList<CharSequence>> allRandomWalks) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(fileName));
		for (ArrayList<CharSequence> randomWalk : allRandomWalks) {
			for (CharSequence step : randomWalk) {
				pw.write(step.toString());
				pw.write(' ');
			}
			pw.write("\n");
		}
		pw.close();
    }
	
	public static ArrayList<CharSequence> randomWalk(HDT hdt, CharSequence movieTitle, int maxSteps) throws Exception {
		
		Random rand = new Random();
    	CharSequence newNode = movieTitle;
    	
        int nSteps = 0;
        ArrayList<CharSequence> randomWalk = new ArrayList<CharSequence>();
        CharSequence predicate = "";
        boolean coin = true;
        IteratorTripleString it = hdt.search(newNode.toString(), "", "");
        randomWalk.add(newNode);
    	
        while(nSteps < maxSteps){
        	nSteps++;

        	try{
        		if(coin){
        			it = hdt.search(newNode.toString(), "", "");
        		} else {
        			it = hdt.search("", "", newNode.toString());
        		}
        		if(printInfo){System.out.println("Estimated number of results: "+it.estimatedNumResults()+" --- "+newNode.toString());}
        	}catch (org.rdfhdt.hdt.exceptions.NotFoundException e){
        		if(printInfo){System.out.println(newNode.toString() + " has zero outgoing edges.");}
        		randomWalk.remove(newNode.toString());
        		randomWalk.remove(predicate);
        	}
        	
        	coin = rand.nextBoolean();
        	coin = true; //only forward
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
	        		if(printInfo){System.out.print("\nTrying ");System.out.println(newNode);}
	            }
	        }
        }
        if(printInfo){
            for(int i = 0; i < randomWalk.size(); i++){
                System.out.print(randomWalk.get(i));
                System.out.print(" -> ");
            }System.out.println("\n-----\n");	
        }
        return randomWalk;
	}
    
    private static ArrayList<ArrayList<CharSequence>> createRandomWalks(HDT hdt, ArrayList<CharSequence> movieURIs, int nMovies, int nRandomWalks, int maxRandomWalkLength) throws Exception {
    	if (nMovies > movieURIs.size()){
    		nMovies = movieURIs.size();
    	}
        int movieCount = 0;
        int shorterThan = 0;
        
        ArrayList<ArrayList<CharSequence>> allRandomWalks = new ArrayList<ArrayList<CharSequence>>();
        ArrayList<CharSequence> randomWalk;
        for (CharSequence movieTitle : movieURIs)  {
        	movieCount++;
	        System.out.print("Creating a randomwalks starting from: ");
	        System.out.println(movieTitle);
	        for(int i = 0; i < nRandomWalks; i++){
	        	randomWalk = randomWalk(hdt, movieTitle, maxRandomWalkLength);
	        	allRandomWalks.add(randomWalk);
	        	if ((randomWalk.size()/2) + 1 < 8){
		        	shorterThan++;
	        	}
	        }
	        if (movieCount >= nMovies){
	        	break;
	        }
        }
        if(printInfo){
        	System.out.print(shorterThan);
            System.out.println(" randomwalks are shorter than 8.");	
        }
        return allRandomWalks;
    }
	
    public static void main(String[] args) throws Exception {
        HDT hdt = HDTManager.loadHDT("../mappingbased-dbpedia.en.2015-10.hdt", null);
        
        ArrayList<CharSequence> movieURIs = readFile();
        
        int nMovies = 10;
        int nRandomWalks = 100;
        int maxRandomWalkLength = 8;
        ArrayList<ArrayList<CharSequence>> allRandomWalks = createRandomWalks(hdt, movieURIs, nMovies, nRandomWalks, maxRandomWalkLength);
        
        writeRandomWalksToFile("../superSmallRandomWalks.txt", allRandomWalks);
    }
}
