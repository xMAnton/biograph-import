package it.cnr.icar.biograph.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class _09_ReactomeImport {

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
		
    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();

    	graph.declareIntent(new OIntentMassiveInsert());

    	OrientVertexType vpath = graph.createVertexType("pathway");
    	vpath.createProperty("pathwayId", OType.STRING);
    	
    	graph.createKeyIndex("pathwayId", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "pathway"));
    	
    	graph.createEdgeType("pathway2go");
    	graph.createEdgeType("miRNA2pathway");

        int entryCounter = 0;
        int edgeCounter = 0;
        long startTime = System.currentTimeMillis();

		String fileName = "/Users/xMAnton/biodb/reactome/pathways.txt";
		System.out.println("\nReading homo sapiens Reactome entries from " + fileName);

        BufferedReader reader;
        String line;
        
        reader = new BufferedReader(new FileReader(fileName));
        
        HashMap<String, String> pathwayName = new HashMap<String, String>();
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	String id = datavalue[0];
        	String name = datavalue[1];
        	
        	pathwayName.put(id, name);
        }
        reader.close();

		fileName = "/Users/xMAnton/biodb/reactome/pathwayDisease.txt";
        System.out.println("\nReading pathways diseases from " + fileName);
        HashMap<String, String> pathwayDisease = new HashMap<String, String>();

        reader = new BufferedReader(new FileReader(fileName));
        line = reader.readLine(); //skip header line
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	if (datavalue.length <= 1)
        		continue;
        	
        	String id = datavalue[0];
        	String disease = datavalue[1];
        	
        	pathwayDisease.put(id, disease);
        }
        reader.close();

		fileName = "/Users/xMAnton/biodb/reactome/pathwaySummation.txt";
        System.out.println("\nReading pathways summations from " + fileName);
        HashMap<String, String> pathwaySummation = new HashMap<String, String>();

        reader = new BufferedReader(new FileReader(fileName));
        line = reader.readLine(); //skip header line
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	if (datavalue.length <= 1)
        		continue;
        	
        	String id = datavalue[0];
        	String summation = datavalue[1];
        	
        	pathwaySummation.put(id, summation);
        }
        reader.close();
        
		fileName = "/Users/xMAnton/biodb/reactome/pathwayLiteratureReference.txt";
        System.out.println("\nReading literature references from " + fileName);
        HashMap<String, ArrayList<String>> pathwayLiteratureReference = new HashMap<String, ArrayList<String>>();

        reader = new BufferedReader(new FileReader(fileName));
        line = reader.readLine(); //skip header line
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	String id = datavalue[0];
        	String pubmedId = datavalue[1];
        	ArrayList<String> idList;
        	
        	idList = pathwayLiteratureReference.containsKey(id) ? pathwayLiteratureReference.get(id) : new ArrayList<String>();
        	idList.add(pubmedId);
        	
        	pathwayLiteratureReference.put(id, idList);
        }
        reader.close();

		fileName = "/Users/xMAnton/biodb/reactome/pathwaySummationLiteratureReference.txt";
        System.out.println("\nReading summation literature references from " + fileName);
        HashMap<String, ArrayList<String>> pathwaySummationLiteratureReference = new HashMap<String, ArrayList<String>>();

        reader = new BufferedReader(new FileReader(fileName));
        line = reader.readLine(); //skip header line
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	String id = datavalue[0];
        	String pubmedId = datavalue[1];
        	ArrayList<String> idList;
        	
        	idList = pathwaySummationLiteratureReference.containsKey(id) ? pathwaySummationLiteratureReference.get(id) : new ArrayList<String>();
        	idList.add(pubmedId);
        	
        	pathwaySummationLiteratureReference.put(id, idList);
        }
        reader.close();

        System.out.print("\nCreating pathways nodes ");
        HashMap<String, Vertex> pathway = new HashMap<String, Vertex>();
        
        for (String id : pathwayName.keySet()) {
        	String name = pathwayName.get(id);
        	String disease = (pathwayDisease.get(id) == null) ? "" : pathwayDisease.get(id);
        	String summation = (pathwaySummation.get(id) == null) ? "" : pathwaySummation.get(id);
        	ArrayList<String> literatureReference = pathwayLiteratureReference.get(id);
        	ArrayList<String> summationLiteratureReference = pathwaySummationLiteratureReference.get(id);
        	
        	Vertex p = graph.addVertex("class:pathway",
        			"pathwayId", id,
        			"name", name,
        			"disease", disease,
        			"summation", summation
        			);
        	
        	pathway.put(id, p);
        	
        	if (literatureReference != null)
	        	for (String reference : literatureReference) {
	            	Iterator<Vertex> it = graph.getVertices("pubmed.pubmedId", reference).iterator();
	        		Vertex citation = it.hasNext() ? it.next() : graph.addVertex("class:pubmed", "pubmedId", reference);
	        		p.addEdge("citedIn", citation);
	        		edgeCounter++;
	        	}
        	
        	if (summationLiteratureReference != null)
	        	for (String reference : summationLiteratureReference) {
	            	Iterator<Vertex> it = graph.getVertices("pubmed.pubmedId", reference).iterator();
	        		Vertex citation = it.hasNext() ? it.next() : graph.addVertex("class:pubmed", "pubmedId", reference);
	        		p.addEdge("citedIn", citation);
	        		edgeCounter++;
	        	}

        	entryCounter++;
            if (entryCounter % 100 == 0) {
            	System.out.print("."); System.out.flush();
            }
        }
        
		fileName = "/Users/xMAnton/biodb/reactome/pathway2go.txt";
        System.out.print("\n\nCreating pathway to GO relations ");

        reader = new BufferedReader(new FileReader(fileName));
        line = reader.readLine(); //skip header line
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	String id = datavalue[0];
        	String goId = datavalue[1];

        	Vertex p = pathway.get(id);
        	Vertex g = null;
        	
        	Iterator<Vertex> it = graph.getVertices("go.goId", goId).iterator();
        	if (it.hasNext())
        		g = it.next();
        	
        	if ((p != null) && (g != null)) {
        		p.addEdge("pathway2go", g);

        		edgeCounter++;
                if (edgeCounter % 100 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        reader.close();
        
		fileName = "/Users/xMAnton/biodb/reactome/miRBase2Reactome_All_Levels.txt";
        System.out.print("\n\nCreating miRBase to pathway relations ");

        reader = new BufferedReader(new FileReader(fileName));
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	String accessionId = datavalue[0];
        	String pathwayId = datavalue[1];

        	if (accessionId.startsWith("miR"))
        		accessionId = "MI" + accessionId.substring(3);
        	
        	Vertex m = null;
        	Vertex p = pathway.get(pathwayId);
        	
        	Iterator<Vertex> it = graph.getVertices("miRNA.accession", accessionId).iterator();
        	if (it.hasNext())
        		m = it.next();
        	
        	if ((p != null) && (m != null)) {
        		m.addEdge("miRNA2pathway", p);

        		edgeCounter++;
                if (edgeCounter % 100 == 0) {
                	System.out.print("."); System.out.flush();
                }
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
