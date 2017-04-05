package it.cnr.icar.biograph.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//import java.util.ArrayList;
import java.util.Iterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class _05_HumanMiRNAGenePrediction_S_C_Import {

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
		String dbUrl = "remote://localhost/biograph";
		String fileName = "/Users/xMAnton/biodb/human_predictions_S_C_aug2010.txt";
		String line;
		//ArrayList<String> errorLog = new ArrayList<String>();
		
		int entryCounter = 0;
		
        long startTime = System.currentTimeMillis();

    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());

    	OrientVertexType vint = graph.createVertexType("interaction");
    	vint.createProperty("transcriptId", OType.STRING);
    	vint.createProperty("mirStart", OType.INTEGER);
    	vint.createProperty("mirEnd", OType.INTEGER);
    	vint.createProperty("geneStart", OType.INTEGER);
    	vint.createProperty("geneEnd", OType.INTEGER);
    	vint.createProperty("conservation", OType.DOUBLE);
    	vint.createProperty("energy", OType.DOUBLE);
    	vint.createProperty("database", OType.STRING);
    	
    	vint.createProperty("snpEnergy", OType.DOUBLE);
    	vint.createProperty("basePair", OType.STRING);
    	vint.createProperty("geneAve", OType.DOUBLE);
    	vint.createProperty("mirnaAve", OType.DOUBLE);

    	graph.createKeyIndex("database", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "interaction"));
    	graph.createKeyIndex("transcriptId", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "interaction"));
    	graph.createKeyIndex("mirStart", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "interaction"));
    	graph.createKeyIndex("mirEnd", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "interaction"));
    	graph.createKeyIndex("geneStart", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "interaction"));
    	graph.createKeyIndex("geneEnd", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "interaction"));
    	graph.createKeyIndex("conservation", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "interaction"));
    	graph.createKeyIndex("energy", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "interaction"));

    	graph.createKeyIndex("snpEnergy", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "interaction"));
    	graph.createKeyIndex("basePair", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "interaction"));
    	graph.createKeyIndex("geneAve", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "interaction"));
    	graph.createKeyIndex("mirnaAve", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "interaction"));

    	graph.createEdgeType("interactingGene");
    	graph.createEdgeType("interactingMiRNA");

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
            			"mirSvrScore", mirSvrScore,
            			"database", "miRanda"
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
