package it.cnr.icar.biograph.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class _13_Uniprot2PathwayImport {

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
		String fileName = "/Users/xMAnton/biodb/reactome/uniprot2pathway.txt";
		String line;
		
		int edgeCounter = 0;
		
        long startTime = System.currentTimeMillis();

    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());

    	graph.createEdgeType("contains");

	    BufferedReader reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\nImporting protein to pathway associations from " + fileName + " ");
        
        HashMap<String, Integer> noProt = new HashMap<String, Integer>();
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	String uniprotId = datavalue[0];
        	String pathwayId = datavalue[1];

        	Vertex protein = null;
        	Vertex pathway = null;
        	
        	Iterator<Vertex> it = graph.getVertices("proteinName.name", uniprotId).iterator();
			if (it.hasNext())
				protein = it.next().getEdges(Direction.OUT, "refersTo").iterator().next().getVertex(Direction.IN);
			else {
				Integer c = noProt.getOrDefault(uniprotId, 0);
				c++;
				noProt.put(uniprotId, c);
			}

			it = graph.getVertices("pathway.pathwayId", pathwayId).iterator();
			if (it.hasNext())
				pathway = it.next();
			
			if ((protein != null) && (pathway != null)) {
				pathway.addEdge("contains", protein);
				edgeCounter++;

				if (edgeCounter % 5000 == 0) {
                	System.out.print("."); System.out.flush();
                }
			}

        }

        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + edgeCounter + " edges in " + timeConversion(stopTime));

        System.out.print("\nNot found proteins: ");
        for (String id : noProt.keySet())
        	System.out.print(id + "(" + noProt.get(id) + ") ");
        
        reader.close();

        graph.declareIntent(null);
        graph.shutdown();
        
        graphFactory.close();
	}
}
