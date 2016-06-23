package it.cnr.icar.biograph.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class _15_UniprotIdMappingImport {

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
		String fileName = "/Users/xMAnton/biodb/uniprot/HUMAN_9606_idmapping_selected.tab";
		String line;
		
		int entryCounter = 0;
		int edgeCounter = 0;
		
        long startTime = System.currentTimeMillis();

    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());

	    BufferedReader reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\nImporting proteins id mappings from " + fileName + " ");
        
        // skip first line
        reader.readLine();
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	//String uniprotAcc = datavalue[0];
        	String uniprotID = datavalue[1];
        	String geneId = datavalue[2];
        	String refseq = datavalue[3];
        	/*
        	String gi = datavalue[4];
        	String pdb = datavalue[5];
        	*/
        	String go = datavalue[6];
        	/*
        	String uniref100 = datavalue[7];
        	String uniref90 = datavalue[8];
        	String uniref50 = datavalue[9];
        	String uniparc = datavalue[10];
        	String pir = datavalue[11];
        	String ncbiTax = datavalue[12];
        	*/
        	if (datavalue.length < 14)
        		continue;
        	//String mim = datavalue[13];
        	String unigeneId = datavalue[14];
        	//String pubmed = datavalue[15];
        	if (datavalue.length < 17)
        		continue;
        	//String embl = datavalue[16];
        	//String embl_cds = datavalue[17];
        	if (datavalue.length < 19)
        		continue;
        	//String ensembl = datavalue[18];
        	//String ensembl_trs = datavalue[19];
        	String ensembl_pro = datavalue[20];
        	//String addPubmed = datavalue[21];

        	if (!uniprotID.equals("")) {
        		Vertex protein = null;
        		
        		Iterator<Vertex> it = graph.getVertices("protein.name", uniprotID).iterator();
        		if (it.hasNext())
        			protein = it.next();
        		
        		if (protein != null) {
        			if (!refseq.equals("")) {
        				if (refseq.contains(";")) {
            				String refseqId[] = refseq.split("; ");
            				for (int i=0; i<refseqId.length; i++) {
            					Vertex vref = graph.addVertex("class:proteinName", "name", refseqId[i]);
            					vref.addEdge("refersTo", protein);
    	            			entryCounter++;
    	            			edgeCounter++;
            				}        					
        				} else {
        					Vertex vref = graph.addVertex("class:proteinName", "name", refseq);
        					vref.addEdge("refersTo", protein);
        					entryCounter++;
        					edgeCounter++;
        				}
        			}
        			
        			if (!ensembl_pro.equals("")) {
        				if (ensembl_pro.contains(";")) {
            				String ensembleId[] = ensembl_pro.split("; ");
            				for (int i=0; i<ensembleId.length; i++) {
            					Vertex ensembl = graph.addVertex("class:proteinName", "name", ensembleId[i]);
            					ensembl.addEdge("refersTo", protein);
    	            			entryCounter++;
    	            			edgeCounter++;
            				}        					
        				} else {
        					Vertex ensembl = graph.addVertex("class:proteinName", "name", ensembl_pro);
        					ensembl.addEdge("refersTo", protein);
        					entryCounter++;
        					edgeCounter++;
        				}
        			}
        			
        			if (!geneId.equals("")) {
        				Vertex gene = null;
                		
                		Iterator<Vertex> git = graph.getVertices("gene.geneId", geneId).iterator();
                		if (git.hasNext())
                			gene = git.next();
                		
                		if (gene != null) {
                			if (!gene.getEdges(Direction.OUT, "coding").iterator().hasNext()) {
                				graph.addEdge("class:coding", gene, protein, "coding");
                				edgeCounter++;
                			}
                			
                			if (!unigeneId.equals("")) {
                				Vertex unigene = graph.addVertex("class:geneName", "symbol", unigeneId);
                				unigene.addEdge("synonymOf", gene);
                				entryCounter++;
        	        			edgeCounter++;
                			}
                		}
        			}
        			
        			if (!go.equals("")) {
        				if (go.contains(";")) {
            				String goId[] = go.split("; ");
            				for (int i=0; i<goId.length; i++) {
            					Vertex g = null;
            		        	it = graph.getVertices("go.goId", goId[i]).iterator();
            		        	if (it.hasNext())
            		        		g = it.next();
            		        	if (g != null) {
            		        		g.addEdge("annotates", protein);
            		        		edgeCounter++;
            		        	}
            				}               					
        				} else {
        					Vertex g = null;
        		        	it = graph.getVertices("go.goId", go).iterator();
        		        	if (it.hasNext())
        		        		g = it.next();
        		        	if (g != null) {
        		        		g.addEdge("annotates", protein);
        		        		edgeCounter++;
        		        	}
        				}
        			}
        		}
        		
                if (entryCounter % 1000 == 0) {
                	System.out.print("."); System.out.flush();
                }
            }
        }
        reader.close();

        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + entryCounter + " vertices and " + edgeCounter + " edges in " + timeConversion(stopTime));

        reader.close();

        graph.declareIntent(null);
        graph.shutdown();
        
        graphFactory.close();
	}
}
