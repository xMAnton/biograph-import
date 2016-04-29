package it.cnr.icar.biorient.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class ProteinCodingGeneImport {

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
		String fileName = "/Users/ninni/biodb/protein-coding_gene.txt";
		String line;
		//ArrayList<String> errorLog = new ArrayList<String>();
		
		int entryCounter = 0;
		int edgeCounter = 0;
		
        long startTime = System.currentTimeMillis();

    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());
    	graph.setStandardElementConstraints(false);

    	try {
	    	graph.dropEdgeType("coding");
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropEdgeType("synonymOf");
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropKeyIndex("geneName.symbol", Vertex.class);
		} catch (Exception e) {
		}
    	try {
	    	graph.dropVertexType("geneName");
		} catch (Exception e) {
		}

    	graph.createVertexType("geneName");
    	graph.createKeyIndex("symbol", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "geneName"));
    	graph.createEdgeType("synonymOf");
    	graph.createEdgeType("coding");

	    BufferedReader reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\nImporting proteing coding to gene associations from " + fileName + " ");
        
        // skip first line
        reader.readLine();
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	String hgncId = datavalue[0];
        	/*
        	String name = datavalue[2];
        	String locusGroup = datavalue[3];
        	String locusType = datavalue[4];
        	String status = datavalue[5];
        	String location = datavalue[6];
        	String locationSortable = datavalue[7];
        	String aliasSymbol = datavalue[8];
        	String aliasName = datavalue[9];
        	String prevSymbol = datavalue[10];
        	String prevName = datavalue[11];
        	String geneFamily = datavalue[12];
        	String geneFamilyId = datavalue[13];
        	String dateApprovedReserved = datavalue[14];
        	String dateSymbolChanged = datavalue[15];
        	String dateNameChanged = datavalue[16];
        	String dateModified = datavalue[17];
        	*/
        	String entrezId = datavalue[18];
        	String ensemblGeneId = datavalue[19];
        	/*
        	String vegaId = datavalue[20];
        	String ucscId = datavalue[21];
        	String ena = datavalue[22];
        	 */
        	String refseqAccession = datavalue[23];
        	if (datavalue.length < 25)
        		continue;
        	//String ccdsId = datavalue[24];
        	if (datavalue.length < 26)
        		continue;
        	String uniprotIds = datavalue[25];
        	
        	/*
        	if (datavalue.length < 27)
        		continue;
        	String pubmedId = datavalue[26];
        	if (datavalue.length < 28)
        		continue;
        	String mgdId = datavalue[27];
        	if (datavalue.length < 29)
        		continue;
        	String rgdId = datavalue[28];
        	if (datavalue.length < 30)
        		continue;
        	String lsdb = datavalue[29];
        	if (datavalue.length < 31)
        		continue;
        	String cosmic = datavalue[30];
        	if (datavalue.length < 32)
        		continue;
        	String omimId = datavalue[31];
        	if (datavalue.length < 33)
        		continue;
        	String mirbase = datavalue[32];
        	String homeodb = datavalue[33];
        	if (datavalue.length < 35)
        		continue;
        	String snornabase = datavalue[34];
        	String bioparadigmsSlc = datavalue[35];
        	if (datavalue.length < 37)
        		continue;
        	String orphanet = datavalue[36];
        	if (datavalue.length < 38)
        		continue;
        	String pseudogene = datavalue[37];
        	if (datavalue.length < 39)
        		continue;
        	String hordeId = datavalue[38];
        	if (datavalue.length < 40)
        		continue;
        	String merops = datavalue[39];
        	if (datavalue.length < 41)
        		continue;
        	String imgt = datavalue[40];
        	String iuphar = datavalue[41];
        	if (datavalue.length < 43)
        		continue;
        	String kznfGeneCatalog = datavalue[42];
        	if (datavalue.length < 44)
        		continue;
        	String mamitTrnadb = datavalue[43];
        	String cd = datavalue[44];
        	if (datavalue.length < 46)
        		continue;
        	String lncrnadb = datavalue[45];
        	String enzymeId = datavalue[46];
        	if (datavalue.length < 48)
        		continue;
        	String intermediateFilamentDb = datavalue[47];
        	*/

        	if (!entrezId.equals("")) {
        		Vertex gene = null;
        		
        		Iterator<Vertex> it = graph.getVertices("gene.geneId", entrezId).iterator();
        		if (it.hasNext())
        			gene = it.next();

        		if (gene != null) {            		
        			Vertex ncbi = graph.addVertex("class:geneName",
        					"symbol", entrezId
        					);
        			ncbi.addEdge("synonymOf", gene);
        			entryCounter++;
        			edgeCounter++;
        			
        			String entrezSymbol = gene.getProperty("symbol");
        			Vertex symbol = graph.addVertex("class:geneName",
        					"symbol", entrezSymbol
        					);
        			symbol.addEdge("synonymOf", gene);
        			entryCounter++;
        			edgeCounter++;

        			if ((hgncId != null) && (!hgncId.equals(""))) {
            			Vertex hgnc = graph.addVertex("class:geneName",
            					"symbol", hgncId
            					);
            			hgnc.addEdge("synonymOf", gene);
            			entryCounter++;
            			edgeCounter++;
            		}
        			
            		if ((ensemblGeneId != null) && (!ensemblGeneId.equals(""))) {
            			Vertex ensembl = graph.addVertex("class:geneName",
            					"symbol", ensemblGeneId
            					);
            			ensembl.addEdge("synonymOf", gene);
            			entryCounter++;
            			edgeCounter++;
            		}
            		
            		if ((refseqAccession != null) && (!refseqAccession.equals(""))) {
            			Vertex refseq = graph.addVertex("class:geneName",
            					"symbol", refseqAccession
            					);
            			refseq.addEdge("synonymOf", gene);
            			entryCounter++;
            			edgeCounter++;
            		}
            		
                	if (!uniprotIds.equals("")) {
                		if (uniprotIds.contains("|")) {
                			String uniprotId[] = uniprotIds.split("|");
                			
                    		for (int i=0; i<uniprotId.length; i++) {
                    			it = graph.getVertices("proteinAccession.name", uniprotId[i]).iterator();
                    			if (it.hasNext()) {
                    				Edge e = it.next().getEdges(Direction.OUT, "refersTo").iterator().next();
                    				Vertex protein = e.getVertex(Direction.IN);
                    				
                    				//gene.addEdge("coding", protein);
                    				graph.addEdge("class:coding", gene, protein, "coding");
                    				edgeCounter++;
                    			}
                    		}
                		} else {
                			it = graph.getVertices("proteinAccession.name", uniprotIds).iterator();
                			if (it.hasNext()) {
                				Edge e = it.next().getEdges(Direction.OUT, "refersTo").iterator().next();
                				Vertex protein = e.getVertex(Direction.IN);
                				
                				//gene.addEdge("coding", protein);
                				graph.addEdge("class:coding", gene, protein, "coding");
                				edgeCounter++;
                			}
                		}
                	}

                    if (entryCounter % 1000 == 0) {
                    	System.out.print("."); System.out.flush();
                    }
        		}
        	}
        }
        reader.close();

		fileName = "/Users/ninni/biodb/non-coding_RNA.txt";
	    reader = new BufferedReader(new FileReader(fileName));
		
        System.out.print("\n\nImporting non-coding associations from " + fileName + " ");
        
        // skip first line
        reader.readLine();
        
        while ((line = reader.readLine()) != null) {        	
    		Vertex gene = null;

        	String datavalue[] = line.split("\t");
        	
        	String hgncId = datavalue[0];
        	String symbol = datavalue[1];
        	
    		if ((symbol != null) && (!symbol.equals(""))) {        		
        		Iterator<Vertex> it = graph.getVertices("gene.geneId", symbol).iterator();
        		if (it.hasNext())
        			gene = it.next();
        	}
    		if (gene == null)
    			continue;
    		
			Vertex ncbi = graph.addVertex("class:geneName",
					"symbol", symbol
					);
			ncbi.addEdge("synonymOf", gene);
			entryCounter++;
			edgeCounter++;

    		if (!hgncId.equals("")) {
    			Vertex hgnc = graph.addVertex("class:geneName",
    					"symbol", hgncId
    					);
    			hgnc.addEdge("synonymOf", gene);
    			entryCounter++;
    			edgeCounter++;
    		}

        	/*
    		String name = datavalue[2];
        	String locusGroup = datavalue[3];
        	String locusType = datavalue[4];
        	String status = datavalue[5];
        	String location = datavalue[6];
        	String locationSortable = datavalue[7];
        	String aliasSymbol = datavalue[8];
        	String aliasName = datavalue[9];
        	String prevSymbol = datavalue[10];
        	String prevName = datavalue[11];
        	String geneFamily = datavalue[12];
        	String geneFamilyId = datavalue[13];
        	String dateApprovedReserved = datavalue[14];
        	String dateSymbolChanged = datavalue[15];
        	String dateNameChanged = datavalue[16];
        	String dateModifiedEntrezId = datavalue[17];
        	*/

        	if (datavalue.length < 19)
        		continue;
        	String ensemblGeneId = datavalue[18];
    		if (!ensemblGeneId.equals("")) {
    			Vertex ensembl = graph.addVertex("class:geneName",
    					"symbol", ensemblGeneId
    					);
    			ensembl.addEdge("synonymOf", gene);
    			entryCounter++;
    			edgeCounter++;
    		}

        	if (datavalue.length < 20)
        		continue;
        	//String vegaId = datavalue[19];
        	//String ucscId = datavalue[20];
        	if (datavalue.length < 22)
        		continue;
        	//String ena = datavalue[21];
        	
        	if (datavalue.length < 23)
        		continue;
        	String refseqAccession = datavalue[22];
    		if (!refseqAccession.equals("")) {
    			Vertex refseq = graph.addVertex("class:geneName",
    					"symbol", refseqAccession
    					);
    			refseq.addEdge("synonymOf", gene);
    			entryCounter++;
    			edgeCounter++;
    		}

        	/*
        	if (datavalue.length < 24)
        		continue;
        	//String ccdsId = datavalue[23];
        	
        	if (datavalue.length < 25)
        		continue;
        	
        	String uniprotIds = datavalue[24];
        	if (!uniprotIds.equals(""))
        		System.out.println(uniprotIds);
        	String pubmedId = datavalue[25];
        	if (datavalue.length < 27)
        		continue;
        	String mgdId = datavalue[26];
        	if (datavalue.length < 28)
        		continue;
        	String rgdId = datavalue[27];
        	if (datavalue.length < 29)
        		continue;
        	String lsdbcosmic = datavalue[28];
        	if (datavalue.length < 30)
        		continue;
        	String omimId = datavalue[29];
        	if (datavalue.length < 31)
        		continue;
        	String mirbase = datavalue[30];
        	if (datavalue.length < 32)
        		continue;
        	String homeodb = datavalue[31];
        	if (datavalue.length < 33)
        		continue;
        	String snornabase = datavalue[32];
        	if (datavalue.length < 34)
        		continue;
        	String bioparadigmsSlc = datavalue[33];
        	String orphanet = datavalue[34];
        	if (datavalue.length < 36)
        		continue;
        	String pseudogeneOrg = datavalue[35];
        	String hordeId = datavalue[36];
        	if (datavalue.length < 38)
        		continue;
        	String merops = datavalue[37];
        	if (datavalue.length < 39)
        		continue;
        	String imgt = datavalue[38];
        	String iuphar = datavalue[39];
        	String kznfGeneCatalog = datavalue[40];
        	String mamitTrnadb = datavalue[41];
        	String cd = datavalue[42];
        	String lncrnadb = datavalue[43];
        	if (datavalue.length < 45)
        		continue;
        	String enzymeId = datavalue[44];
        	String intermediateFilamentDb = datavalue[45];
        	*/   	
        }
       
        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + entryCounter + " vertices and " + edgeCounter + " edges in " + timeConversion(stopTime));

        reader.close();

        graph.declareIntent(null);
        graph.shutdown();
        
        graphFactory.close();
	}
}
