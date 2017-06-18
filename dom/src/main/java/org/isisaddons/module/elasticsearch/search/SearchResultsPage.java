package org.isisaddons.module.elasticsearch.search;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.isisaddons.module.elasticsearch.search.elastic.Type;

import javax.inject.Inject;
import java.util.SortedSet;

@ViewModel
@ViewModelLayout(cssClassFa = "fa fa-search")
public class SearchResultsPage {
    public SearchResultsPage() {
    }

    public SearchResultsPage(String query, Type preferredType) {
        this.query = query;
        this.preferredType = (preferredType != null ? preferredType.name() : null);
    }

    public String title() {
        return "Search results";
    }

    @Getter @Setter
    private String query;

    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String preferredType;

    public String getPreferredTypeName() {
        return (getPreferredType() != null ? Type.valueOf(getPreferredType()).getTypeName() : null);
    }

    public Integer getNumberOfResults() {
        return findResults().size();
    }

    private SortedSet<SearchResult> results;

    private SortedSet<SearchResult> findResults() {
        if (this.results == null) {
            this.results = searchService.executeFind(getQuery(),
                    (getPreferredType() != null ? Type.valueOf(getPreferredType()) : null));
        }
        return this.results;
    }

    @CollectionLayout(paged = 50, defaultView = "table")
    public SortedSet<SearchResult> getResults() {
        return findResults();
    }

    @Inject
    private SearchService searchService;
}
