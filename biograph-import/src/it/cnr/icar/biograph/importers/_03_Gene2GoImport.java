package it.cnr.icar.biograph.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class _03_Gene2GoImport {

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
		String fileName = "/Users/xMAnton/biodb/gene2go";
		String line;
		ArrayList<String> errorLog = new ArrayList<String>();
		
		int entryCounter = 0;
		int edgeCounter = 0;
		
        long startTime = System.currentTimeMillis();

    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());
    	graph.setStandardElementConstraints(false);

    	graph.createEdgeType("annotates");

	    BufferedReader reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\nImporting gene-go associations from " + fileName + " ");
        
        // skip first line
        reader.readLine();
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	String taxId = datavalue[0];
        	if (!taxId.equals("9606"))
        		continue;

        	String geneId = datavalue[1];
        	String goId = datavalue[2];
        	String evidence = datavalue[3];
        	String qualifier = datavalue[4];
        	//String goTerm = datavalue[5];
        	//String pubmedIds = datavalue[6];
        	String category = datavalue[7];

        	OrientVertex gene = null;
        	OrientVertex go = null;

        	Iterator<Vertex> it = graph.getVertices("gene.geneId", geneId).iterator();
        	if (it.hasNext()) {
        		gene = (OrientVertex) it.next();
        		
        		it = graph.getVertices("go.goId", goId).iterator();
        		if (it.hasNext())
        			go = (OrientVertex) it.next();
        	}
        	
        	if ((gene != null) && (go != null)) {
            	edgeCounter++;
            	
            	//Edge association = graph.addEdge("class:gene2go", gene, go, "gene2go");
            	/*
            	Edge association = gene.addEdge("gene2go", go);
            	association.setProperty("evidence", evidence);
            	association.setProperty("qualifier", qualifier);
            	//association.setProperty("pubmed", pubmed);
            	association.setProperty("category", category);
            	*/
            	
            	OrientEdge association = graph.addEdge("class:annotates", go, gene, "annotates");
            	association.setProperties(
            			"evidence", evidence,
            			"qualifier", qualifier,
            			"category", category);

        	} else {
        		errorLog.add("NOT FOUND: " + geneId + " [line: " + entryCounter + "] " + goId );
        	}
        	
            if (edgeCounter % 5000 == 0) {
            	System.out.print("."); System.out.flush();
            }
        }
        
        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + entryCounter + " vertices and " + edgeCounter + " edges in " + timeConversion(stopTime));

        //for(String error : errorLog)
        	//System.err.println(error);
        
        reader.close();

        graph.declareIntent(null);
        graph.shutdown();
        
        graphFactory.close();
	}
}
