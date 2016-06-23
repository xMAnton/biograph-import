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

public class _16_miRNASNPImport {

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
		String fileName = "/Users/xMAnton/biodb/miRNASNP/snp_in_human_miRNA_seed_region.txt";
		String line;
		
		int entryCounter = 0;
		
        long startTime = System.currentTimeMillis();

    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());

    	OrientVertexType msnp = graph.createVertexType("miRNASNP");
    	msnp.createProperty("miRNA", OType.STRING);
    	msnp.createProperty("SNPid", OType.STRING);
    	msnp.createProperty("chr", OType.STRING);
    	msnp.createProperty("miRstart", OType.INTEGER);
    	msnp.createProperty("miRend", OType.INTEGER);
    	msnp.createProperty("lostNum", OType.INTEGER);
    	msnp.createProperty("gainNum", OType.INTEGER);

    	graph.createKeyIndex("SNPid", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "miRNASNP"));
    	graph.createKeyIndex("miRNA", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "miRNASNP"));

    	graph.createEdgeType("hasSNP");

	    BufferedReader reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\nImporting SNPs in human miRNA seed region from " + fileName + " ");
        
        // skip first line
        reader.readLine();
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	String mirna = datavalue[0];
        	String chr = datavalue[1];
        	int miRstart = Integer.valueOf(datavalue[2]);
        	int miRend = Integer.valueOf(datavalue[3]);
        	String SNPid = datavalue[4];
        	int lostNum = Integer.valueOf(datavalue[5]);
        	int gainNum = Integer.valueOf(datavalue[6]);

        	Vertex miRNA = null;

        	Iterator<Vertex> it = graph.getVertices("miRNAmature.product", mirna).iterator();
        	if (it.hasNext()) {
        		miRNA = it.next();
        		
        		Vertex snp = graph.addVertex("class:miRNASNP",
        				"SNPid", SNPid,
        				"miRNA", mirna,
        				"chr", chr,
        				"miRstart", miRstart,
        				"miRend", miRend,
        				"lostNum", lostNum,
        				"gainNum", gainNum
        				);
        		miRNA.addEdge("hasSNP", snp);
        		entryCounter++;
        	}
        }
        
        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + entryCounter + " vertices and " + entryCounter + " edges in " + timeConversion(stopTime));
       
        reader.close();

        graph.declareIntent(null);
        graph.shutdown();
        
        graphFactory.close();
	}
}
