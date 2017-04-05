package it.cnr.icar.biograph.importers;

import java.io.FileReader;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

import it.cnr.icar.biograph.models.uniprot.CitationType;
import it.cnr.icar.biograph.models.uniprot.CommentType;
import it.cnr.icar.biograph.models.uniprot.DbReferenceType;
import it.cnr.icar.biograph.models.uniprot.Entry;
import it.cnr.icar.biograph.models.uniprot.IsoformType;
import it.cnr.icar.biograph.models.uniprot.OrganismType;
import it.cnr.icar.biograph.models.uniprot.ProteinType;
import it.cnr.icar.biograph.models.uniprot.ReferenceType;
import it.cnr.icar.biograph.models.uniprot.SequenceType;

public class _12_UniprotImport {

	//final static String dbUrl = "plocal:/mnt/storage/orientdb/orientdb-enterprise-2.1.5/databases/biorient";
	//final static String dbUrl = "plocal:/Users/ninni/orientdb-enterprise-2.1.5/databases/biorient";
	private static String timeConversion(long seconds) {

	    final int MINUTES_IN_AN_HOUR = 60;
	    final int SECONDS_IN_A_MINUTE = 60;

	    long minutes = seconds / SECONDS_IN_A_MINUTE;
	    seconds -= minutes * SECONDS_IN_A_MINUTE;

	    long hours = minutes / MINUTES_IN_AN_HOUR;
	    minutes -= hours * MINUTES_IN_AN_HOUR;

	    return hours + " hours " + minutes + " minutes " + seconds + " seconds";
	}
	
    public static void main(String[] args) throws Exception {
    	String dbUrl = "remote://localhost/biograph";
    	String fileName = "/Users/xMAnton/biodb/uniprot_sprot.xml";
    	
    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());

    	graph.createEdgeType("refersTo");
 
    	OrientVertexType vprot = graph.createVertexType("protein");
    	vprot.createProperty("name", OType.STRING);
    	vprot.createProperty("fullName", OType.STRING);
    	vprot.createProperty("sequenceLenght", OType.INTEGER);
    	vprot.createProperty("sequenceMass", OType.INTEGER);
    	
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "protein"));
    	graph.createKeyIndex("fullName", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "protein"));
       	graph.createKeyIndex("sequenceLenght", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "protein"));
       	graph.createKeyIndex("sequenceMass", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "protein"));

    	OrientVertexType vacc = graph.createVertexType("proteinAccession");
    	vacc.createProperty("name", OType.STRING);
    	
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "proteinAccession"));

        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xsr = xif.createXMLStreamReader(new FileReader(fileName));
        xsr.nextTag(); // Advance to statements element

        int entryCounter = 0;
        int edgeCounter = 0;
        
        long startTime = System.currentTimeMillis();
        JAXBContext jc = JAXBContext.newInstance(Entry.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        
        System.out.print("\nReading proteins entries from " + fileName + " ");
        while (xsr.nextTag() == XMLStreamConstants.START_ELEMENT) {
            Entry entry = (Entry) unmarshaller.unmarshal(xsr);
            
            OrganismType organism = entry.getOrganism();
            String organismTaxonomyId = ((organism != null) && (!organism.getDbReference().isEmpty())) ? organism.getDbReference().get(0).getId() : "";
            
            if (organismTaxonomyId.equals("9606")) {
            	if (entry.getAccession().isEmpty())
            		continue;
            	
            	ProteinType prot = entry.getProtein();
            	
            	//String accession = entry.getAccession().get(0);
            	String name = entry.getName().get(0);
            	String fullName = ((prot.getRecommendedName() != null) && (prot.getRecommendedName().getFullName() != null)) ? prot.getRecommendedName().getFullName().getValue() : "";
            	String alternativeName = ((!prot.getAlternativeName().isEmpty()) && (prot.getAlternativeName().get(0).getFullName() != null)) ? prot.getAlternativeName().get(0).getFullName().getValue() : "";
            	
            	SequenceType seq = entry.getSequence();
            	String sequence = seq.getValue();
            	int sequenceLenght = seq.getLength();
            	int sequenceMass = seq.getMass();
            	
            	Vertex protein = graph.addVertex("class:protein",
            			"name", name,
            			"fullName", fullName,
            			"alternativeName", alternativeName,
            			"sequence", sequence,
            			"sequenceLenght", sequenceLenght,
            			"sequenceMass", sequenceMass
            			);
                entryCounter++;

            	for (String accessionName : entry.getAccession()) {
            		Vertex accession = graph.addVertex("class:proteinAccession",
            				"name", accessionName
            				);
            		accession.addEdge("refersTo", protein);
                    entryCounter++;
                    edgeCounter++;
            	}

            	for (CommentType comment : entry.getComment()) {
            		if (!comment.getIsoform().isEmpty()) {
            			for (IsoformType isoform : comment.getIsoform()) {
            				for (String isoId : isoform.getId()) {
                        		Vertex accession = graph.addVertex("class:proteinAccession",
                        				"name", isoId
                        				);
                        		accession.addEdge("refersTo", protein);
                                entryCounter++;
                                edgeCounter++;
            				}
            			}
            		}
            	}
            	
            	for (ReferenceType refType: entry.getReference()) {
            		CitationType citation = refType.getCitation();
            		if ((citation != null) && (citation.getType().equals("journal article"))) {
            			
            			if (citation.getTitle() != null)			
                	        for (DbReferenceType dbref: citation.getDbReference()) {
                	        	if (dbref.getType().equals("PubMed")) {
                	        			String pubmedId = dbref.getId();
                	        			
                        				if (!pubmedId.equals("")) {
                        	            	Iterator<Vertex> it = graph.getVertices("pubmed.pubmedId", pubmedId).iterator();
                        	            	Vertex cit = null;
                        	            	if (it.hasNext())
                        	            		cit = it.next();
                        	            	else {
                        	            		cit = graph.addVertex("class:pubmed", "pubmedId", pubmedId);
                        	            		entryCounter++;
                        	            	}
                    	            		protein.addEdge("citedIn", cit);
                        	        		edgeCounter++;
                        				}
                	        	}
                	        }                	        
            		}
            	}

            	
                if (entryCounter % 2000 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
    	
    	graph.declareIntent(null);
        graph.shutdown();
        graphFactory.close();
        
        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nCreated " + entryCounter + " vertices and " + edgeCounter + " edges in " + timeConversion(stopTime));
    }

}
