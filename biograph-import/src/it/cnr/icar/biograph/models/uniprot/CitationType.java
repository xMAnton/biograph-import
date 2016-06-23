//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2015.12.01 alle 07:40:41 AM CET 
//


package it.cnr.icar.biograph.models.uniprot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;


/**
 * Describes different types of citations.
 *             Equivalent to the flat file RX-, RG-, RA-, RT- and RL-lines.
 * 
 * <p>Classe Java per citationType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="citationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="editorList" type="{http://uniprot.org/uniprot}nameListType" minOccurs="0"/>
 *         &lt;element name="authorList" type="{http://uniprot.org/uniprot}nameListType" minOccurs="0"/>
 *         &lt;element name="locator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dbReference" type="{http://uniprot.org/uniprot}dbReferenceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="book"/>
 *             &lt;enumeration value="journal article"/>
 *             &lt;enumeration value="online journal article"/>
 *             &lt;enumeration value="patent"/>
 *             &lt;enumeration value="submission"/>
 *             &lt;enumeration value="thesis"/>
 *             &lt;enumeration value="unpublished observations"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="date">
 *         &lt;simpleType>
 *           &lt;union memberTypes=" {http://www.w3.org/2001/XMLSchema}date {http://www.w3.org/2001/XMLSchema}gYearMonth {http://www.w3.org/2001/XMLSchema}gYear">
 *           &lt;/union>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="volume" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="first" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="last" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="publisher" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="city" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="db" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="number" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="institute" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="country" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "citationType", propOrder = {
    "title",
    "editorList",
    "authorList",
    "locator",
    "dbReference"
})
public class CitationType {

    protected String title;
    protected NameListType editorList;
    protected NameListType authorList;
    protected String locator;
    protected List<DbReferenceType> dbReference;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "date")
    protected String date;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "volume")
    protected String volume;
    @XmlAttribute(name = "first")
    protected String first;
    @XmlAttribute(name = "last")
    protected String last;
    @XmlAttribute(name = "publisher")
    protected String publisher;
    @XmlAttribute(name = "city")
    protected String city;
    @XmlAttribute(name = "db")
    protected String db;
    @XmlAttribute(name = "number")
    protected String number;
    @XmlAttribute(name = "institute")
    protected String institute;
    @XmlAttribute(name = "country")
    protected String country;

    /**
     * Recupera il valore della proprietà title.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Imposta il valore della proprietà title.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Recupera il valore della proprietà editorList.
     * 
     * @return
     *     possible object is
     *     {@link NameListType }
     *     
     */
    public NameListType getEditorList() {
        return editorList;
    }

    /**
     * Imposta il valore della proprietà editorList.
     * 
     * @param value
     *     allowed object is
     *     {@link NameListType }
     *     
     */
    public void setEditorList(NameListType value) {
        this.editorList = value;
    }

    /**
     * Recupera il valore della proprietà authorList.
     * 
     * @return
     *     possible object is
     *     {@link NameListType }
     *     
     */
    public NameListType getAuthorList() {
        return authorList;
    }

    /**
     * Imposta il valore della proprietà authorList.
     * 
     * @param value
     *     allowed object is
     *     {@link NameListType }
     *     
     */
    public void setAuthorList(NameListType value) {
        this.authorList = value;
    }

    /**
     * Recupera il valore della proprietà locator.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocator() {
        return locator;
    }

    /**
     * Imposta il valore della proprietà locator.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocator(String value) {
        this.locator = value;
    }

    /**
     * Gets the value of the dbReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dbReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDbReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DbReferenceType }
     * 
     * 
     */
    public List<DbReferenceType> getDbReference() {
        if (dbReference == null) {
            dbReference = new ArrayList<DbReferenceType>();
        }
        return this.dbReference;
    }

    /**
     * Recupera il valore della proprietà type.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Imposta il valore della proprietà type.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Recupera il valore della proprietà date.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDate() {
        return date;
    }

    /**
     * Imposta il valore della proprietà date.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDate(String value) {
        this.date = value;
    }

    /**
     * Recupera il valore della proprietà name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietà name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Recupera il valore della proprietà volume.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVolume() {
        return volume;
    }

    /**
     * Imposta il valore della proprietà volume.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVolume(String value) {
        this.volume = value;
    }

    /**
     * Recupera il valore della proprietà first.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirst() {
        return first;
    }

    /**
     * Imposta il valore della proprietà first.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirst(String value) {
        this.first = value;
    }

    /**
     * Recupera il valore della proprietà last.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLast() {
        return last;
    }

    /**
     * Imposta il valore della proprietà last.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLast(String value) {
        this.last = value;
    }

    /**
     * Recupera il valore della proprietà publisher.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Imposta il valore della proprietà publisher.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublisher(String value) {
        this.publisher = value;
    }

    /**
     * Recupera il valore della proprietà city.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Imposta il valore della proprietà city.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Recupera il valore della proprietà db.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDb() {
        return db;
    }

    /**
     * Imposta il valore della proprietà db.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDb(String value) {
        this.db = value;
    }

    /**
     * Recupera il valore della proprietà number.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Imposta il valore della proprietà number.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Recupera il valore della proprietà institute.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstitute() {
        return institute;
    }

    /**
     * Imposta il valore della proprietà institute.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstitute(String value) {
        this.institute = value;
    }

    /**
     * Recupera il valore della proprietà country.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Imposta il valore della proprietà country.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }


    public Vertex save(OrientGraphNoTx graph) {
    	Vertex ref = null;
    	
    	ArrayList<Vertex> authorsPerson = new ArrayList<Vertex>();
    	ArrayList<Vertex> authorsConsortium = new ArrayList<Vertex>();
    	
    	if ((authorList != null) && (!authorList.getConsortiumOrPerson().isEmpty())) {
    		for (Object consortiumOrPerson: authorList.getConsortiumOrPerson()) {
    			if (consortiumOrPerson instanceof PersonType) {
    				Vertex author = ((PersonType)consortiumOrPerson).save(graph);
    				authorsPerson.add(author);
    			}
    			if (consortiumOrPerson instanceof ConsortiumType) {
    				Vertex author = ((ConsortiumType)consortiumOrPerson).save(graph);
    				authorsConsortium.add(author);
    			}
    		}
    	}
    	
		String dateSt = (date != null) ? date : "";

		switch (type) {
    		case "thesis":
    			if (title != null) {
    				Vertex thesis = null;
    				Iterator<Vertex> i = graph.getVertices("Thesis.title", title).iterator();
    				
    				if (i.hasNext())
    					thesis = i.next();
    				else {
    					thesis = graph.addVertex("class:Thesis",
    							"title", title
    							);
    					
    					if (institute != null) {
    						Vertex inst = null;
    						Iterator<Vertex> it = graph.getVertices("Institute.name", institute).iterator();
    						
    						if (it.hasNext())
    							inst = it.next();
    						else {
    							inst = graph.addVertex("class:Institute",
    									"name", institute
    									);
    						}
    						
    						if (country != null) {
    							Vertex cou = null;
    							Iterator<Vertex> itc = graph.getVertices("Country.name", country).iterator();
    							
    							if (itc.hasNext())
    								cou = itc.next();
    							else {
    								cou = graph.addVertex("class:Country",
    										"name", country
    										);
    							}
    							
    							graph.addEdge("class:locatedIn", inst, cou, "locatedIn");
    						}
    						
    						graph.addEdge("class:hasInstitute", thesis, inst, "hasInstitute");
    					}
    				}
    				        		
    				ref = graph.addVertex("class:Reference",
							"date", dateSt
							);
    				
    				graph.addEdge("class:isThesis", ref, thesis, "isThesis");
    				
    				for (Vertex person: authorsPerson)
    					graph.addEdge("class:isAuthor", person, ref, "isAuthor");
    			}
    			break;
    		case "patent":
    			if (number != null) {
    				Vertex pat = null;
    				Iterator<Vertex> i = graph.getVertices("Patent.number", number).iterator();
    				
    				if (i.hasNext())
    					pat = i.next();
    				else {
    					String titleSt = (title != null) ? title : "";
    					
    					pat = graph.addVertex("class:Patent",
    							"number", number,
    							"title", titleSt
    							);
    				}
    				
    				ref = graph.addVertex("class:Reference",
							"date", dateSt
							);
    				
    				graph.addEdge("class:isPatent", ref, pat, "isPatent");
    				
    				for (Vertex person: authorsPerson)
    					graph.addEdge("class:isAuthor", person, ref, "isAuthor");
    			}
    			break;
    		case "submission":
    			if (title != null) {
    				Vertex submission = null;
    				Iterator<Vertex> i = graph.getVertices("Submission.title", title).iterator();
    				
    				if (i.hasNext())
    					submission = i.next();
    				else {
    					submission = graph.addVertex("class:Submission",
    							"title", title
    							);
    					
    					if (db != null) {
    						Vertex dbname = null;
    						Iterator<Vertex> it = graph.getVertices("DBName.name", db).iterator();
    						
    						if (it.hasNext())
    							dbname = it.next();
    						else {
    							dbname = graph.addVertex("class:DBName",
    									"name", db
    									);
    						}
    						
    						graph.addEdge("class:isInDB", submission, dbname, "isInDB");
    					}
    				}
    				
    				ref = graph.addVertex("class:Reference",
							"date", dateSt
							);
    				
    				graph.addEdge("class:isSubmission", ref, submission, "isSubmission");
    				
    				for (Vertex person: authorsPerson)
    					graph.addEdge("class:isAuthor", person, ref, "isAuthor");

    				for (Vertex consortium: authorsConsortium)
    					graph.addEdge("class:isAuthor", consortium, ref, "isAuthor");
    			}
    			break;
    		case "book":
    			if (name != null) {
    				Vertex book = null;
    				Iterator<Vertex> i = graph.getVertices("Book.name", name).iterator();
    				
    				if (i.hasNext())
    					book = i.next();
    				else {
    					book = graph.addVertex("class:Book",
    							"name", name
    							);
    				}
    				
    				ref = graph.addVertex("class:Reference",
							"date", dateSt
							);
    				
    				graph.addEdge("class:isBook", ref, book, "isBook");
    				
    				for (Vertex person: authorsPerson)
    					graph.addEdge("class:isAuthor", person, ref, "isAuthor");
    				
    				if ((editorList != null) && (!editorList.getConsortiumOrPerson().isEmpty())) {
    					for (Object consortiumOrPerson: editorList.getConsortiumOrPerson()) {
    		    			if (consortiumOrPerson instanceof PersonType) {
    		    				Vertex editor = ((PersonType)consortiumOrPerson).save(graph);
    		    				graph.addEdge("class:editedBy", book, editor, "editedBy");
    		    			}
    		    		}
    				}
    				
    				if (publisher != null) {
    					Vertex pub = null;
    					Iterator<Vertex> it = graph.getVertices("Publisher.name", publisher).iterator();
    					
    					if (it.hasNext())
    						pub = it.next();
    					else {
    						pub = graph.addVertex("class:Publisher",
    								"name", publisher
    								);
    					}
    					
    					graph.addEdge("class:publishedBy", book, pub, "publishedBy");
    				}
    				
    				if (city != null) {
    					Vertex cit = null;
    					Iterator<Vertex> it = graph.getVertices("City.name", city).iterator();
    					
    					if (it.hasNext())
    						cit = it.next();
    					else {
    						cit = graph.addVertex("class:City",
    								"name", city
    								);
    					}
    					
    					graph.addEdge("class:publishedIn", book, cit, "publishedIn");
    				}
    			}
    			break;
    		case "online journal article":
    			if (title != null) {
    				Vertex article = null;
    				Iterator<Vertex> i = graph.getVertices("OnlineArticle.title", title).iterator();
    				
    				if (i.hasNext())
    					article = i.next();
    				else {
    					article = graph.addVertex("class:OnlineArticle",
    							"title", title
    							);
    				}
    				
    				ref = graph.addVertex("class:Reference",
							"date", dateSt
							);
    				
    				graph.addEdge("class:isOnlineArticle", ref, article, "isOnlineArticle");

    				for (Vertex person: authorsPerson)
    					graph.addEdge("class:isAuthor", person, ref, "isAuthor");

    				for (Vertex consortium: authorsConsortium)
    					graph.addEdge("class:isAuthor", consortium, ref, "isAuthor");
    				
    				if (name != null) {
    					Vertex journal = null;
    					Iterator<Vertex> it = graph.getVertices("OnlineJournal.name", name).iterator();
    					
    					if (it.hasNext())
    						journal = it.next();
    					else {
    						journal = graph.addVertex("class:OnlineJournal",
    								"name", name
    								);
    					}
    					
    					graph.addEdge("class:inOnlineJournal", ref, journal, "inOnlineJournal");
    				}
    			}
    			break;
    		case "journal article":
    			if (title != null) {    				
        			String doiSt = "";
        			//TODO: MEDLINE
        	        //String medlineSt = "";
        	        String pubmedId = "";
        	        
        	        for (DbReferenceType dbref: getDbReference()) {
        	        	switch (dbref.getType()) {
        	        		case "DOI":
        	        			doiSt = dbref.getId();
        	        			break;
        	        		case "MEDLINE":
        	        			//medlineSt = dbref.getId();
        	        			break;
        	        		case "PubMed":
        	        			pubmedId = dbref.getId();
        	        			break;
        	        	}
        	        }
        	        
    				Vertex article = null;
    				Iterator<Vertex> i = graph.getVertices("Article.title", title).iterator();
    				
    				if (i.hasNext())
    					article = i.next();
    				else {
    					article = graph.addVertex("class:Article",
    							"title", title,
    							"doiId", doiSt
    							);
    				}

    				if (!pubmedId.equals("")) {
    					Vertex pubmed = null;
    					Iterator<Vertex> it = graph.getVertices("PubMed.id", pubmedId).iterator();
    					
    					if (it.hasNext())
    						pubmed = it.next();
    					else {
    						pubmed = graph.addVertex("class:PubMed",
    								"id", pubmedId
    								);
    					}
    					
    					graph.addEdge("class:isPubMedArticle", article, pubmed, "isPubMedArticle");
    				}
    				
    				ref = graph.addVertex("class:Reference",
							"date", dateSt
							);
    				
    				graph.addEdge("class:isArticle", ref, article, "isArticle");
    				
    				for (Vertex person: authorsPerson)
    					graph.addEdge("class:isAuthor", person, ref, "isAuthor");

    				for (Vertex consortium: authorsConsortium)
    					graph.addEdge("class:isAuthor", consortium, ref, "isAuthor");
    				
    				if (name != null) {
    					Vertex journal = null;
    					Iterator<Vertex> it = graph.getVertices("Journal.name", name).iterator();
    					
    					if (it.hasNext())
    						journal = it.next();
    					else {
    						journal = graph.addVertex("class:Journal",
    								"name", name
    								);
    					}
    					
            			String firstSt = (first != null) ? first : "";
            			String lastSt = (last != null) ? last : "";
            			String volumeSt = (volume != null) ? volume : "";

    					Edge inJournal = graph.addEdge("class:inJournal", article, journal, "inJournal");
    					inJournal.setProperty("volume", volumeSt);
    					inJournal.setProperty("first", firstSt);
    					inJournal.setProperty("last", lastSt);
    				}
    			}
    			break;
    	}
    	
    	return ref;
    }

}
