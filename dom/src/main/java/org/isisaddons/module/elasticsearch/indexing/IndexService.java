package org.isisaddons.module.elasticsearch.indexing;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.isisaddons.module.elasticsearch.ElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@DomainService(nature = NatureOfService.DOMAIN)
public class IndexService extends ElasticSearchService {

    private static final Logger log = LoggerFactory.getLogger(IndexService.class);

    public void deleteDocument(Indexable deletedObject) {
        try {
            AbstractIndex index = indexerFactory.createIndexer(deletedObject).createUpdatedIndex();
            DeleteRequest deleteRequest = new DeleteRequest(ElasticSearchService.getIndexName(), index.getType().getName(),
                    deletedObject.getIndexId());
            getClient().delete(deleteRequest).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateIndex(Indexable updatedObject) {
        try {
            if (updatedObject.isIndexable()) {
                insertOrUpdate(updatedObject, indexerFactory.createIndexer(updatedObject).createUpdatedIndex());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertOrUpdate(Indexable updatedObject, AbstractIndex index) throws InterruptedException, ExecutionException {
        String json = index.createJson();

        IndexRequest indexRequest = new IndexRequest(ElasticSearchService.getIndexName(), index.getType().getName(),
                updatedObject.getIndexId())
                .source(json, XContentType.JSON);

        UpdateRequest updateRequest = new UpdateRequest(ElasticSearchService.getIndexName(), index.getType().getName(),
                updatedObject.getIndexId())
                .doc(json, XContentType.JSON)
                .upsert(indexRequest);

        log.info("Updating Elastic Search for "+ updatedObject.toString() +" >>> "+ json);

        getClient().update(updateRequest).get();
    }

    private XContentBuilder createSettings() throws IOException {
        XContentBuilder settingsBuilder = jsonBuilder()
                .startObject()
                .startObject("analysis")
                .startObject("char_filter")
                .startObject("ampersand_mapping")
                .field("type", "mapping")
                .field("mappings", new String[]{"& => EN"})
                .endObject()
                .endObject()
                .startObject("analyzer")
                .startObject("custom_with_char_filter")
                .field("filter", new String[]{"standard", "lowercase"})
                .field("char_filter", new String[]{"ampersand_mapping"})
                .field("tokenizer", "standard")
                .endObject()
                .endObject()
                .endObject()
                .endObject();

        // above settings tmp disabled
        settingsBuilder = jsonBuilder();

        return settingsBuilder;
    }

    private void addMappings() throws IOException {
        // MAPPING GOES HERE
        XContentBuilder mappingBuilder = jsonBuilder()
                .startObject()
                .startObject("company")
                .startObject("properties")
                .startObject("name")
                .field("type", "string")
                .field("analyzer", "custom_with_char_filter")
                //.field("search_analyzer", "custom_with_char_filter")
                .endObject()
                .endObject()
                .endObject()
                .endObject();

        //createIndexRequestBuilder.addMapping(SearchService.ELASTIC_SEARCH_INDEX_NAME, mappingBuilder);
        PutMappingResponse putMappingResponse = getClient().admin().indices()
                .preparePutMapping(ElasticSearchService.getIndexName())
                .setType("company")
                .setSource(mappingBuilder)
                .execute().actionGet();
    }

    public void initialiseIndex() {
        try {
            DeleteIndexRequest deleteRequest = new DeleteIndexRequest(ElasticSearchService.getIndexName());
            getClient().admin().indices().delete(deleteRequest).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            final CreateIndexRequestBuilder createIndexRequestBuilder = getClient().admin().indices().prepareCreate(ElasticSearchService.getIndexName());
            createIndexRequestBuilder.setSettings(createSettings()).execute().actionGet();

            // tmp disable mappings
            // addMappings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialiseSearchEngine() {
        try {
            initialiseIndex();

            BulkRequestBuilder bulkRequest = getClient().prepareBulk();

            /*
            contactService.listAllActive().forEach((indexable) -> addToBulkRequest(bulkRequest, indexable));
            companyService.listAllActive().forEach((indexable) -> addToBulkRequest(bulkRequest, indexable));
            subscriptionFilterService.listAllSubscriptionsIgnoringAncient().forEach((indexable) -> addToBulkRequest(bulkRequest, indexable));
            inportService.listAll().forEach((indexable) -> addToBulkRequest(bulkRequest, indexable));
            orderService.listAll().forEach((indexable) -> addToBulkRequest(bulkRequest, indexable));
            phoneNumberService.listAll().forEach((indexable) -> addToBulkRequest(bulkRequest, indexable));
            */

            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                log.error("An error occurred while initialising search engine.\n\n" + bulkResponse.buildFailureMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void addToBulkRequest(BulkRequestBuilder bulkRequest, Indexable indexable) {
        try {
            AbstractIndex index = indexerFactory.createIndexer(indexable).createUpdatedIndex();
            bulkRequest.add(getClient().prepareIndex(ElasticSearchService.getIndexName(), index.getType().getName(), indexable.getIndexId())
                    .setSource(index.createJson(), XContentType.JSON));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject
    private BookmarkService2 bookmarkServiceDefault;

    @Inject
    private IndexerFactory indexerFactory;
}
