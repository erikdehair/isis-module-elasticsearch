package org.isisaddons.module.elasticsearch.search;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.registry.ServiceRegistry;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.WeightBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.isisaddons.module.elasticsearch.search.elastic.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@DomainService(nature = NatureOfService.DOMAIN)
public class SearchService {
    public static final String ELASTIC_SEARCH_INDEX_NAME = "portal";

    private static final String ELASTIC_SEARCH_TYPE_WEIGHTS_KEY = "search.service.default.type.weights";

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    @Programmatic
    @PostConstruct
    public void postConstruct() throws UnknownHostException {
        client = createElasticSearchClient();
    }

    @Programmatic
    @PreDestroy
    public void preDestroy() {
        client.close();
    }

    public static Client createElasticSearchClient() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        return client;
    }

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
        //query = StringUtils.stripAccents(query);

        boolean isPreferationSet = preferredType != null && !preferredType.equals(Type.empty_choice);

        Integer boostValue = 1;
        if (isPreferationSet) {
            boostValue = 10;
        }

        SearchRequestBuilder builder = client.prepareSearch(ELASTIC_SEARCH_INDEX_NAME)
                .setTypes(Type.toArray())
                .setSize(25)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setExplain(false);

        //Total query builder
        QueryBuilder queryBuilder;

        //QueryBuilder of the general part
        QueryBuilder generalQueryBuilder;

        //MatchQueryBuilder matchAll = QueryBuilders.matchQuery("_all", query);
        //generalQueryBuilder = QueryBuilders.matchPhrasePrefixQuery("_all", query).slop(100).maxExpansions(50);

        generalQueryBuilder = QueryBuilders.boolQuery();
        String termWildCard, termExact;
        for (String term :
                Arrays.asList(query.toLowerCase().split(" "))) {
            termWildCard = term;//.replace("&", "\u0026");
            termExact = term;//.replace("&", "\u0026");
            generalQueryBuilder = ((BoolQueryBuilder) generalQueryBuilder).must(QueryBuilders.wildcardQuery("_all", "*" + termWildCard + "*"));
            generalQueryBuilder = ((BoolQueryBuilder) generalQueryBuilder).should(QueryBuilders.matchQuery("_all", termExact).boost(2));
        }

        /*
        if(!SecurityUtil.isSuperUser())
		{
			PortalCompany currentUsersCompany = companyService.findCompanyRepresentedByUser(SecurityUtil.getUsername()); 
			queryBuilder = QueryBuilders.boolQuery()
					.must(QueryBuilders.termQuery("tenancy", currentUsersCompany.getId()).boost(new Float(0.1)))
					.should(generalQueryBuilder);
		}
		else
		{
			queryBuilder = generalQueryBuilder;
		}
		*/
        queryBuilder = generalQueryBuilder;

        if (isPreferationSet) {
            FunctionScoreQueryBuilder.FilterFunctionBuilder[] functions = {
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("_type", preferredType.name()),
                            new WeightBuilder().setWeight(boostValue.floatValue()))
            };
            builder = builder.setQuery(QueryBuilders.functionScoreQuery(functions));

        } else {
            String defaultWeights = "{\"data\":{\"company\":1,\"contact\":2,\"inport\":3,\"order\":4,\"phonenumber\":5,\"subscription\":6}}";

            //String defaultWeights = applicationSettingsService.find(ELASTIC_SEARCH_TYPE_WEIGHTS_KEY).valueAsString();

            HashMap<String, Integer> typeWeights = new HashMap<>();
            try {
                typeWeights = new ObjectMapper()
                        .readValue(defaultWeights, new TypeReference<Map<String, Integer>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] types = Type.toArray();
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
                    String[] idConfig = h.getId().split(":");
                    String className = idConfig[0];
                    String id = idConfig[1];
                    SearchResult result = new SearchResult(id, className, h.getScore(), h.getSourceAsString());
                    serviceRegistry.injectServicesInto(result);
                    return result;
                })
                .filter(r -> r.getResult() != null)
                .collect(Collectors.toCollection(() -> Sets.newTreeSet()));
    }

    private Client client;

    @Inject
    private ServiceRegistry serviceRegistry;
}
