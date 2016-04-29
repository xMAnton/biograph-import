package it.cnr.icar.biorient.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class GeneImport {

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
		String fileName = "/Users/ninni/biodb/NCBI_gene/gene_info";
		String line;
		int entryCounter = 0;
		int edgeCounter = 0;
        long startTime = System.currentTimeMillis();

    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());

    	/*
    	try {	    	
	    	graph.dropKeyIndex("gene.nomenclatureAuthoritySymbol", Vertex.class);
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropKeyIndex("gene.symbol", Vertex.class);
		} catch (Exception e) {
		}
		*/
    	try {	    	
	    	graph.dropKeyIndex("gene.geneId", Vertex.class);
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropKeyIndex("gene.type", Vertex.class);
		} catch (Exception e) {
		}
    	try {
	    	graph.dropVertexType("gene");
		} catch (Exception e) {
		}

    	graph.createVertexType("gene");
    	graph.createKeyIndex("geneId", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "gene"));
    	//graph.createKeyIndex("symbol", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "gene"));
    	graph.createKeyIndex("type", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "gene"));
    	//graph.createKeyIndex("nomenclatureAuthoritySymbol", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "gene"));

	    BufferedReader reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\nImporting NCBI gene info entries from " + fileName + " ");
        
        // skip first line
        reader.readLine();
        
        while ((line = reader.readLine()) != null) {
        	String datavalue[] = line.split("\t");
        	
        	String taxId = datavalue[0];
        	if (!taxId.equals("9606"))
        		continue;
        	
        	String geneId = datavalue[1];
        	//String symbol = datavalue[2];
        	String locusTag = datavalue[3];
        	//String synonyms = datavalue[4];
        	//String dbXrefs = datavalue[5];
        	String chromosome = datavalue[6];
        	String mapLocation = datavalue[7];
        	String description = datavalue[8];
        	String geneType = datavalue[9];
        	String nomenclatureAuthoritySymbol = datavalue[10];
        	String nomenclatureAuthorityFullName = datavalue[11];
        	String nomenclatureStatus = datavalue[12];
        	String otherDesignations = datavalue[13];
        	//String modificationDate = datavalue[14];
        	
        	graph.addVertex("class:gene",
        			"geneId", geneId,
        			//"taxId", taxId,
        			//"symbol", symbol,
        			"locusTag", locusTag,
        			//"synonyms", synonyms,
        			//"dbXrefs", dbXrefs,
        			"chromosome", chromosome,
        			"mapLocation", mapLocation,
        			"description", description,
        			"type", geneType,
        			"nomenclatureAuthoritySymbol", nomenclatureAuthoritySymbol,
        			"nomenclatureAuthorityFullName", nomenclatureAuthorityFullName,
        			"nomenclatureStatus", nomenclatureStatus,
        			"otherDesignations", otherDesignations
        			//"modificationDate", modificationDate
        			);
        	entryCounter++;

            if (entryCounter % 10000 == 0) {
            	System.out.print("."); System.out.flush();
            }
        }
        
        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + entryCounter + " vertices and " + edgeCounter + " edges in " + timeConversion(stopTime));

        reader.close();

        graph.declareIntent(null);
        graph.shutdown();
        
        graphFactory.close();
	}
}
