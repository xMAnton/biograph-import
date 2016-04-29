package it.cnr.icar.biorient.importers;

import it.cnr.icar.biorient.models.go.*;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class GoTermImport {

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
		String fileName = "/Users/ninni/biodb/go_daily-termdb.xml";

		HashMap<String, Vertex> idVertexMap = new HashMap<String, Vertex>();
		
    	HashMap<String, List<String>> termParentsMap = new HashMap<String, List<String>>();
    	HashMap<String, List<String>> regulatesMap = new HashMap<String, List<String>>();
    	HashMap<String, List<String>> negativelyRegulatesMap = new HashMap<String, List<String>>();
    	HashMap<String, List<String>> positivelyRegulatesMap = new HashMap<String, List<String>>();
    	HashMap<String, List<String>> partOfMap = new HashMap<String, List<String>>();
    	HashMap<String, List<String>> hasPartMap = new HashMap<String, List<String>>();
        
    	OrientGraphFactory graphFactory = new OrientGraphFactory(dbUrl);
    	OrientGraphNoTx graph = graphFactory.getNoTx();
    	
    	graph.setStandardElementConstraints(false);
    	
    	try {
	    	graph.dropEdgeType("hasPart");
    	} catch (Exception e) {
    	}
    	try {
	    	graph.dropEdgeType("partOf");
    	} catch (Exception e) {
    	}
    	try {
	    	graph.dropEdgeType("positivelyRegulates");
    	} catch (Exception e) {
    	}
    	try {
	    	graph.dropEdgeType("negativelyRegulates");
    	} catch (Exception e) {
    	}
    	try {
	    	graph.dropEdgeType("regulates");
    	} catch (Exception e) {
    	}
    	try {
	    	graph.dropEdgeType("isA");
    	} catch (Exception e) {
    	}
    	try {	    	
	    	graph.dropKeyIndex("go.id", Vertex.class);
		} catch (Exception e) {
		}
    	try {	    	
	    	graph.dropKeyIndex("go.name", Vertex.class);
		} catch (Exception e) {
		}
    	try {
	    	graph.dropVertexType("go");
		} catch (Exception e) {
		}

    	//graph.createVertexType("go_SubOntology");
    	graph.createVertexType("go");
    	
    	//graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "go_SubOntology"));
    	graph.createKeyIndex("id", Vertex.class, new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, String>("class", "go"));
    	graph.createKeyIndex("name", Vertex.class, new Parameter<String, String>("type", "NOTUNIQUE"), new Parameter<String, String>("class", "go"));

    	graph.createEdgeType("isA");
    	graph.createEdgeType("regulates");
    	graph.createEdgeType("negativelyRegulates");
    	graph.createEdgeType("positivelyRegulates");
    	graph.createEdgeType("partOf");
    	graph.createEdgeType("hasPart");
    	  	
    	/*
    	Vertex subOntologyBP = graph.addVertex("class:go_SubOntology", "name", "biological_process");
    	Vertex subOntologyCC = graph.addVertex("class:go_SubOntology", "name", "cellular_component");
    	Vertex subOntologyMF = graph.addVertex("class:go_SubOntology", "name", "molecular_function");
    	*/
    	
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xsr = xif.createXMLStreamReader(new FileReader(fileName));
        xsr.nextTag(); // Advance to statements element

        int entryCounter = 0;
        long startTime = System.currentTimeMillis();
        JAXBContext jc = JAXBContext.newInstance(Header.class, Source.class, Term.class, Typedef.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        
        System.out.println("\nReading GO entries from " + fileName + "\n");
        System.out.print("inserting term nodes ");
        while (xsr.nextTag() == XMLStreamConstants.START_ELEMENT) {
            Object entry = unmarshaller.unmarshal(xsr);
            
            if (entry instanceof Term) {
            	Term term = (Term)entry;
            	
            	String goId = term.getId();
            	String goName = (term.getName() != null) ? term.getName() : "";
            	String goDefinition = ((term.getDef() != null) && (term.getDef().getDefstr() != null)) ? term.getDef().getDefstr() : "";
            	String goComment = (term.getComment() != null) ? term.getComment() : "";
            	String goIsObsolete = "";
            	if (term.getIsObsolete() != null) {
            		goIsObsolete = (term.getIsObsolete() == 1) ? "true" : "false";
            	}
            	String goNamespace = (term.getNamespace() != null) ? term.getNamespace() : "";
            	            	
                entryCounter++;
	
                Vertex t = graph.addVertex("class:go", 
                		"id", goId,
                		"name", goName,
                		"namespace", goNamespace,
                		"definition", goDefinition,
                		"obsolete", goIsObsolete,
                		"comment", goComment
                		);

                idVertexMap.put(goId, t);
                
            	termParentsMap.put(goId, term.getIsA());
            	
        		for (Term.Relationship rel : term.getRelationship()) {
            		String goRelationshipType = rel.getType();
            		String goRelationshipTo = rel.getTo();
            		
            		List<String> tempArray = null;
            		
            		switch (goRelationshipType) {
            			case "regulates":
            				tempArray = regulatesMap.get(goId);
            				if (tempArray == null) {
					          tempArray = new ArrayList<String>();
					          regulatesMap.put(goId, tempArray);
            			    }
            			    tempArray.add(goRelationshipTo);
            				break;
            			case "positively_regulates":
            				tempArray = positivelyRegulatesMap.get(goId);
            				if (tempArray == null) {
        			          tempArray = new ArrayList<String>();
        			          positivelyRegulatesMap.put(goId, tempArray);
            			    }
            			    tempArray.add(goRelationshipTo);
            				break;
            			case "negatively_regulates":
            				tempArray = negativelyRegulatesMap.get(goId);
            				if (tempArray == null) {
            					tempArray = new ArrayList<String>();
            					negativelyRegulatesMap.put(goId, tempArray);
            				}
            				tempArray.add(goRelationshipTo);
            				break;
            			case "part_of":
            				tempArray = partOfMap.get(goId);
            				if (tempArray == null) {
            					tempArray = new ArrayList<String>();
            					partOfMap.put(goId, tempArray);
            				}
            				tempArray.add(goRelationshipTo);
            				break;
            			case "has_part":
            				tempArray = hasPartMap.get(goId);
            				if (tempArray == null) {
            					tempArray = new ArrayList<String>();
            					hasPartMap.put(goId, tempArray);
            				}
            				tempArray.add(goRelationshipTo);
            				break;
            		}
        		}
            }

            if (entryCounter % 1000 == 0) {
            	System.out.print("."); System.out.flush();
            }
        }

        System.out.println("\n\ncreating relationships:");

        int relCounter = 0;
        List<String> tempArray = null;
        Set<String> keys = null;

        System.out.print("\n  'is_a' relationships ");
        keys = termParentsMap.keySet();
        for (String key : keys) {
        	//Vertex tempGoTerm = graph.getVertices("go_Term.id", key).iterator().next();
        	Vertex tempGoTerm = idVertexMap.get(key);
        	tempArray = termParentsMap.get(key);
        	for (String string : tempArray) {
        		Vertex tempGoTerm2 = idVertexMap.get(string);
        		tempGoTerm.addEdge("isA", tempGoTerm2);
        		relCounter++;
                if (relCounter % 1000 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        
        System.out.print("\n\n  'regulates' relationships ");
        keys = regulatesMap.keySet();
        for (String key : keys) {
        	//Vertex tempGoTerm = graph.getVertices("go_Term.id", key).iterator().next();
        	Vertex tempGoTerm = idVertexMap.get(key);
        	tempArray = regulatesMap.get(key);
        	for (String string : tempArray) {
        		Vertex tempGoTerm2 = idVertexMap.get(string);
        		tempGoTerm.addEdge("regulates", tempGoTerm2);
        		relCounter++;
                if (relCounter % 100 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        
        System.out.print("\n\n  'negatively_regulates' relationships ");
        keys = negativelyRegulatesMap.keySet();
        for (String key : keys) {
        	//Vertex tempGoTerm = graph.getVertices("go_Term.id", key).iterator().next();
        	Vertex tempGoTerm = idVertexMap.get(key);
        	tempArray = negativelyRegulatesMap.get(key);
        	for (String string : tempArray) {
        		Vertex tempGoTerm2 = idVertexMap.get(string);
        		tempGoTerm.addEdge("negativelyRegulates", tempGoTerm2);
        		relCounter++;
                if (relCounter % 100 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        
        System.out.print("\n\n  'positively_regulates' relationships ");
        keys = positivelyRegulatesMap.keySet();
        for (String key : keys) {
        	//Vertex tempGoTerm = graph.getVertices("go_Term.id", key).iterator().next();
        	Vertex tempGoTerm = idVertexMap.get(key);
        	tempArray = positivelyRegulatesMap.get(key);
        	for (String string : tempArray) {
        		Vertex tempGoTerm2 = idVertexMap.get(string);
        		tempGoTerm.addEdge("positivelyRegulates", tempGoTerm2);
        		relCounter++;
                if (relCounter % 100 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        
        System.out.print("\n\n  'part_of' relationships ");
        keys = partOfMap.keySet();
        for (String key : keys) {
        	//Vertex tempGoTerm = graph.getVertices("go_Term.id", key).iterator().next();
        	Vertex tempGoTerm = idVertexMap.get(key);
        	tempArray = partOfMap.get(key);
        	for (String string : tempArray) {
        		Vertex tempGoTerm2 = idVertexMap.get(string);
        		tempGoTerm.addEdge("partOf", tempGoTerm2);
        		relCounter++;
                if (relCounter % 100 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        
        System.out.print("\n\n  'has_part_of' relationships ");
        keys = hasPartMap.keySet();
        for (String key : keys) {
        	//Vertex tempGoTerm = graph.getVertices("go_Term.id", key).iterator().next();
        	Vertex tempGoTerm = idVertexMap.get(key);
        	tempArray = hasPartMap.get(key);
        	for (String string : tempArray) {
        		Vertex tempGoTerm2 = idVertexMap.get(string);
        		tempGoTerm.addEdge("hasPart", tempGoTerm2);
        		relCounter++;
                if (relCounter % 100 == 0) {
                	System.out.print("."); System.out.flush();
                }
        	}
        }
        
        graph.shutdown();
        graphFactory.close();
        
        long stopTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("\n\nImported " + entryCounter + " GO terms and created " + relCounter + " relations in " + timeConversion(stopTime));
    }

}
