package OntologyDescription;

import Ontology.OntologyRepository;

import java.util.Map;

//not to store
public class OntologyRepositoryDescription {
    OntologyRepository ontologyRepository;
    Map<String, OntologyDescription> ontologyDescriptionMap;

    public String getDescription(){
        String description = "";
        description = ontologyRepository.getDescription();
        return description;
    }

}
