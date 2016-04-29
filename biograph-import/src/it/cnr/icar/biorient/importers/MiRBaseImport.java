	package it.cnr.icar.biorient.importers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Feature;
import org.biojavax.Comment;
import org.biojavax.Namespace;
import org.biojavax.RankedCrossRef;
import org.biojavax.RankedDocRef;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class MiRBaseImport {

	private static String timeConversion(long seconds) {

	    final int MINUTES_IN_AN_HOUR = 60;
	    final int SECONDS_IN_A_MINUTE = 60;

	    long minutes = seconds / SECONDS_IN_A_MINUTE;
	    seconds -= minutes * SECONDS_IN_A_MINUTE;

	    long hours = minutes / MINUTES_IN_AN_HOUR;
	    minutes -= hours * MINUTES_IN_AN_HOUR;

	    return hours + " hours " + minutes + " minutes " + seconds + " seconds";
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchElementException, BioException {
		
		String dbUrl = "remote://localhost/biorient";
		String fileName = "/Users/ninni/biodb/miRNA.dat";
		
    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();

    	graph.declareIntent(new OIntentMassiveInsert());
    	graph.setStandardElementConstraints(false);

    	try {
	    	graph.dropEdgeType("precursorOf");
    	} catch (Exception e) {
    	}
    	try {	    	
	    	graph.dropKeyIndex("miRNA.accession", Vertex.class);
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropKeyIndex("miRNA.name", Vertex.class);
		} catch (Exception e) {
		}
    	try {	    		    	
	    	graph.dropKeyIndex("miRNAmature.accession", Vertex.class);
		} catch (Exception e) {
		}
    	try {	    		    	
	    	graph.dropKeyIndex("miRNAmature.product", Vertex.class);
    	} catch (Exception e) {
    	}
    	try {
	    	graph.dropVertexType("miRNA");
		} catch (Exception e) {
		}
    	try {
	    	graph.dropVertexType("miRNA_mature");
		} catch (Exception e) {
		}

    	graph.createVertexType("miRNA");
    	graph.createVertexType("miRNAmature");
    	
    	graph.createKeyIndex("accession", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "miRNA"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "miRNA"));

    	graph.createKeyIndex("accession", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "miRNAmature"));
    	graph.createKeyIndex("product", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "miRNAmature"));

    	graph.createEdgeType("precursorOf");

        int entryCounter = 0;
        int edgeCounter = 0;
        long startTime = System.currentTimeMillis();
        System.out.print("\nImporting miRNA entries from " + fileName + " ");

        BufferedReader br = new BufferedReader(new FileReader(fileName)); 
		Namespace ns = RichObjectFactory.getDefaultNamespace();
		RichSequenceIterator seqs = RichSequence.IOTools.readEMBLRNA(br, ns);
		
		while (seqs.hasNext()) {
			RichSequence entry = seqs.nextRichSequence();

            entryCounter++;

			String accession = entry.getAccession();
			String name = entry.getName();
			String description = entry.getDescription();
			Vector<String> dbReferences = new Vector<String>();
			Vector<String> comments = new Vector<String>();
			
			for (Comment comment : entry.getComments()) {
				String cmt = comment.getComment().replaceAll("\n", " ");
				comments.add(cmt);
			}
			String comment = "";
			if (comments.size() > 0)
				comment = comments.get(0);

			for (RankedCrossRef docRef : entry.getRankedCrossRefs()) {
				String reference = docRef.getCrossRef().getDbname() + " " + docRef.getCrossRef().getAccession();
				dbReferences.add(reference);
			}

			String sequence = entry.getInternalSymbolList().seqString();
			
            Vertex mirna = graph.addVertex("class:miRNA", 
            		"accession", accession,
            		"name", name,
            		"description", description,
            		"comment", comment,
            		//"dbRefs", dbReferences,
            		"sequence", sequence
            		);

			for (RankedDocRef docRef : entry.getRankedDocRefs()) {
				if (docRef.getDocumentReference().getCrossref() != null) {
					String db = docRef.getDocumentReference().getCrossref().getDbname();
					String reference = docRef.getDocumentReference().getCrossref().getAccession();
					
					if (db.equals("PUBMED")) {
		            	Iterator<Vertex> it = graph.getVertices("pubmed.id", reference).iterator();
		        		Vertex citation = it.hasNext() ? it.next() : graph.addVertex("class:pubmed", "id", reference);
		        		mirna.addEdge("citedIn", citation);
		        		edgeCounter++;
					} else
					if (db.equals("MEDLINE")) {
		            	Iterator<Vertex> it = graph.getVertices("medline.id", reference).iterator();
		        		Vertex citation = it.hasNext() ? it.next() : graph.addVertex("class:medline", "id", reference);
		        		mirna.addEdge("citedIn", citation);
		        		edgeCounter++;
					}
				}
			}

			Iterator<Feature> itf = entry.getFeatureSet().iterator();
			
			while (itf.hasNext()) {
				Feature f = itf.next();
				
				String location = f.getLocation().toString();
				String subSequence = sequence.substring(f.getLocation().getMin()-1, f.getLocation().getMax());
				
				entryCounter++;
				Vertex mature = graph.addVertex("class:miRNAmature",
						"location", location,
						"sequence", subSequence
						);
				
				@SuppressWarnings("unchecked")
				Map<Object, ?> map = f.getAnnotation().asMap();
				Set<Object> keys = map.keySet();
				for (Object key : keys) {
					String keyString = key.toString();
					String value = (String) map.get(key);
					
					mature.setProperty(keyString.substring(keyString.lastIndexOf(":")+1), value);
				}
				
				edgeCounter++;
				mirna.addEdge("precursorOf", mature);
			}
			
            if (entryCounter % 1000 == 0) {
            	System.out.print("."); System.out.flush();
            }
		}
		
        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + entryCounter + " vertices and " + edgeCounter + " edges in " + timeConversion(stopTime));

    	graph.declareIntent(null);
    	graph.shutdown();
        graphFactory.close();
	}
}
