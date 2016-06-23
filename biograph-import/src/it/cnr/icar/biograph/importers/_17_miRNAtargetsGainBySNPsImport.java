package it.cnr.icar.biograph.importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class _17_miRNAtargetsGainBySNPsImport {

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
		String fileName = "/Users/xMAnton/biodb/miRNASNP/miRNA_targets_gain_by_SNPs_in_seed_regions.txt";
		String line;
		
		int entryCounter = 0;
		
        long startTime = System.currentTimeMillis();

    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());

    	OrientVertexType vint = graph.getVertexType("interaction");
    	vint.createProperty("snpEnergy", OType.DOUBLE);
    	vint.createProperty("basePair", OType.STRING);
    	vint.createProperty("geneAve", OType.DOUBLE);
    	vint.createProperty("mirnaAve", OType.DOUBLE);
    	
    	graph.createKeyIndex("snpEnergy", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "interaction"));

    	graph.createEdgeType("interactingSNP");

	    BufferedReader reader = new BufferedReader(new FileReader(fileName));

        System.out.print("\nImporting miRNA target gain by SNPs in seed regions from " + fileName + " ");
        
        // skip first line
        reader.readLine();
        
        while ((line = reader.readLine()) != null) {        	
        	String datavalue[] = line.split("\t");
        	
        	//String id = datavalue[0];
        	String gene_id = datavalue[1];
        	String transcriptId = datavalue[2];
        	//String mirAccession = datavalue[3];
        	String SNPid = datavalue[4];
        	
        	int miRstart = Integer.valueOf(datavalue[5]);
        	int miRend = Integer.valueOf(datavalue[6]);
        	double energy = Double.valueOf(datavalue[7]);
        	double snpEnergy = Double.valueOf(datavalue[8]);

        	String basePair = datavalue[9];
        	
        	double geneAve = Double.NaN;
        	if ((datavalue[10] != null) && (!datavalue[10].equalsIgnoreCase("null")))
        		geneAve = Double.valueOf(datavalue[10]);
        	
        	//String geneId = datavalue[11];

        	double mirnaAve = Double.NaN;
        	if ((datavalue[12] != null) && (!datavalue[12].equalsIgnoreCase("null")))
        		mirnaAve = Double.valueOf(datavalue[12]);

        	//String mirnaId = datavalue[13];

        	Vertex miRNASNP = null;
        	Vertex gene = null;
        	
        	Iterator<Vertex> it = graph.getVertices("miRNASNP.SNPid", SNPid).iterator();
        	if (it.hasNext()) {
        		miRNASNP = it.next();
        		
        		it = graph.getVertices("gene.nomenclatureAuthoritySymbol", gene_id).iterator();
        		if (it.hasNext())
        			gene = it.next();
        		
        		if ((miRNASNP != null) && (gene != null)) {
        			entryCounter++;
        			Vertex interaction = graph.addVertex("class:interaction",
                			"extTranscriptId", transcriptId,
                			"mirStart", miRstart,
                			"mirEnd", miRend,
                			"energy", energy,
                			"snpEnergy", snpEnergy,
                			"basePair", basePair,
                			"geneAve", geneAve,
                			"mirnaAve", mirnaAve,
                			"database", "miRNASNP"
                			);
                	interaction.addEdge("interactingSNP", miRNASNP);
                	interaction.addEdge("interactingGene", gene);

                    if (entryCounter % 12500 == 0) {
                    	System.out.print("."); System.out.flush();
                    }
        		}
        	}
        }
        
        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + entryCounter + " vertices and " + (entryCounter*2) + " edges in " + timeConversion(stopTime));
       
        reader.close();

        graph.declareIntent(null);
        graph.shutdown();
        
        graphFactory.close();
	}
}
