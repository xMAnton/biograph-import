package it.cnr.icar.biorient.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Iterator;


import java.util.Iterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class miRCancerImport {

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
		String fileName = "/Users/ninni/biodb/miRCancerSeptember2015.txt";
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
	    	graph.dropEdgeType("cancer2mirna");
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropKeyIndex("cancer.name", Vertex.class);
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropKeyIndex("cancer.profile", Vertex.class);
		} catch (Exception e) {
		}
    	try {
	    	graph.dropVertexType("cancer");
		} catch (Exception e) {
		}

    	graph.createVertexType("cancer");
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "cancer"));
    	graph.createKeyIndex("profile", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "cancer"));
    	graph.createEdgeType("cancer2mirna");

	    BufferedReader reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\nImporting miRCancer from " + fileName + " ");
        
        // skip first line
        reader.readLine();
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	String mirId = datavalue[0];
        	String cancerName = datavalue[1];
        	String cancerProfile = datavalue[2];
        	//String pubmedTitle = datavalue[3];

        	Vertex miRNA = null;
        	Vertex cancer = null;

        	Iterator<Vertex> it = graph.getVertices("miRNA.name", mirId).iterator();
        	if (it.hasNext()) {
        		miRNA = it.next();
        		
                it = graph.getVertices("cancer.name", cancerName).iterator();
        		if (it.hasNext()) {
        			cancer = it.next();
        			String profile = cancer.getProperty("profile");
        			
        			if (!cancerProfile.equals(profile)) {
        				if (it.hasNext())
        					cancer = it.next();
        				else {
        					cancer = graph.addVertex("class:cancer", "name", cancerName, "profile", cancerProfile);
        					entryCounter++;
        				}
        			}
        		}
        		else {
        			cancer = graph.addVertex("class:cancer", "name", cancerName, "profile", cancerProfile);
        			entryCounter++;
        		}
        		
        		cancer.addEdge("cancer2mirna", miRNA);
        		edgeCounter++;

        		if (entryCounter % 500 == 0) {
                	System.out.print("."); System.out.flush();
                }
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
