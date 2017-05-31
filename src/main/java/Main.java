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
//import java.io.PrintWriter;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
//import java.io.FileWriter;

/**
 * Created by cel_w on 5/18/2017.
 * Load HDT file NOTE: Use loadIndexedHDT() if you are doing ?P?, ?PO, ??O queries
 * CharSequence movieTitle looks like: "http://dbpedia.org/resource/Kiss_Kiss_Bang_Bang";
 */
public class Main {
	private static boolean printInfo = false;
	private static boolean singleWrite = true;
	
	//code from: http://javadevnotes.com/java-read-text-file-examples
    public static ArrayList<CharSequence> readFile(String filename) {
        ArrayList<CharSequence> movieURIs = new ArrayList<CharSequence>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
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
	
    private static IteratorTripleString nextStep(HDT hdt, Random rand, CharSequence movieTitle) throws Exception {
    	try {
        	IteratorTripleString it_forward = hdt.search(movieTitle.toString(), "", "");    	
        	try {
        		IteratorTripleString it_back = hdt.search("", "", movieTitle.toString());
            	int weightedCoin = rand.nextInt(toIntExact(it_forward.estimatedNumResults() + it_back.estimatedNumResults()));
            	if (weightedCoin < it_forward.estimatedNumResults()){
            		return it_forward;
            	}else{
            		return it_back;
            	}
        	} catch (org.rdfhdt.hdt.exceptions.NotFoundException e){
            	return it_forward;
        	}    	
    	}catch (org.rdfhdt.hdt.exceptions.NotFoundException e){
    		try {
        		IteratorTripleString it_back = hdt.search("", "", movieTitle.toString());
            	return it_back;
        	} catch (org.rdfhdt.hdt.exceptions.NotFoundException e2){
            	return null;
        	}
    	}

//    	long possDirections = it_forward.estimatedNumResults() + it_back.estimatedNumResults();
    }
    
	public static ArrayList<CharSequence> randomWalk(HDT hdt, CharSequence movieTitle, int maxSteps, Writer pw) throws Exception {
//		movieTitle = "http://dbpedia.org/resource/White_Noise_(film)";
		Random rand = new Random();
    	CharSequence newNode = movieTitle;
    	
        int nSteps = 0;
        ArrayList<CharSequence> randomWalk = new ArrayList<CharSequence>();
        CharSequence predicate = "";
        randomWalk.add(newNode);
        IteratorTripleString it = nextStep(hdt, rand, newNode);
    	CharSequence nextNode;
    	int i = 0;
    	int estimate = 0;
        while(nSteps < maxSteps){
        	nSteps++;

        	int next;
        	try{
        		it = nextStep(hdt, rand, newNode);
        		if(printInfo){System.out.println("Estimated number of results: "+it.estimatedNumResults()+" --- "+newNode.toString());}
        		if(estimate == it.estimatedNumResults()){
            		next = rand.nextInt(toIntExact(i+1)); //+1 because 0 crashes
        		}else{
            		next = rand.nextInt(toIntExact(it.estimatedNumResults()));
        		}
        	}catch (org.rdfhdt.hdt.exceptions.NotFoundException e){
        		if(printInfo){System.out.println(newNode.toString() + " has zero outgoing edges.");}
        		randomWalk.remove(newNode.toString());
        		randomWalk.remove(predicate);
        		next = rand.nextInt(toIntExact(it.estimatedNumResults()));
        	}
        	
            i = 0;
            estimate = toIntExact(it.estimatedNumResults());
            it.goToStart();
        	while(it.hasNext()) {
	            TripleString ts = it.next();
	            if(i == next){
	            	predicate = ts.getPredicate().toString();
	            	nextNode = ts.getSubject().toString();
	            	if (newNode.toString().equals(nextNode)){
		            	newNode = ts.getObject();
		            	if(printInfo){
		            		System.out.print("Turning it around: ");
		                	System.out.println(newNode.toString());
		            	}
	            	} else {
		            	newNode = nextNode;
	            	}
	        		randomWalk.add(predicate);
	        		randomWalk.add(newNode.toString());
	        		if(printInfo){System.out.print("\nTrying ");System.out.println(newNode);}
	        		break;
	            }
	        	i++;
	        }    
        }
        if(printInfo){
            for(int j = 0; j < randomWalk.size(); j++){
                System.out.print(randomWalk.get(j));
                System.out.print(" -> ");
            }System.out.println("\n-----\n");	
            System.out.println(randomWalk.size());
        }
        if(singleWrite){
			for (CharSequence step : randomWalk) {
				pw.write(step.toString());
				pw.write(' ');
			}
			pw.write("\n");
        }
        return randomWalk;
	}
	
	//overload
    private static void createRandomWalks(HDT hdt, ArrayList<CharSequence> movieURIs, int nRandomWalks, int maxRandomWalkLength, String fileName) throws Exception {
        createRandomWalks(hdt, movieURIs, movieURIs.size(), nRandomWalks, maxRandomWalkLength, fileName);
    }
    
//    private static ArrayList<ArrayList<CharSequence>> createRandomWalks(HDT hdt, ArrayList<CharSequence> movieURIs, int nMovies, int nRandomWalks, int maxRandomWalkLength, String fileName) throws Exception {
    private static void createRandomWalks(HDT hdt, ArrayList<CharSequence> movieURIs, int nMovies, int nRandomWalks, int maxRandomWalkLength, String fileName) throws Exception {
    	Writer pw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
    	if (nMovies > movieURIs.size()){
    		nMovies = movieURIs.size();
    	}
    	System.out.print("Working on ");
    	System.out.print(nMovies);
    	System.out.println(" movies.");
        int movieCount = 0;
        int shorterThan = 0;
        
//        ArrayList<ArrayList<CharSequence>> allRandomWalks = new ArrayList<ArrayList<CharSequence>>();
        ArrayList<CharSequence> randomWalk;
        for (CharSequence movieTitle : movieURIs)  {
        	movieCount++;
	        System.out.print("Creating a randomwalks starting from: ");
	        System.out.println(movieTitle);
	        for(int i = 0; i < nRandomWalks; i++){
	        	randomWalk = randomWalk(hdt, movieTitle, maxRandomWalkLength, pw);
//	        	allRandomWalks.add(randomWalk);
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
        pw.close();
//        return allRandomWalks;
    }
	
    public static void main(String[] args) throws Exception {
        HDT hdt = HDTManager.loadHDT("../mappingbased-dbpedia.en.2015-10.hdt", null);
        ArrayList<CharSequence> movieURIs = readFile("../mc-movies.pruned.train.tsv");
        //includes random walks for movies in test set.
//        movieURIs.addAll(readFile("../mc-movies.pruned.test.tsv"));
        System.out.print("Found ");
        System.out.print(movieURIs.size());
        System.out.println(" movies.");
        int nRandomWalks = 1000;
        int maxRandomWalkLength = 8;
//        int nMovies = 10;
//        createRandomWalks(hdt, movieURIs, nMovies, nRandomWalks, maxRandomWalkLength, "../smallRandomWalksComplete.txt");
        createRandomWalks(hdt, movieURIs, nRandomWalks, maxRandomWalkLength, "../smallRandomWalks_bi.txt");
//        writeRandomWalksToFile("../smallRandomWalks.txt", allRandomWalks);
    }
}
