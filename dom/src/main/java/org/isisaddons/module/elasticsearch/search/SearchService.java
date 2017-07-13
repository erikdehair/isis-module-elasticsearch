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
public class SearchService extends ElasticSearchService {
    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    /**
     * Map of indexable types with a default preference
     * @return
     */
    @Programmatic
    public HashMap<Class<? extends Indexable>,Integer> getPreferredTypes(){
        return new HashMap<>();
    }

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

    @Programmatic
    public SortedSet<SearchResult> executeFind(String query, Class<Indexable> preferredType) {
        if (query == null) {
            return new TreeSet<>();
        }
        List<String> toBeReplaced = Lists.newArrayList("+", "-", "&", ":", "/", "\\");
        for (String replaceMe : toBeReplaced) {
            query = query.replace(replaceMe, " ");
        }

        query = Normalizer.normalize(query, Normalizer.Form.NFD);

        boolean isPreferationSet = preferredType != null;

        Integer boostValue = 1;
        if(isPreferationSet) {
            boostValue = 10;
        }

        HashMap<Class<? extends Indexable>,Integer> weightDefinitions = getPreferredTypes();

        String[] types = weightDefinitions.entrySet().stream().map(t -> t.getKey().getTypeName()).toArray(String[]::new);
        SearchRequestBuilder builder = getClient().prepareSearch(ElasticSearchService.getIndexName())
                .setTypes(types)
                .setSize(25)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setExplain(true);

        //QueryBuilder of the general part matching part of query and boosts full match
        QueryBuilder generalQueryBuilder = QueryBuilders.boolQuery();
        for (String term : Arrays.asList(query.toLowerCase().split(" "))) {
            // match partly
            generalQueryBuilder = ((BoolQueryBuilder) generalQueryBuilder).must(QueryBuilders.wildcardQuery("_all", "*" + term + "*"));
            // boost an exact match
            generalQueryBuilder = ((BoolQueryBuilder) generalQueryBuilder)
                    .should(QueryBuilders.matchQuery("_all", term).boost(2));
        }

        if (isPreferationSet) {
            FunctionScoreQueryBuilder.FilterFunctionBuilder[] functions = {
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("_type", preferredType.getTypeName()),
                            new WeightBuilder().setWeight(boostValue.floatValue()))
            };
            builder = builder.setQuery(QueryBuilders.functionScoreQuery(functions));
        } else {
            Map<String, Integer> typeWeights = weightDefinitions.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().getTypeName(),
                            e -> e.getValue()
                    ));

            FunctionScoreQueryBuilder.FilterFunctionBuilder[] functions = new FunctionScoreQueryBuilder.FilterFunctionBuilder[types.length];
            String typeName;
            Integer weight;
            for (int i = 0; i < types.length; i++) {
                typeName = types[i];

                if (typeWeights.containsKey(typeName)) {
                    weight = typeWeights.get(typeName);
                } else {
                    weight = 1;
                }

                functions[i] = new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("_type", typeName),
                        new WeightBuilder().setWeight(weight));
            }

            builder = builder.setQuery(QueryBuilders.functionScoreQuery(functions));
        }


        log.info(builder.toString());
        SearchResponse response = builder.execute().actionGet();
        log.info(response.toString());

        SearchHit[] hits = response.getHits().getHits();

        return Arrays.stream(hits)
                .map(h -> {
                    String className = h.getType();
                    String id = h.getId();
                    try {
                        Bookmark bookmark = bookmarkService2.bookmarkFor(Class.forName(className), id.split(":")[1]);
                        SearchResult result = new SearchResult(bookmark, h.getScore(), h.getSourceAsString());
                        serviceRegistry.injectServicesInto(result);
                        return result;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(r -> r != null && r.getResult() != null)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Inject
    private ServiceRegistry serviceRegistry;

    @Inject
    private BookmarkService2 bookmarkService2;
}
