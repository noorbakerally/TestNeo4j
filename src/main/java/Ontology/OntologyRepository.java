package Ontology;

import OntologyDescription.OntologyRepositoryDescription;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.util.HashMap;
import java.util.Map;

public class OntologyRepository implements OntologyEntitySource {
    Map<String,Ontology> ontologies;

    String name;
    String iri;
    OntologyRepositoryDescription ontologyRepositoryDescription;
    public OntologyRepository(String name){
        this.name = name;
        ontologies = new HashMap<String, Ontology>();
    }
    public void addOntology(Ontology ontology){
        ontologies.put(ontology.getIRI(),ontology);
    }

    public void addOntology(String iri, Ontology ontology){
        ontologies.put(iri,ontology);
    }



    public void addOntology(String name, String iri) throws OWLOntologyCreationException {
        Ontology temp = new Ontology(name,iri);
        ontologies.put(iri,temp);
    }

    public Map <String, Ontology> getOntologies() {
        return ontologies;
    }

    public String getType() {
        return "Ontology repository";
    }

    public String getName() {
        return this.name;
    }

    public String getIRI() {
        return this.iri;
    }

    public String getDescription() {
        String description = "";
        for (Ontology ontology:ontologies.values()){
            description += ontology.getDescription();
        }
        return description;
    }
}
