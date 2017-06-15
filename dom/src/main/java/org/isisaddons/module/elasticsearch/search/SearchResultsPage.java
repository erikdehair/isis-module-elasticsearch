package org.isisaddons.module.elasticsearch.search;

import org.apache.isis.applib.annotation.*;
import org.isisaddons.module.elasticsearch.search.elastic.Type;

import javax.inject.Inject;
import java.util.SortedSet;

@ViewModel
@ViewModelLayout(named = "Zoekresultaten", cssClassFa = "fa fa-search")
@MemberGroupLayout(columnSpans = {4, 0, 0, 12},
        left = {SearchResultsPage.BLOCK_TITLE_SEARCHCOMMAND})
public class SearchResultsPage {
    static final String BLOCK_TITLE_SEARCHCOMMAND = "Zoekopdracht";

    public SearchResultsPage() {
    }

    public SearchResultsPage(String query, Type preferredType) {
        this.query = query;
        this.preferredType = (preferredType != null ? preferredType.name() : null);
    }

    public String title() {
        return "Zoekresultaten";
    }

    // {{ Query (property)
    private String query;

    @MemberOrder(name = BLOCK_TITLE_SEARCHCOMMAND, sequence = "10")
    @PropertyLayout(named = "Zoekterm(en)")
    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }
    // }}

    // {{ PreferredType (property)
    private String preferredType;

    @Property(hidden = Where.EVERYWHERE)
    public String getPreferredType() {
        return preferredType;
    }

    public void setPreferredType(final String preferredType) {
        this.preferredType = preferredType;
    }

    @MemberOrder(name = BLOCK_TITLE_SEARCHCOMMAND, sequence = "20")
    @PropertyLayout(named = "Voorkeurselement")
    public String getPreferredTypeName() {
        return (getPreferredType() != null ? Type.valueOf(getPreferredType()).getTypeName() : null);
    }
    // }}

    @MemberOrder(name = BLOCK_TITLE_SEARCHCOMMAND, sequence = "30")
    @PropertyLayout(named = "Aantal resultaten")
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

    @CollectionLayout(named = "Resultaten", paged = 50, defaultView = "table")
    public SortedSet<SearchResult> getResults() {
        return findResults();
    }

    @Inject
    private SearchService searchService;
}
