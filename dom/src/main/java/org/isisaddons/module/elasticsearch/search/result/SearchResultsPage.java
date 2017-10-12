package org.isisaddons.module.elasticsearch.search.result;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.isisaddons.module.elasticsearch.indexing.Indexable;
import org.isisaddons.module.elasticsearch.search.SearchService;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

@XmlRootElement(name = "searchResultsPage")
@XmlType(
        propOrder = {
                "query",
                "preferredType",
                "results"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@ViewModelLayout(cssClassFa = "fa fa-search")
public class SearchResultsPage {
    public SearchResultsPage() {
    }

    public SearchResultsPage(String query, String preferredType) {
        this.query = query;
        this.preferredType = preferredType;
    }

    public String title() {
        return "Search results";
    }

    @XmlElement(required = true)
    @Getter @Setter
    private String query;

    @XmlElement(required = false)
    @Getter @Setter
    private String preferredType;

    @XmlTransient
    public Integer getNumberOfResults() {
        return findResults().size();
    }

    private SortedSet<SearchResult> results;

    @XmlTransient
    @Collection()
    @CollectionLayout(defaultView = "table")
    public SortedSet<SearchResult> getResults(){
        return findResults();
    }

    private SortedSet<SearchResult> findResults() {
        if (this.results == null || this.results.isEmpty()) {
            Class<Indexable> preferredType = searchService.getPreferredTypes().entrySet().stream()
                    .filter(t -> t.getKey().getSimpleName().equals(getPreferredType()))
                    .findFirst()
                    .map(t -> (Class<Indexable>)t.getKey())
                    .orElse(null);
            this.results = searchService.executeFind(getQuery(), preferredType);
        }
        return this.results;
    }

    public SearchResultsPage searchAgain(@ParameterLayout(named = "Query") String query,
                                         @Nullable @ParameterLayout(named = "Preferred type") final String preferredType){
        return searchService.find(query, preferredType);
    }
    public String default0SearchAgain(){
        return getQuery();
    }
    public String default1SearchAgain(){
        return getPreferredType();
    }
    public List<String> choices1SearchAgain(){
        return searchService.getPreferredTypes().entrySet().stream()
                .map(t -> t.getKey().getSimpleName())
                .collect(Collectors.toList());
    }

    @XmlTransient
    @Inject
    private SearchService searchService;
}
