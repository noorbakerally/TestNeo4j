package Ontology;

import OntologyDescription.DataPropertyDescription;
import OntologyDescription.ObjectPropertyDescription;
import OntologyDescription.OntologyDescription;
import OntologyDescription.ResourceDescription;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.IOException;
import java.util.Set;

public class Ontology implements OntologyEntitySource {
    static OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
    static OWLDataFactory df;

    String name;
    String iri;
    String sourcePath;
    Model graph;
    OWLOntology ontology;
    OntologyDescription ontologyDescription;

    public Ontology(String name){
        this.name = name;
    }
    public Ontology(String name,String iri) throws OWLOntologyCreationException {
        this.name = name;
        this.iri = iri;
        this.ontology = loadasOWLOntology(iri);
        this.graph = loadAsRDFGraph(iri);
        this.sourcePath = this.iri;
    }
    public Ontology(String name,String iri, String sourcePath) throws OWLOntologyCreationException {
        this.name = name;
        this.iri = iri;
        this.ontology = loadasOWLOntology(iri);
        this.graph = loadAsRDFGraph(iri);
        this.sourcePath = sourcePath;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public OWLOntology getOWLOntology(){
        return ontology;
    }

    public Model getRdfGraph (){
        return graph;
    }



    public OWLOntology loadasOWLOntology(String ontologyStrIRI) throws OWLOntologyCreationException {
        IRI ontologyIRI = IRI.create(ontologyStrIRI);
        this.ontology = ontologyManager.loadOntology(ontologyIRI);
        //ontology = ontologyManager.loadOntologyFromOntologyDocument(ontologyIRI);

        //RDFXMLParserFactory parser = new RDFXMLParserFactory();
        //parser.createParser().
        return this.ontology;
    }

    public static Model loadAsRDFGraph(String ontologyStrIRI){

        Model model = ModelFactory.createDefaultModel() ;
        model.read(ontologyStrIRI) ;
        return model;
    }


    public String getType() {
        return "Ontology";
    }

    public String getName() {
        return this.name;
    }

    public String getIRI() {
        return this.iri;
    }

    @Override
    public String getDescription() {
        String description = "";
        if (this.ontologyDescription != null){
            return this.ontologyDescription.toString();
        }
        try {
            description = getOntologyDescription().toString();
                   } catch (IOException e) {
            e.printStackTrace();
        }
        return description;
    }

    public OntologyDescription getOntologyDescription() throws IOException {
        if (this.ontologyDescription != null){
            return this.ontologyDescription;
        }
        //ontologyDescription = getOnlyOntologyDescription(ontologyDescription);
        this.ontologyDescription = OntologyUtils.getOntologyDescription(this);

        Set <OWLClass> classes = ontology.getClassesInSignature();
        for (OWLClass cclass:classes){
            String classIRI = cclass.getIRI().getIRIString();
            ResourceDescription resourceDescription = OntologyUtils.getClassDescription(classIRI,this);
            this.ontologyDescription.getClassDescriptionsMap().put(classIRI, resourceDescription);
        }

        Set<OWLObjectProperty> objectProperties = ontology.getObjectPropertiesInSignature();
        for (OWLObjectProperty objectProperty:objectProperties){
            String objectPropertyIRI = objectProperty.getIRI().getIRIString();
            ObjectPropertyDescription resourceDescription = OntologyUtils.getObjectPropertyDescription(objectPropertyIRI,this);
            this.ontologyDescription.getObjectPropertyDescriptionsMap().put(objectPropertyIRI, resourceDescription);
        }

        Set<OWLDataProperty> dataProperties = ontology.getDataPropertiesInSignature();
        for (OWLDataProperty dataProperty:dataProperties){
            String dataPropertyIRI = dataProperty.getIRI().getIRIString();
            DataPropertyDescription resourceDescription = OntologyUtils.getDataPropertyDescription(dataPropertyIRI,
                    this);
            this.ontologyDescription.getDataPropertyDescriptionsMap().put(dataPropertyIRI, resourceDescription);
        }
        this.ontologyDescription = ontologyDescription;

        return ontologyDescription;
    }



    @Override
    public String toString() {
        String value="";
        try {
            value = getOntologyDescription().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public void print(){
        System.out.println(this.toString());
    }

    public Model getGraph() {
        return graph;
    }

    public void setGraph(Model graph) {
        this.graph = graph;
    }

    public void setOWLOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }

}
