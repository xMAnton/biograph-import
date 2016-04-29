package it.cnr.icar.biorient.importers;

import it.cnr.icar.biorient.models.uniprot.CitationType;
import it.cnr.icar.biorient.models.uniprot.DbReferenceType;
import it.cnr.icar.biorient.models.uniprot.Entry;
import it.cnr.icar.biorient.models.uniprot.OrganismType;
import it.cnr.icar.biorient.models.uniprot.ProteinType;
import it.cnr.icar.biorient.models.uniprot.ReferenceType;
import it.cnr.icar.biorient.models.uniprot.SequenceType;

import java.io.FileReader;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class UniprotImport {

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
    	String dbUrl = "remote://localhost/biorient";
    	String fileName = "/Users/ninni/biodb/uniprot_sprot.xml";
    	
    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.declareIntent(new OIntentMassiveInsert());
    	graph.setStandardElementConstraints(false);

    	try {	    	
	    	graph.dropEdgeType("refersTo");
		} catch (Exception e) {
		}
    	graph.createEdgeType("refersTo");

    	try {	    	
	    	graph.dropKeyIndex("proteinAccession.name", Vertex.class);
		} catch (Exception e) {
		}
    	try {
	    	graph.dropVertexType("proteinAccession");
		} catch (Exception e) {
		}

    	try {	    	
	    	graph.dropKeyIndex("protein.name", Vertex.class);
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropKeyIndex("protein.accession", Vertex.class);
		} catch (Exception e) {
		}
    	try {
	    	graph.dropVertexType("protein");
		} catch (Exception e) {
		}
    	graph.createVertexType("protein");
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "protein"));

    	graph.createVertexType("proteinAccession");
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "proteinAccession"));
    	/*
    	graph.createVertexType("Sequence");
    	graph.createVertexType("Ensemble");
    	graph.createVertexType("PIR");
    	graph.createVertexType("UniGene");
    	graph.createVertexType("KEGG");
    	graph.createVertexType("EMBL");
    	graph.createVertexType("RefSeq");
    	graph.createVertexType("Reactome");
    	graph.createVertexType("Comment");
    	graph.createVertexType("Disease");
    	graph.createVertexType("SubcellularLocation");
    	graph.createVertexType("Isoform");
    	graph.createVertexType("AlternativeProduct");
    	graph.createVertexType("SequenceCaution");
    	graph.createVertexType("Feature");
    	graph.createVertexType("Dataset");
    	graph.createVertexType("Reference");
    	graph.createVertexType("Person");
    	graph.createVertexType("Consortium");
    	graph.createVertexType("Thesis");
    	graph.createVertexType("Institute");
    	graph.createVertexType("Country");
    	graph.createVertexType("Patent");
    	graph.createVertexType("Submission");
    	graph.createVertexType("DBName");
    	graph.createVertexType("Book");
    	graph.createVertexType("Publisher");
    	graph.createVertexType("City");
    	graph.createVertexType("OnlineArticle");
    	graph.createVertexType("OnlineJournal");
    	graph.createVertexType("Article");
    	graph.createVertexType("PubMed");
    	graph.createVertexType("Journal");
    	graph.createVertexType("UnpublishedObservation");
    	
    	graph.createEdgeType("hasSequence");
    	graph.createEdgeType("hasEnsemble");
    	graph.createEdgeType("hasPIR");
    	graph.createEdgeType("hasUniGene");
    	graph.createEdgeType("hasKEGG");
    	graph.createEdgeType("hasEMBL");
    	graph.createEdgeType("hasRefSeq");
    	graph.createEdgeType("hasReactome");
    	graph.createEdgeType("hasComment");
    	graph.createEdgeType("hasDisease");
    	graph.createEdgeType("hasLinkedSubcellularLocation");
    	graph.createEdgeType("hasIsoform");
    	graph.createEdgeType("hasAlternativeProduct");
    	graph.createEdgeType("hasSequenceCaution");
    	graph.createEdgeType("hasFeature");
    	graph.createEdgeType("hasDataset");
    	graph.createEdgeType("hasReference");
    	graph.createEdgeType("isAuthor");
    	graph.createEdgeType("isThesis");
    	graph.createEdgeType("hasInstitute");
    	graph.createEdgeType("locatedIn");
    	graph.createEdgeType("isPatent");
    	graph.createEdgeType("isSubmission");
    	graph.createEdgeType("isInDB");
    	graph.createEdgeType("isBook");
    	graph.createEdgeType("editedBy");
    	graph.createEdgeType("publishedBy");
    	graph.createEdgeType("publishedIn");
    	graph.createEdgeType("isOnlineArticle");
    	graph.createEdgeType("inOnlineJournal");
    	graph.createEdgeType("isArticle");
    	graph.createEdgeType("isPubMedArticle");
    	graph.createEdgeType("inJournal");
    	graph.createEdgeType("isUnpublishedObservation");
    	
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Accession"));
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Ensemble"));
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "PIR"));
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "UniGene"));
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "KEGG"));
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "EMBL"));
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "RefSeq"));
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Reactome"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Comment"));
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Disease"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "SubcellularLocation"));
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Isoform"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "AlternativeProduct"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "SequenceCaution"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Feature"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Dataset"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Person"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Consortium"));
    	graph.createKeyIndex("title", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Thesis"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Institute"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Country"));
    	graph.createKeyIndex("number", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Patent"));
    	graph.createKeyIndex("title", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Submission"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "DBName"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Book"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Publisher"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "City"));
    	graph.createKeyIndex("title", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "OnlineArticle"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "OnlineJournal"));
    	graph.createKeyIndex("title", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Article"));
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "PubMed"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "Journal"));
    	*/
    	
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

            	
            	for (ReferenceType refType: entry.getReference()) {
            		CitationType citation = refType.getCitation();
            		if ((citation != null) && (citation.getType().equals("journal article"))) {
            			
            			if (citation.getTitle() != null)			
                	        for (DbReferenceType dbref: citation.getDbReference()) {
                	        	if (dbref.getType().equals("PubMed")) {
                	        			String pubmedId = dbref.getId();
                	        			
                        				if (!pubmedId.equals("")) {
                        	            	Iterator<Vertex> it = graph.getVertices("pubmed.id", pubmedId).iterator();
                        	            	Vertex cit = null;
                        	            	if (it.hasNext())
                        	            		cit = it.next();
                        	            	else {
                        	            		cit = graph.addVertex("class:pubmed", "id", pubmedId);
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
