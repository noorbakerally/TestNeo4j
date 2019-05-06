package OntologyDescription;

import Ontology.Ontology;
import Ontology.OntologyUtils;
import Utils.Utils;
import Word.Keyword;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.*;

import java.util.*;

@NodeEntity(label="Ontology")
public class OntologyDescription implements OntologyEntityDescription {

    Ontology ontology;

    @Property(name="labelKeywords")
    Set<Keyword> labelKeywords;

    @Property(name="commentKeywords")
    Set <Keyword> commentKeywords;


    Map <String,ResourceDescription> classDescriptionsMap;

    Map <String, ObjectPropertyDescription> objectPropertyDescriptionsMap;


    Map <String, DataPropertyDescription> dataPropertyDescriptionsMap;

    //not to store
    Map <String,Set <Keyword>> commentKeywordsForProperty;

    public OntologyDescription(Ontology ontology){
        this.ontology = ontology;
        labelKeywords = new HashSet<Keyword>();
        commentKeywords = new HashSet<Keyword>();

        classDescriptionsMap = new HashMap<String, ResourceDescription>();
        objectPropertyDescriptionsMap = new HashMap<String, ObjectPropertyDescription>();
        dataPropertyDescriptionsMap = new HashMap<String, DataPropertyDescription>();

        commentKeywordsForProperty = new HashMap<String,Set <Keyword>>();

    }

    public boolean isClass(String entityIRI){
        if (classDescriptionsMap.containsKey(entityIRI)){ return true;}
        return false;
    }

    public boolean isDataProperty(String entityIRI){
        if (dataPropertyDescriptionsMap.containsKey(entityIRI)){ return true;}
        return false;
    }


    public ResourceDescription getClassDescription(String classIRI){
        if (classDescriptionsMap.containsKey(classIRI)){
            return classDescriptionsMap.get(classIRI);
        }
        return null;
    }
    public DataPropertyDescription getDataPropertyDescription(String dataPropertyIRI){
        if (dataPropertyDescriptionsMap.containsKey(dataPropertyIRI)){
            return dataPropertyDescriptionsMap.get(dataPropertyIRI);
        }
        return null;
    }


    public String getIRI() {
        return getOntologyIRI();
    }


    public String getOntologyIRI() {
        return ontology.getIRI();
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


    public void addLabelKeywords(Set<Keyword> keywords) {
        Utils.addUniqueKeywords(keywords,labelKeywords);
    }


    public void addCommentKeywords(Set<Keyword> keywords) {
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
    }

    public void setCommentKeywords(Set<Keyword> commentKeywords) {
        this.commentKeywords = commentKeywords;
    }

    public Map<String, ResourceDescription> getClassDescriptionsMap() {
        return classDescriptionsMap;
    }

    public void setClassDescriptionsMap(Map<String, ResourceDescription> classDescriptionsMap) {
        this.classDescriptionsMap = classDescriptionsMap;
    }

    public Map<String, ObjectPropertyDescription> getObjectPropertyDescriptionsMap() {
        return objectPropertyDescriptionsMap;
    }

    public void setObjectPropertyDescriptionsMap(Map<String, ObjectPropertyDescription> objectPropertyDescriptionsMap) {
        this.objectPropertyDescriptionsMap = objectPropertyDescriptionsMap;
    }

    public Map<String, DataPropertyDescription> getDataPropertyDescriptionsMap() {
        return dataPropertyDescriptionsMap;
    }

    public void setDataPropertyDescriptionsMap(Map<String, DataPropertyDescription> dataPropertyDescriptionsMap) {
        this.dataPropertyDescriptionsMap = dataPropertyDescriptionsMap;
    }
    public String toString(){
        String description="";
        description += "Ontology:"+ontology.getIRI()+"\n";
        description += StringUtils.repeat("=",description.length()) +"\n";


        description += "\t\tLabel Keywords:\n";
        for (Keyword labelKeyword:this.labelKeywords){
            description += "\t\t\t"+labelKeyword+"\n";
        }

        description += "\t\tComment Keywords:\n";
        for (Keyword commentKeyword:this.commentKeywords){
            description += "\t\t\t"+commentKeyword+"\n";
        }


        Iterator<Map.Entry<String, ResourceDescription>> iterClassDescription = classDescriptionsMap.entrySet().iterator();
        while (iterClassDescription.hasNext()){
            description += iterClassDescription.next().getValue().toString();
        }

        Iterator<Map.Entry<String, ObjectPropertyDescription>> iterObjectPropertyDescription =
                objectPropertyDescriptionsMap.entrySet().iterator();
        while (iterObjectPropertyDescription.hasNext()){
            description += iterObjectPropertyDescription.next().getValue().toString();
        }


        Iterator<Map.Entry<String, DataPropertyDescription>> iterDataPropertyDescription =
                dataPropertyDescriptionsMap.entrySet().iterator();
        while (iterDataPropertyDescription.hasNext()){
            description += iterDataPropertyDescription.next().getValue().toString();
        }


        return description;
    }

    public void print(){
        System.out.println(this.toString());
    }
}
