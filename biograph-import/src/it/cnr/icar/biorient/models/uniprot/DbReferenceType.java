//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2015.12.01 alle 07:40:41 AM CET 
//


package it.cnr.icar.biorient.models.uniprot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;


/**
 * Describes a database cross-reference.
 *             Equivalent to the flat file DR-line.
 *             
 * 
 * <p>Classe Java per dbReferenceType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="dbReferenceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="molecule" type="{http://uniprot.org/uniprot}moleculeType" minOccurs="0"/>
 *         &lt;element name="property" type="{http://uniprot.org/uniprot}propertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="evidence" type="{http://uniprot.org/uniprot}intListType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dbReferenceType", propOrder = {
    "molecule",
    "property"
})
public class DbReferenceType {

    protected MoleculeType molecule;
    protected List<PropertyType> property;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "evidence")
    protected List<Integer> evidence;

    /**
     * Recupera il valore della proprietà molecule.
     * 
     * @return
     *     possible object is
     *     {@link MoleculeType }
     *     
     */
    public MoleculeType getMolecule() {
        return molecule;
    }

    /**
     * Imposta il valore della proprietà molecule.
     * 
     * @param value
     *     allowed object is
     *     {@link MoleculeType }
     *     
     */
    public void setMolecule(MoleculeType value) {
        this.molecule = value;
    }

    /**
     * Gets the value of the property property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the property property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProperty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PropertyType }
     * 
     * 
     */
    public List<PropertyType> getProperty() {
        if (property == null) {
            property = new ArrayList<PropertyType>();
        }
        return this.property;
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
     * Recupera il valore della proprietà id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Imposta il valore della proprietà id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the evidence property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the evidence property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEvidence().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getEvidence() {
        if (evidence == null) {
            evidence = new ArrayList<Integer>();
        }
        return this.evidence;
    }

    public Vertex save(OrientGraphNoTx graph) {
    	Vertex ref = null;
    	Iterator<Vertex> i = null;
    	
    	if (id == null)
    		return null;
    	
    	switch (type) {
    		case "Ensembl":
    			i = graph.getVertices("Ensemble.id", id).iterator();
    			if (i.hasNext())
    				ref = i.next();
    			else {
    				String moleculeIdSt = "";
    				String proteinSequenceIdSt = "";
    				String geneIdSt = "";
    				
    				for (PropertyType prop: property) {
    					if (prop.getType().equals("protein sequence ID"))
    						proteinSequenceIdSt = prop.getValue();
    					if (prop.getType().equals("gene ID"))
    						geneIdSt = prop.getValue();
    				}
    				
    				if (molecule != null) {
    					moleculeIdSt = molecule.getId();
    				}
    				
    				ref = graph.addVertex("class:Ensemble",
    						"id", id,
    						"proteinSequenceId", proteinSequenceIdSt,
    						"moleculeId", moleculeIdSt,
    						"geneId", geneIdSt
    						);
    			}
    			break;
    		case "PIR":
    			i = graph.getVertices("PIR.id", id).iterator();
    			if (i.hasNext())
    				ref = i.next();
    			else {
    				String entryNameSt = "";
    				
    				for (PropertyType prop: property) {
    					if (prop.getType().equals("entry name"))
    						entryNameSt = prop.getValue();
    				}
    				
    				ref = graph.addVertex("class:PIR",
    						"id", id,
    						"entryName", entryNameSt
    						);
    			}
    			break;
    		case "UniGene":
    			i = graph.getVertices("UniGene.id", id).iterator();
    			if (i.hasNext())
    				ref = i.next();
    			else {
    				ref = graph.addVertex("class:UniGene",
    						"id", id
    						);    				
    			}
    			break;
    		case "KEGG":
    			i = graph.getVertices("KEGG.id", id).iterator();
    			if (i.hasNext())
    				ref = i.next();
    			else {
    				ref = graph.addVertex("class:KEGG",
    						"id", id
    						);    				
    			}
    			break;
    		case "EMBL":
    			i = graph.getVertices("EMBL.id", id).iterator();
    			if (i.hasNext())
    				ref = i.next();
    			else {
    				String moleculeTypeSt = "";
    				String proteinSequenceIdSt = "";

    				for (PropertyType prop: property) {
    					if (prop.getType().equals("protein sequence ID"))
    						proteinSequenceIdSt = prop.getValue();
    					if (prop.getType().equals("molecule type"))
    						moleculeTypeSt = prop.getValue();
    				}

    				ref = graph.addVertex("class:EMBL",
    						"id", id,
    						"proteinSequenceId", proteinSequenceIdSt,
    						"moleculeType", moleculeTypeSt
    						);    				
    			}
    			break;
    		case "RefSeq":
    			i = graph.getVertices("RefSeq.id", id).iterator();
    			if (i.hasNext())
    				ref = i.next();
    			else {
    				String nucleotideSequenceIdSt = "";
    				
    				for (PropertyType prop: property) {
    					if (prop.getType().equals("nucleotide sequence ID"))
    						nucleotideSequenceIdSt = prop.getValue();
    				}
    				
    				ref = graph.addVertex("class:RefSeq",
    						"id", id,
    						"nucleotideSequenceId", nucleotideSequenceIdSt
    						);    				
    			}
    			break;
    		case "Reactome":
    			i = graph.getVertices("Reactome.id", id).iterator();
    			if (i.hasNext())
    				ref = i.next();
    			else {
	    			String pathwayName = "";

    				for (PropertyType prop: property) {
    					if (prop.getType().equals("pathway name"))
    						pathwayName = prop.getValue();
    				}
    				
    				ref = graph.addVertex("class:Reactome",
    						"id", id,
    						"pathwayName", pathwayName
    						);    				
    			}
    			break;
    	}
    	
    	return ref;
    }
}
