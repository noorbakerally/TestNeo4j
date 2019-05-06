package OntologyDescription;

import Word.Keyword;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity(label="ObjectProperty")
public class ObjectPropertyDescription extends ResourceDescription {

    @Relationship(type = "Class")
    Set<String> domains;

    @Property(name="Class")
    Set<String> ranges;


    public ObjectPropertyDescription(String iri,String ontologyIRI){
        super(iri,ontologyIRI);
        domains = new HashSet<String>();
        ranges = new HashSet<String>();
    }
    public void addDomain(String iri){
        domains.add(iri);
    }

    public void addRange(String iri){
        ranges.add(iri);
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

    public Set<String> getRanges() {
        return ranges;
    }

    public void setRanges(Set<String> ranges) {
        this.ranges = ranges;
    }

    public String toString(){
        String description="";
        description += "ObjectProperty:"+this.iri+"\n";
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
