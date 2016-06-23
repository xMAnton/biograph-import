package it.cnr.icar.biograph.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class _10_Pathway2GoImport {

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

        int edgeCounter = 0;
        long startTime = System.currentTimeMillis();

        String fileName = "/Users/xMAnton/biodb/reactome/pathway2go.txt";
        System.out.print("\n\nCreating pathway to GO relations ");

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine(); //skip header line
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	String id = datavalue[0];
        	String goId = datavalue[1];

        	Vertex p = null;
        	Vertex g = null;

        	Iterator<Vertex> it = graph.getVertices("pathway.pathwayId", id).iterator();
        	if (it.hasNext())
        		p = it.next();

        	it = graph.getVertices("go.goId", goId).iterator();
        	if (it.hasNext())
        		g = it.next();
        	
        	if ((p != null) && (g != null)) {
        		g.addEdge("annotates", p);

        		edgeCounter++;
                if (edgeCounter % 100 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        reader.close();
        
        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + edgeCounter + " edges in " + timeConversion(stopTime));

    	graph.declareIntent(null);
    	graph.shutdown();
        graphFactory.close();
	}
}
