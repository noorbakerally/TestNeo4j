package OntologyDescription;

import Word.Keyword;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@NodeEntity(label="DataProperty")
public class DataPropertyDescription extends ObjectPropertyDescription {

    //not to store
    Map<String,LiteralDescription> literalDescriptionMap;

    public DataPropertyDescription(String iri,String ontologyIRI) {
        super(iri,ontologyIRI);
        literalDescriptionMap = new HashMap<String, LiteralDescription>();
    }


    public void addDomain(String iri){
        domains.add(iri);
    }

    public String getOntologyIRI() {
        return ontologyIRI;
    }

    public void setOntologyIRI(String ontologyIRI) {
        this.ontologyIRI = ontologyIRI;
    }

    public Set<String> getDomains() {
        return domains;
    }

    public void setDomains(Set<String> domains) {
        this.domains = domains;
    }

    @Override
    public String toString(){
        String description="";
        description += "DataProperty:"+this.iri+"\n";
        description += StringUtils.repeat("=",description.length()) +"\n";
        description += "\t\tOntologyIRI:"+this.ontologyIRI+"\n";

        description += "\t\tDomains:\n";
        for (String domain:this.domains){
            description += "\t\t\t"+domain+"\n";
        }

        description += "\t\tRanges:\n";
        for (String range:this.ranges){
            description += "\t\t\t"+range+"\n";
        }

        description += "\t\tLabel Keywords:\n";
        for (Keyword labelKeyword:this.labelKeywords){
            description += "\t\t\t"+labelKeyword+"\n";
        }

        description += "\t\tComment Keywords:\n";
        for (Keyword commentKeyword:this.commentKeywords){
            description += "\t\t\t"+commentKeyword+"\n";
        }

        return description;
    }

    public void print(){
        System.out.println(this.toString());
    }
}
