package it.cnr.icar.biograph.importers;

import java.io.IOException;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class _00_PubmedInit {

	public static void main(String[] args) throws IOException {
		
		String dbUrl = "remote://localhost/biograph";
		
    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();

    	graph.declareIntent(new OIntentMassiveInsert());

    	OrientVertexType pubmed = graph.createVertexType("pubmed");
    	pubmed.createProperty("pubmedId", OType.STRING);
    	graph.createKeyIndex("pubmedId", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "pubmed"));
    	graph.createEdgeType("citedIn");

        System.out.println("\n\nCreated pubmed node type, index and citedIn relation");

    	graph.declareIntent(null);
    	graph.shutdown();
        graphFactory.close();
	}
}
