package it.cnr.icar.biorient.importers;

import java.io.IOException;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class PubmedMedlineInit {

	public static void main(String[] args) throws IOException {
		
		String dbUrl = "remote://localhost/biorient";
		
    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();

    	graph.declareIntent(new OIntentMassiveInsert());
    	graph.setStandardElementConstraints(false);

    	try {	    	
	    	graph.dropEdgeType("citedIn");
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropKeyIndex("pubmed.id", Vertex.class);
		} catch (Exception e) {
		}
    	try {
	    	graph.dropVertexType("pubmed");
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropKeyIndex("medline.id", Vertex.class);
		} catch (Exception e) {
		}
    	try {
	    	graph.dropVertexType("medline");
		} catch (Exception e) {
		}

    	graph.createVertexType("pubmed");
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "pubmed"));
    	graph.createVertexType("medline");
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "medline"));
    	graph.createEdgeType("citedIn");

        System.out.println("\n\nCreated pubmed/medline node types, indexes and citedIn relation");

    	graph.declareIntent(null);
    	graph.shutdown();
        graphFactory.close();
	}
}
