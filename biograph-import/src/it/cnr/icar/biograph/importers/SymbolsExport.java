package it.cnr.icar.biograph.importers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class SymbolsExport {

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
		
		int entryCounter = 0;
		
        long startTime = System.currentTimeMillis();

    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.setStandardElementConstraints(false);

		String fileName1 = "/Users/ninni/biodb/exports/proteinAccession.txt";
	    BufferedWriter writer1 = new BufferedWriter(new FileWriter(fileName1));

	    System.out.print("\nExporting go to " + fileName1 + " ");

    	Iterator<Vertex> it = graph.getVerticesOfClass("proteinAccession").iterator();
    	while (it.hasNext()) {
    		Vertex go = it.next();
    		String name = go.getProperty("name");
    		writer1.write(name); writer1.newLine();
    		entryCounter++;
            if (entryCounter % 200 == 0) {
            	System.out.print("."); System.out.flush();
            }
    	}
        writer1.close();
    	        
        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nExported " + entryCounter + " symbols in " + timeConversion(stopTime));

        graph.shutdown();      
        graphFactory.close();
	}
}
