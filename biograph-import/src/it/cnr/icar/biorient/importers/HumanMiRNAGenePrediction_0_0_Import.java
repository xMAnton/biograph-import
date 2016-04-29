package it.cnr.icar.biorient.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//import java.util.ArrayList;
import java.util.Iterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class HumanMiRNAGenePrediction_0_0_Import {

	private static String timeConversion(long seconds) {

	    final int MINUTES_IN_AN_HOUR = 60;
	    final int SECONDS_IN_A_MINUTE = 60;

	    long minutes = seconds / SECONDS_IN_A_MINUTE;
	    seconds -= minutes * SECONDS_IN_A_MINUTE;

	    long hours = minutes / MINUTES_IN_AN_HOUR;
	    minutes -= hours * MINUTES_IN_AN_HOUR;

	    return hours + " hours " + minutes + " minutes " + seconds + " seconds";
	}

	public static void main(String[] args) throws IOException {
		String dbUrl = "remote://localhost/biorient";
		String fileName = "/Users/ninni/biodb/human_predictions_0_0_aug2010.txt";
		String line;
		//ArrayList<String> errorLog = new ArrayList<String>();
		
		int entryCounter = 0;
		
        long startTime = System.currentTimeMillis();

    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());

	    BufferedReader reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\nImporting human MiRNA-genes interactions from " + fileName + " ");
        
        // skip first line
        reader.readLine();
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	String mirAccession = datavalue[0];
        	//String mirName = datavalue[1];
        	String geneId = datavalue[2];
        	//String geneSymbol = datavalue[3];
        	String transcriptId = datavalue[4];
        	String extTranscriptId = datavalue[5];
        	String mirAlignment = datavalue[6];
        	String alignment = datavalue[7];
        	String geneAlignment = datavalue[8];
        	int mirStart = Integer.valueOf(datavalue[9]);
        	int mirEnd = Integer.valueOf(datavalue[10]);
        	int geneStart = Integer.valueOf(datavalue[11]);
        	int geneEnd = Integer.valueOf(datavalue[12]);
        	String genomeCoordinates = datavalue[13];
        	double conservation = Double.valueOf(datavalue[14]);
        	int alignScore = Integer.valueOf(datavalue[15]);
        	int seedCat = Integer.valueOf(datavalue[16]);
        	double energy = Double.valueOf(datavalue[17]);
        	double mirSvrScore = Double.valueOf(datavalue[18]);

        	Vertex miRNA = null;
        	Vertex gene = null;

        	Iterator<Vertex> it = graph.getVertices("miRNAmature.accession", mirAccession).iterator();
        	if (it.hasNext()) {
        		miRNA = it.next();
        		
        		it = graph.getVertices("gene.geneId", geneId).iterator();
        		if (it.hasNext())
        			gene = it.next();
        	}
        	
        	if ((miRNA != null) && (gene != null)) {
            	entryCounter++;
            	Vertex interaction = graph.addVertex("class:interaction",
            			"transcriptId", transcriptId,
            			"extTranscriptId", extTranscriptId,
            			"mirAlignment", mirAlignment,
            			"alignment", alignment,
            			"geneAlignment", geneAlignment,
            			"mirStart", mirStart,
            			"mirEnd", mirEnd,
            			"geneStart", geneStart,
            			"geneEnd", geneEnd,
            			"genomeCoordinates", genomeCoordinates,
            			"conservation", conservation,
            			"alignScore", alignScore,
            			"seedCat", seedCat,
            			"energy", energy,
            			"mirSvrScore", mirSvrScore
            			);
            	interaction.addEdge("interactingMiRNA", miRNA);
            	interaction.addEdge("interactingGene", gene);

                if (entryCounter % 25000 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	} else {
        		//errorLog.add("NOT FOUND: " + mirAccession + " [line: " + entryCounter + "] " + gene );
        	}
        }
        
        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + entryCounter + " vertices and " + (entryCounter*2) + " edges in " + timeConversion(stopTime));

        //for(String error : errorLog)
        	//System.err.println(error);
        
        reader.close();

        graph.declareIntent(null);
        graph.shutdown();
        
        graphFactory.close();
	}
}
