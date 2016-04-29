package it.cnr.icar.biorient.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class GeneGroupImport {

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
		String fileName = "/Users/ninni/biodb/NCBI_gene/gene_group";
		String line;
		int entryCounter = 0;
        long startTime = System.currentTimeMillis();

    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());

    	try {	    	
	    	graph.dropEdgeType("gene_relatedTo");
		} catch (Exception e) {
		}

    	graph.createEdgeType("gene_relatedTo");

    	HashMap<String, Vertex> taxonomy = new HashMap<String, Vertex>();
        System.out.println("\nLoading NCBI gene taxonomy entries for filtering");
        for (Vertex v : graph.getVerticesOfClass("geneTaxonomy")) {
        	String taxId = v.getProperty("taxId");
        	taxonomy.put(taxId, v);
        }

	    BufferedReader reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\nImporting NCBI gene relations from " + fileName + " ");
        
        // skip first line
        reader.readLine();
        
        while ((line = reader.readLine()) != null) {
        	String datavalue[] = line.split("\t");
        	
        	String taxId = datavalue[0];
        	if (taxonomy.get(taxId) == null)
        		continue;

        	String geneId = datavalue[1];
        	String relationship = datavalue[2];

        	String otherTaxId = datavalue[3];
        	if (taxonomy.get(otherTaxId) == null)
        		continue;

        	String otherGeneId = datavalue[4];

        	Vertex srcGene = null;
        	Vertex dstGene = null;

        	Iterator<Vertex> it = graph.getVertices("gene.geneId", geneId).iterator();
        	if (it.hasNext()) {
        		srcGene = it.next();
        		
        		it = graph.getVertices("gene.geneId", otherGeneId).iterator();
        		if (it.hasNext())
        			dstGene = it.next();
        	}
        	
        	if ((srcGene != null) && (dstGene != null)) {
            	entryCounter++;
        		Edge e = graph.addEdge("class:gene_relatedTo", srcGene, dstGene, "gene_relatedTo");
        		e.setProperty("type", relationship);
        	}
        	
            if (entryCounter % 10000 == 0) {
            	System.out.print("."); System.out.flush();
            }
        }
        
        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + entryCounter + " edges in " + timeConversion(stopTime));

        reader.close();

        graph.declareIntent(null);
        graph.shutdown();
        
        graphFactory.close();
	}
}
