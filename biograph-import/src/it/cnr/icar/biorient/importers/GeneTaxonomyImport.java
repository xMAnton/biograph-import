package it.cnr.icar.biorient.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class GeneTaxonomyImport {

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
		String line;
		String fileName;
		BufferedReader reader;
		
		int entryCounter = 0;
		int edgeCounter = 0;
        long startTime = System.currentTimeMillis();

    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());
    	graph.setStandardElementConstraints(false);

    	/*
    	try {	    	
	    	graph.dropVertexType("geneTaxonomy_division");
		} catch (Exception e) {
		}
    	graph.createVertexType("geneTaxonomy_division");

		String fileName = "/Users/ninni/biodb/NCBI_taxonomy/division.dmp";
	    BufferedReader reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\nReading GenBank divisions from " + fileName + " ");
        
        HashMap<String,Vertex> divisions = new HashMap<String,Vertex>();
        
        while ((line = reader.readLine()) != null) {
        	line = line.replace("\t", "");
        	line = line.replace("|", "\t");
        	String datavalue[] = line.split("\t");
        	
        	String divisionId = datavalue[0];
        	String divisionCode = datavalue[1];
        	String divisionName = datavalue[2];
        	String comment = (datavalue.length < 4) ? "" : datavalue[3];

        	entryCounter++;
        	
        	Vertex division = graph.addVertex("class:geneTaxonomy_division",
        			"code", divisionCode,
        			"name", divisionName,
        			"comment", comment
        			);
        	
        	divisions.put(divisionId, division);

        	System.out.print("."); System.out.flush();
        }
        reader.close();
        */
        
    	/*
    	try {	    	
	    	graph.dropVertexType("geneTaxonomy_geneticCode");
		} catch (Exception e) {
		}
    	graph.createVertexType("geneTaxonomy_geneticCode");

    	fileName = "/Users/ninni/biodb/NCBI_taxonomy/gencode.dmp";
	    reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\n\nReading GenBank genetic codes from " + fileName + " ");
        
        HashMap<String,Vertex> geneticCodes = new HashMap<String,Vertex>();

        while ((line = reader.readLine()) != null) {        	
        	line = line.replace("\t", "");
        	line = line.replace("|", "\t");
        	String datavalue[] = line.split("\t");
        	
        	String codeId = datavalue[0];
        	//String abbrevation = datavalue[2];
        	String name = datavalue[2];
        	String cde = datavalue[3];
        	String starts = datavalue[4];

        	entryCounter++;
        	
        	Vertex geneticCode = graph.addVertex("class:geneTaxonomy_geneticCode",
        			//"abbrevation", abbrevation,
        			"name", name,
        			"cde", cde,
        			"starts", starts
        			);
        	
        	geneticCodes.put(codeId, geneticCode);

        	System.out.print("."); System.out.flush();
        }
        reader.close();
        */
        

    	try {	    	
	    	graph.dropEdgeType("geneTaxonomy_named");
		} catch (Exception e) {
		}
    	/*
    	try {	    	
	    	graph.dropEdgeType("geneTaxonomy_hasDivision");
		} catch (Exception e) {
		}
		*/
    	try {	    	
	    	graph.dropEdgeType("geneTaxonomy_childOf");
		} catch (Exception e) {
		}
    	/*
    	try {	    	
	    	graph.dropEdgeType("geneTaxonomy_hasGeneticCode");
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropEdgeType("geneTaxonomy_hasMitochondrialGeneticCode");
		} catch (Exception e) {
		}
		*/
    	try {	    	
	    	graph.dropKeyIndex("geneTaxonomy.taxId", Vertex.class);
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropKeyIndex("geneTaxonomy.rank", Vertex.class);
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropVertexType("geneTaxonomy");
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropVertexType("geneTaxonomyName");
		} catch (Exception e) {
		}
    	graph.createVertexType("geneTaxonomy");
    	graph.createVertexType("geneTaxonomyName");
    	graph.createKeyIndex("taxId", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "geneTaxonomy"));
    	graph.createKeyIndex("rank", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "geneTaxonomy"));
    	graph.createEdgeType("geneTaxonomy_childOf");
    	graph.createEdgeType("geneTaxonomy_named");
    	//graph.createEdgeType("geneTaxonomy_hasDivision");
    	//graph.createEdgeType("geneTaxonomy_hasGeneticCode");
    	//graph.createEdgeType("geneTaxonomy_hasMitochondrialGeneticCode");

    	fileName = "/Users/ninni/biodb/NCBI_taxonomy/nodes.dmp";
	    reader = new BufferedReader(new FileReader(fileName));

        HashMap<String,Vertex> nodes = new HashMap<String,Vertex>();
        HashMap<String,String> childOf = new HashMap<String,String>();
        /*
        HashMap<String,Vertex> node2division = new HashMap<String,Vertex>();
        HashMap<String,String> node2parentDivision = new HashMap<String,String>();
        HashMap<String,Vertex> node2geneticCode = new HashMap<String,Vertex>();
        HashMap<String,String> node2parentGeneticCode = new HashMap<String,String>();
        HashMap<String,Vertex> node2mitochondrialGeneticCode = new HashMap<String,Vertex>();
        HashMap<String,String> node2parentMitochondrialGeneticCode = new HashMap<String,String>();
        */

        System.out.print("\n\nImporting NCBI taxonomy from " + fileName + " ");
        
        while ((line = reader.readLine()) != null) {        	
        	line = line.replace("\t", "");
        	line = line.replace("|", "\t");
        	String datavalue[] = line.split("\t");
        	
        	String taxId = datavalue[0];
        	String parentId = datavalue[1];
        	String rank = datavalue[2];
        	String emblCode = datavalue[3];
        	String divisionId = datavalue[4];
        	
        	if (!divisionId.equals("5"))
        		continue;
        	
        	/*
        	String divisionFlag = datavalue[5];
        	String geneticCodeId = datavalue[6];
        	String geneticCodeFlag = datavalue[7];
        	String mitochondrialGeneticCodeId = datavalue[8];
        	String mitochondrialGeneticCodeFlag = datavalue[9];
        	*/
        	String hiddenFlag = datavalue[10];
        	String hiddenSubtreeRootFlag = datavalue[11];
        	String comments = (datavalue.length < 13) ? "" : datavalue[12];

        	entryCounter++;
        	
        	Vertex taxonomy = graph.addVertex("class:geneTaxonomy",
        			"taxId", taxId,
        			"parentId", parentId,
        			"rank", rank,
        			"emblCode", emblCode,
        			//"divisionFlag", divisionFlag,
        			//"geneticCodeFlag", geneticCodeFlag,
        			//"mitochondrialGeneticCodeFlag", mitochondrialGeneticCodeFlag,
        			"hiddenFlag", hiddenFlag,
        			"hiddenSubtreeRootFlag", hiddenSubtreeRootFlag,
        			"comments", comments
        			);
        	
        	nodes.put(taxId, taxonomy);
        	
        	if (!parentId.equals("0"))
        		childOf.put(taxId, parentId);
        	
        	/*
        	if (divisionFlag.equals("0"))
        		node2division.put(taxId, divisions.get(divisionId));
        	else
        		node2parentDivision.put(taxId, parentId);
        	
        	if (geneticCodeFlag.equals("0"))
        		node2geneticCode.put(taxId, geneticCodes.get(geneticCodeId));
        	else
        		node2parentGeneticCode.put(taxId, parentId);
        	
        	if (mitochondrialGeneticCodeFlag.equals("0"))
        		node2mitochondrialGeneticCode.put(taxId, geneticCodes.get(mitochondrialGeneticCodeId));
        	else
        		node2parentMitochondrialGeneticCode.put(taxId, parentId);
        	*/
        	
            if (entryCounter % 100 == 0) {
            	System.out.print("."); System.out.flush();
            }
        }
        reader.close();

        System.out.print("\n\nCreating parent-child relations ");
        for(String childId : childOf.keySet()) {
        	Vertex src = nodes.get(childId);
        	Vertex dst = nodes.get(childOf.get(childId));
        	
        	if ((src != null) && (dst != null)) {
            	graph.addEdge("class:geneTaxonomy_childOf", src, dst, "geneTaxonomy_childOf");

            	edgeCounter++;
                if (edgeCounter % 100 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }

    	fileName = "/Users/ninni/biodb/NCBI_taxonomy/names.dmp";
	    reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\n\nImporting names from " + fileName + " ");
        
        while ((line = reader.readLine()) != null) {        	
        	line = line.replace("\t", "");
        	line = line.replace("|", "\t");
        	String datavalue[] = line.split("\t");

        	String taxId = datavalue[0];       	
        	if (nodes.get(taxId) == null)
        		continue;
        	
        	String name = datavalue[1];
        	String uniqueName = datavalue[2];
        	String nameClass = datavalue[3];
        	
        	entryCounter++;
        	Vertex taxName = graph.addVertex("class:geneTaxonomyName",
        			"name", name,
        			"uniqueName", uniqueName,
        			"nameClass", nameClass
        			);
        	edgeCounter++;
			graph.addEdge("class:geneTaxonomy_named", nodes.get(taxId), taxName, "geneTaxonomy_named");

			if (entryCounter % 100 == 0) {
            	System.out.print("."); System.out.flush();
            }
        }
        reader.close();
	    

        /*
        System.out.print("\n\nCreating node-division relations ");
        for(String nodeId : node2division.keySet()) {
        	Vertex src = nodes.get(nodeId);
        	Vertex dst = node2division.get(nodeId);
        	
        	if ((src != null) && (dst != null)) {
            	graph.addEdge("class:geneTaxonomy_hasDivision", src, dst, "geneTaxonomy_hasDivision");

            	edgeCounter++;
                if (edgeCounter % 5000 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        for(String nodeId : node2parentDivision.keySet()) {
        	Vertex src = nodes.get(nodeId);
        	Vertex dst = node2division.get(node2parentDivision.get(nodeId));
        	
        	if ((src != null) && (dst != null)) {
            	graph.addEdge("class:geneTaxonomy_hasDivision", src, dst, "geneTaxonomy_hasDivision");

            	edgeCounter++;
                if (edgeCounter % 5000 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        */

        /*
        System.out.print("\n\nCreating genetic code relations ");
        for(String nodeId : node2geneticCode.keySet()) {
        	Vertex src = nodes.get(nodeId);
        	Vertex dst = node2geneticCode.get(nodeId);
        	
        	if ((src != null) && (dst != null)) {
            	graph.addEdge("class:geneTaxonomy_hasGeneticCode", src, dst, "geneTaxonomy_hasGeneticCode");

            	edgeCounter++;
                if (edgeCounter % 5000 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        for(String nodeId : node2parentGeneticCode.keySet()) {
        	Vertex src = nodes.get(nodeId);
        	Vertex dst = node2geneticCode.get(node2parentGeneticCode.get(nodeId));
        	
        	if ((src != null) && (dst != null)) {
            	graph.addEdge("class:geneTaxonomy_hasGeneticCode", src, dst, "geneTaxonomy_hasGeneticCode");

            	edgeCounter++;
                if (edgeCounter % 5000 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }

        System.out.print("\n\nCreating mitochondrial genetic code relations ");
        for(String nodeId : node2mitochondrialGeneticCode.keySet()) {
        	Vertex src = nodes.get(nodeId);
        	Vertex dst = node2mitochondrialGeneticCode.get(nodeId);
        	
        	if ((src != null) && (dst != null)) {
            	graph.addEdge("class:geneTaxonomy_hasMitochondrialGeneticCode", src, dst, "geneTaxonomy_hasMitochondrialGeneticCode");

            	edgeCounter++;
                if (edgeCounter % 5000 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        for(String nodeId : node2parentMitochondrialGeneticCode.keySet()) {
        	Vertex src = nodes.get(nodeId);
        	Vertex dst = node2mitochondrialGeneticCode.get(node2parentMitochondrialGeneticCode.get(nodeId));
        	
        	if ((src != null) && (dst != null)) {
            	graph.addEdge("class:geneTaxonomy_hasMitochondrialGeneticCode", src, dst, "geneTaxonomy_hasMitochondrialGeneticCode");

            	edgeCounter++;
                if (edgeCounter % 5000 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        */

    	fileName = "/Users/ninni/biodb/NCBI_taxonomy/citations.dmp";
	    reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\n\nImporting citations from " + fileName + " ");
        
        while ((line = reader.readLine()) != null) {        	
        	line = line.replace("\t", "");
        	line = line.replace("|", "\t");
        	String datavalue[] = line.split("\t");
       	
        	//String citationId = datavalue[0];
        	//String citationKey = (datavalue.length < 2) ? "" : datavalue[1];
        	String pubmedId = (datavalue.length < 3) ? "0" : datavalue[2];
        	String medlineId = (datavalue.length < 4) ? "0" : datavalue[3];
        	//String url = (datavalue.length < 5) ? "" : datavalue[4];
        	//String text = (datavalue.length < 6) ? "" : datavalue[5];
        	String taxIdList = (datavalue.length < 7) ? "" : datavalue[6];

        	Vertex citation = null;
        	if (!pubmedId.equals("0")) {
            	Iterator<Vertex> it = graph.getVertices("pubmed.id", pubmedId).iterator();
        		citation = it.hasNext() ? it.next() : graph.addVertex("class:pubmed", "id", pubmedId);
        	} else
        	if (!medlineId.equals("0")) {
            	Iterator<Vertex> it = graph.getVertices("medline.id", medlineId).iterator();
        		citation = it.hasNext() ? it.next() : graph.addVertex("class:medline", "id", medlineId);
        	}
        	
        	if (citation == null)
        		continue;
        	
            //System.out.print("\n citationId="+citationId+" pubmedId="+pubmedId+" medlineId="+medlineId+" taxIdList="+taxIdList);

        	entryCounter++;
        	
        	String taxIds[] = taxIdList.split(" ");
        	for (int i=0; i<taxIds.length; i++) {
        		String taxId = taxIds[i];
        		
        		Vertex taxonomy = nodes.get(taxId);
        		if (taxonomy != null) {
            		taxonomy.addEdge("citedIn", citation);
            		edgeCounter++;
        		}
        	}

            if (entryCounter % 1000 == 0) {
            	System.out.print("."); System.out.flush();
            }
        }
        reader.close();

        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + entryCounter + " vertices and " + edgeCounter + " edges in " + timeConversion(stopTime));

        graph.declareIntent(null);
        graph.shutdown();
        
        graphFactory.close();
	}
}
