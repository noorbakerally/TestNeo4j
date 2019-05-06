package OntologyDescription;

import Ontology.OntologyUtils;
import Utils.Utils;
import Word.Keyword;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ResourceDescription implements OntologyEntityDescription {
    String iri;
    String ontologyIRI;
    Set<Keyword> labelKeywords;
    Set <Keyword> commentKeywords;

    //not to store
    Set <Keyword> valueKeywords; //for instances


    Map<String,Set <Keyword>> commentKeywordsForProperty; //do not store

    public ResourceDescription(String iri,String ontologyIRI) {
        this.iri = iri;
        this.ontologyIRI = ontologyIRI;
        this.labelKeywords = new HashSet<Keyword>();
        this.commentKeywords = new HashSet<Keyword>();
        this.valueKeywords = new HashSet<Keyword>();

        commentKeywordsForProperty = new HashMap<String,Set <Keyword>>();
    }

    public void addLabelKeywords(Set <Keyword> keywords){
        Utils.addUniqueKeywords(keywords,labelKeywords);
    }
    public void addCommentKeywords(Set <Keyword> keywords){
        Utils.addUniqueKeywords(keywords,commentKeywords);
    }


    public void addCommentKeywordsForProperty(Set<Keyword> commentKeywords, String property) {
        commentKeywordsForProperty.put(property,commentKeywords);
    }


    public void selectCommentKeywords() {
        OntologyUtils.selectCommentKeywords(commentKeywordsForProperty,this.commentKeywords);
    }

    @Override
    public void finalize() {
        selectCommentKeywords();
        if (getLabelKeywords().size()==0){
            String input = getIRI();
            Set<Keyword> labelKeywords = Utils.getKeywordsFromURL(input);
            setLabelKeywords(labelKeywords);
        }
    }


    public String toString(){
        String description="";
        description += "Class:"+this.iri+"\n";
        description += StringUtils.repeat("=",description.length()) +"\n";
        description += "\t\tOntologyIRI:"+this.ontologyIRI+"\n";

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

    public void addLabelKeyword(String keyword){
        labelKeywords.add(new Keyword(keyword));
    }
    public void addCommentKeyword(String keyword){
        commentKeywords.add(new Keyword(keyword));
    }


    public void setOntologyDescription(String ontologyIRI) {
        this.ontologyIRI = ontologyIRI;
    }
    public String getOntology() {return ontologyIRI;}
    public String getIRI() {
        return iri;
    }
    public void setIRI(String iri) {
        this.iri = iri;
    }
    public String getOntologyIRI() {
        return ontologyIRI;
    }
    public void setOntologyIRI(String ontologyIRI) {
        this.ontologyIRI = ontologyIRI;
    }
    public void print(){
        System.out.println(this.toString());
    }
    public Set<Keyword> getLabelKeywords() {
        return labelKeywords;
    }
    public void setLabelKeywords(Set<Keyword> labelKeywords) {
        this.labelKeywords = labelKeywords;
    }
    public Set<Keyword> getCommentKeywords() {
        return commentKeywords;
    }
    public void setCommentKeywords(Set<Keyword> commentKeywords) {
        this.commentKeywords = commentKeywords;
    }
    public Set<Keyword> getValueKeywords() {
        return valueKeywords;
    }
    public void setValueKeywords(Set<Keyword> valueKeywords) {
        this.valueKeywords = valueKeywords;
    }

}
