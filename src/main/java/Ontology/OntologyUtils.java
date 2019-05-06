package Ontology;

import OntologyDescription.*;
import RawInput.DataDescription;
import Utils.Global;
import Utils.Utils;
import Word.Keyword;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class OntologyUtils {

    static OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
    static OWLDataFactory df;

    public static OWLOntology loadOntology(String ontologyStrIRI) throws OWLOntologyCreationException {
        OWLOntology ontology;
        IRI ontologyIRI = IRI.create(ontologyStrIRI);
        ontology = ontologyManager.loadOntology(ontologyIRI);
        //ontology = ontologyManager.loadOntologyFromOntologyDocument(ontologyIRI);

        //RDFXMLParserFactory parser = new RDFXMLParserFactory();
        //parser.createParser().
        return ontology;
    }

    public static Model getSimpleOntologyModel(String sourceFile){

        Model model = ModelFactory.createDefaultModel() ;
        model.read(sourceFile) ;

        return model;
    }

    public static Model getSimpleOntologyModel(Model graph, DataDescription dataDescription) throws OWLOntologyCreationException {
        Model ontologyGraph = ModelFactory.createOntologyModel();

        //get classes from raw RDF graph and add to ontology
        String classQuery = "SELECT ?class WHERE { _:x a ?class .}";
        Query query = QueryFactory.create(classQuery) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, graph);
        ResultSet results = qexec.execSelect() ;

        //add classes to ontology
        while (results.hasNext()){

            //get current value of class variable
            QuerySolution sol = results.nextSolution();
            Resource classRes = sol.getResource("class");

            //create OWL class
            ontologyGraph.add(classRes, RDF.type, OWL.Class);
            ontologyGraph.add(classRes,RDFS.label, Global.IriToLabelMapping.get(classRes.getURI()));

            //add comments from keywords in description
            String commentType="";

            for (String keyword:dataDescription.getKeywords()){
                commentType+=keyword+" ";
            }

            //add class to ontology
            ontologyGraph.add(classRes, RDFS.comment, commentType);


        }

        //get data properties and add to ontology
        String dataPropertyQuery = "SELECT DISTINCT ?dataProperty WHERE { _:x ?dataProperty _:y .}";
        query = QueryFactory.create(dataPropertyQuery) ;
        qexec = QueryExecutionFactory.create(query, graph);
        results = qexec.execSelect() ;
        while (results.hasNext()){

            //get current value of dataProperty variable
            QuerySolution sol = results.nextSolution() ;
            Resource dataPropertyRes = sol.getResource("dataProperty");

            //skip type, already processed before
            if (dataPropertyRes.getURI().equals(RDF.type.getURI())) {
                continue;
            }

            //add data property to ontology
            ontologyGraph.add(dataPropertyRes,RDF.type, OWL.DatatypeProperty);

            //add label of data property
            ontologyGraph.add(dataPropertyRes,RDFS.label, Global.IriToLabelMapping.get(dataPropertyRes.getURI()));

            /*add comment of data property using keywords from data description*/
            //get field from IRI
            String fieldName = Global.IriToLabelMapping.get(dataPropertyRes.getURI());
            String commentField="";

            //get field keywords
            if (dataDescription.getFields().containsKey(fieldName)){
                for (String keyword:dataDescription.getFields().get(fieldName)){
                    commentField+=keyword+" ";
                }
            }

            ontologyGraph.add(dataPropertyRes,RDFS.comment, commentField);



        }

        //define the ontology
        ontologyGraph.add(ResourceFactory.createResource(Global.exampleNS),RDF.type,OWL.Ontology);

        return ontologyGraph;
    }

    public static OWLOntology getSimpleOWLOntology(Model graph) throws OWLOntologyCreationException {

        OWLOntology ontology = ontologyManager.
                loadOntologyFromOntologyDocument
                        (new ByteArrayInputStream(RdfToTurtle(graph).getBytes()));


        return ontology;
    }

    public static String RdfToTurtle(Model ontologyGraph){
        StringWriter out = new StringWriter();
        ontologyGraph.write(out,"TTL");
        return out.toString();
    }


    public static String OwlToTurtle (OWLOntology exOWL) throws OWLOntologyStorageException, IOException {
        String OwlTurtle = "";


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        //saving the ontology to the output stream
        TurtleDocumentFormat turtleFormat = new TurtleDocumentFormat();

        //to be updated
        //ontologyManager.saveOntology(exOWL, turtleFormat, outputStream);


        //convert the ontology to string
        OwlTurtle = new String(outputStream.toByteArray());

        return OwlTurtle;
    }

    public static ResourceDescription getClassDescription(String classIRI, Ontology ontology){
        /*CLASS Description*/
        //get all classes
        //for each class, get their label and comments
        //remove stop words, blank spaces, stem words
        ResourceDescription currentResourceDescription = new ResourceDescription(classIRI,ontology.getIRI());

        OWLClass classAxiom = ontologyManager.getOWLDataFactory().getOWLClass(classIRI);
        Stream<OWLAnnotationAssertionAxiom> annotations = EntitySearcher.getAnnotationAssertionAxioms(classAxiom,
        ontology.ontology);
        processAnnotations(annotations,currentResourceDescription);

        return currentResourceDescription;
    }

    public static ObjectPropertyDescription getObjectPropertyDescription(String objectPropertyIRI, Ontology ontology){

        //*OBJECT PROPERTY Description*//*
        //get all object properties
        //for each class, get their label and comments
        //remove stop words, blank spaces, stem words

        ObjectPropertyDescription objectPropertyDescription = new ObjectPropertyDescription(objectPropertyIRI,
                ontology.getIRI());

        //process annotations
        OWLObjectProperty objectPropertyAxiom =
                ontologyManager.getOWLDataFactory().getOWLObjectProperty(objectPropertyIRI);
        Stream<OWLAnnotationAssertionAxiom> annotations = EntitySearcher.getAnnotationAssertionAxioms(objectPropertyAxiom,
                ontology.ontology);
        processAnnotations(annotations,objectPropertyDescription);


        //get domain of object property
        Stream<OWLObjectPropertyDomainAxiom> domains =
                ontology.getOWLOntology().objectPropertyDomainAxioms(objectPropertyAxiom);
        domains.forEach(domainClass -> {
            objectPropertyDescription.addDomain(getDLExpression(domainClass.getDomain()));

        });

        //get ranges of the object property
        Stream<OWLObjectPropertyRangeAxiom> ranges = ontology.getOWLOntology().objectPropertyRangeAxioms(objectPropertyAxiom);
        ranges.forEach(rangeClass -> {
            objectPropertyDescription.addRange(getDLExpression(rangeClass.getRange()));
        });
        return objectPropertyDescription;
    }

    public static OntologyDescription getOntologyDescription(Ontology ontology) throws IOException {
        OntologyDescription ontologyDescription = new OntologyDescription(ontology);

        Map <String,Set <Keyword>> keywords = new HashMap<String,Set <Keyword>>();
        //process annotations
        for (OWLAnnotation annotation:ontology.getOWLOntology().getAnnotations()){
            processAnnotation(annotation,ontologyDescription);
        }

        //finalize keyword generation
        ontologyDescription.finalize();

        return ontologyDescription;
    }

    public static DataPropertyDescription getDataPropertyDescription(String dataPropertyIRI, Ontology ontology){

        /*DATA PROPERTY DESCRIPTION*/
        //get all data properties
        //for each class, get their label and comments
        //remove stop words, blank spaces, stem words
        DataPropertyDescription currentResourceDescription =
                new DataPropertyDescription(dataPropertyIRI,ontology.getIRI());

        OWLDataProperty dataPropertyAxiom = ontologyManager.getOWLDataFactory().getOWLDataProperty(dataPropertyIRI);

        //get annotations
        Stream<OWLAnnotationAssertionAxiom> annotations =
                    EntitySearcher.getAnnotationAssertionAxioms(dataPropertyAxiom, ontology.getOWLOntology());

        //process annotations
        processAnnotations(annotations,currentResourceDescription);




        //get domain of data property
        Stream<OWLDataPropertyDomainAxiom> domains = ontology.getOWLOntology().dataPropertyDomainAxioms(dataPropertyAxiom);
        domains.forEach(domainClass -> {
            currentResourceDescription.addDomain(getDLExpression(domainClass.getDomain()));

        });

        //get ranges of the object property
        Stream<OWLDataPropertyRangeAxiom> ranges =
                ontology.getOWLOntology().dataPropertyRangeAxioms(dataPropertyAxiom);
        OWLDataPropertyRangeAxiom test;
        ranges.forEach(rangeClass -> {
            for (OWLDatatype dataType : rangeClass.getDatatypesInSignature()) {
                currentResourceDescription.addRange(dataType.getIRI().getIRIString());
            }
        });


        return currentResourceDescription;
    }
    public static void processAnnotations(Stream<OWLAnnotationAssertionAxiom> annotations,
                                                       ResourceDescription resourceDescription){
        //iterate over every annotation
        annotations.forEach(annotationAssertionAxiom -> {
            OWLAnnotation annotation = annotationAssertionAxiom.getAnnotation() ;
            try {
                //processs current annotation
                processAnnotation(annotation,resourceDescription);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        resourceDescription.finalize();
    }

    public static void processAnnotation(OWLAnnotation annotation,
                                                              OntologyEntityDescription entity) throws IOException {
        String property = annotation.getProperty().getIRI().getIRIString();
        String value = getValueFromAnnotation(annotation);

        Set<Keyword> valueKeyword;

        if (Global.labelIRIs.contains(property)){
            String classTitle = value;
            valueKeyword = Utils.extractKeywords(value,true);

            entity.addLabelKeywords(valueKeyword);
        }

        Set<Keyword> descriptionKeyword = null;
        if (Global.commentIRIs.contains(property)){
            String classDescription = value;
            descriptionKeyword = Utils.extractKeywords(value,false);
            entity.addCommentKeywordsForProperty(descriptionKeyword,property);
        }
    }




    public static String getValueFromAnnotation(OWLAnnotation annotation){
        String value = "";
        if (annotation.getValue() instanceof OWLLiteral){
            OWLLiteral literalVal = ((OWLLiteral) annotation.getValue());
            if (!literalVal.hasLang() || literalVal.hasLang("en")){
                value = literalVal.getLiteral();
            }
        }
        return value;
    }

    public static String getDLExpression(OWLClassExpression owlClassExpression){
        String expression="";
        try{
            expression = owlClassExpression.asOWLClass().getIRI().getIRIString();
        }catch (Exception e){
            DLSyntaxObjectRenderer obj = new DLSyntaxObjectRenderer();
            expression = "DLExpression:"+obj.render(owlClassExpression);
        }
        return expression;
    }

    public static void selectCommentKeywords(Map<String, Set<Keyword>> commentKeywordsForProperty, Set<Keyword> commentKeywords) {
        int i = 0;
        while (i<Global.commentIRIs.size() ){
            String propertyIRI =Global.commentIRIs.get(i);
            if (commentKeywordsForProperty.containsKey(propertyIRI)){
                commentKeywords.addAll(commentKeywordsForProperty.get(propertyIRI));
                break;
            }
            i++;
        }
    }



    public static boolean existPropertyPath(String fromEntityIRI, String toEntityIRI, Ontology ontology){
        String queryPath = "ASK { <fromEntityIRI> (<>|!<>)* <toEntityIRI> }";
        Query query = QueryFactory.create(queryPath) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, ontology.getGraph());
        boolean result = qexec.execAsk();
        return result;
    }
}
