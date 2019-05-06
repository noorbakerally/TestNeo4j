package OntologyDescription;

import Word.Keyword;

import java.util.Set;

public interface OntologyEntityDescription {
    String getIRI();
    public String getOntologyIRI();
    public Set<Keyword> getLabelKeywords();
    public Set <Keyword> getCommentKeywords();
    public void addLabelKeywords(Set<Keyword> keywords);
    public void addCommentKeywords(Set<Keyword> keywords);

    void addCommentKeywordsForProperty(Set<Keyword> descriptionKeyword, String property);
    void selectCommentKeywords();
    void finalize();
}
