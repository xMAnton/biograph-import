package it.cnr.icar.biograph.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//import java.util.ArrayList;
import java.util.Iterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class _18_miRTarBaseHSAImport {

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
		String fileName = "/Users/xMAnton/biodb/hsa_MTI.txt";
		String line;
		
		int entryCounter = 0;
		
        long startTime = System.currentTimeMillis();

    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());

    	OrientVertexType vint = graph.getVertexType("interaction");
    	vint.createProperty("experiments", OType.STRING);
    	vint.createProperty("supportType", OType.STRING);

	    BufferedReader reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\nImporting miRTarBase interactions from " + fileName + " ");
        
        // skip first line
        reader.readLine();
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	//String mirTarBaseId = datavalue[0];
        	String mirna = datavalue[1];
        	//String speciesTarget = datavalue[2];
        	String targetGene = datavalue[3];
        	//String geneId = datavalue[4];
         	//String speciesTargetGene = datavalue[5];
        	String experiments = datavalue[6];
        	String supportType = datavalue[7];
        	String pmid = datavalue[8];

        	Vertex miRNA = null;
        	Vertex gene = null;
        	Vertex pubmed = null;

        	Iterator<Vertex> it = graph.getVertices("miRNAmature.product", mirna).iterator();
        	
        	if (it.hasNext()) {
        		miRNA = it.next();
        		
        		it = graph.getVertices("gene.nomenclatureAuthoritySymbol", targetGene).iterator();
        		if (it.hasNext())
        			gene = it.next();
        	}
        	
        	if ((miRNA != null) && (gene != null)) {
            	entryCounter++;
        		
        		Vertex interaction = graph.addVertex("class:interaction",
        				"experiments", experiments,
        				"supportType", supportType,
        				"database", "miRTarBase"
        				);
        		
            	interaction.addEdge("interactingMiRNA", miRNA);
            	interaction.addEdge("interactingGene", gene);

            	it = graph.getVertices("pubmed.pubmedId", pmid).iterator();
            	if (it.hasNext()) {
            		pubmed = it.next();
            	} else {
            		pubmed = graph.addVertex("class:pubmed",
            			"pubmedId", pmid
            			);
            	}
            	
            	interaction.addEdge("citedIn", pubmed);
            	
                if (entryCounter % 25000 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        
        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + entryCounter + " vertices and " + (entryCounter*3) + " edges in " + timeConversion(stopTime));
       
        reader.close();

        graph.declareIntent(null);
        graph.shutdown();
        
        graphFactory.close();
	}
}
