package org.isisaddons.module.elasticsearch.search;

import com.google.common.collect.Lists;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.registry.ServiceRegistry;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.isisaddons.module.elasticsearch.search.result.SearchResult;
import org.isisaddons.module.elasticsearch.search.result.SearchResultsPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@DomainService(nature = NatureOfService.VIEW)
public class SearchService extends ElasticSearchService {
    private static final String ELASTIC_SEARCH_TYPE_WEIGHTS_KEY = "search.service.default.type.weights";

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    @Action(publishing = Publishing.ENABLED)
    public SearchResultsPage find(String query, @Parameter(optionality = Optionality.OPTIONAL) Type preferredType) {
        return new SearchResultsPage(query, preferredType);
    }

    @Programmatic
    public SortedSet<SearchResult> executeFind(String query, Type preferredType) {
        if (query == null) {
            return new TreeSet<>();
        }
        List<String> toBeReplaced = Lists.newArrayList("+", "-", "&", ":", "/", "\\");
        for (String replaceMe : toBeReplaced) {
            query = query.replace(replaceMe, " ");
        }

        query = Normalizer.normalize(query, Normalizer.Form.NFD);

        boolean isPreferationSet = preferredType != null && !preferredType.equals(Type.empty_choice);

        SearchRequestBuilder builder = getClient().prepareSearch(ElasticSearchService.getIndexName())
                //.setTypes(Type.toArray(), "")
                .setTypes("org.isisaddons.module.elasticsearch.fixture.dom.ElasticSearchDemoObject")
                .setSize(25)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setExplain(false);

        //QueryBuilder of the general part
        QueryBuilder generalQueryBuilder;

        generalQueryBuilder = QueryBuilders.boolQuery();
        String termWildCard, termExact;
        for (String term :
                Arrays.asList(query.toLowerCase().split(" "))) {
            termWildCard = term;//.replace("&", "\u0026");
            termExact = term;//.replace("&", "\u0026");
            generalQueryBuilder = ((BoolQueryBuilder) generalQueryBuilder).must(QueryBuilders.wildcardQuery("_all", "*" + termWildCard + "*"));
            generalQueryBuilder = ((BoolQueryBuilder) generalQueryBuilder).should(QueryBuilders.matchQuery("_all", termExact).boost(2));
        }

        builder.setQuery(generalQueryBuilder);

        log.info(builder.toString());
        SearchResponse response = builder.execute().actionGet();
        log.info(response.toString());

        SearchHit[] hits = response.getHits().getHits();

        return Arrays.stream(hits)
                .map(h -> {
                    String className = h.getType();
                    String id = h.getId();
                    SearchResult result = new SearchResult(id, className, h.getScore(), h.getSourceAsString());
                    serviceRegistry.injectServicesInto(result);
                    return result;
                })
                .filter(r -> r.getResult() != null)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Inject
    private ServiceRegistry serviceRegistry;
}
