package org.isisaddons.module.elasticsearch.search;

import com.google.common.collect.Lists;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.apache.isis.applib.services.registry.ServiceRegistry;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.WeightBuilder;
import org.elasticsearch.search.SearchHit;
import org.isisaddons.module.elasticsearch.ElasticSearchService;
import org.isisaddons.module.elasticsearch.indexing.Indexable;
import org.isisaddons.module.elasticsearch.search.result.SearchResult;
import org.isisaddons.module.elasticsearch.search.result.SearchResultsPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@DomainService(nature = NatureOfService.VIEW)
public class SearchService extends SearchServiceHidden {

    @Action(publishing = Publishing.ENABLED)
    public SearchResultsPage find(@ParameterLayout(named = "Query") String query,
                                  @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(named = "Preferred type") final String preferredType) {
        return new SearchResultsPage(query, preferredType);
    }
    public List<String> choices1Find(){
        return getPreferredTypes().entrySet().stream()
                .map(t -> t.getKey().getSimpleName())
                .collect(Collectors.toList());
    }
}
