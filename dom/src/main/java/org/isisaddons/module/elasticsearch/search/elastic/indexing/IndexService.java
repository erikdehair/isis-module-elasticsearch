package org.isisaddons.module.elasticsearch.search.elastic.indexing;

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.isisaddons.module.elasticsearch.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@DomainService(nature = NatureOfService.DOMAIN)
public class IndexService {

    private static final Logger log = LoggerFactory.getLogger(IndexService.class);

    @Programmatic
    @PostConstruct
    public void postConstruct() throws UnknownHostException {
        client = SearchService.createElasticSearchClient();
    }

    @Programmatic
    @PreDestroy
    public void preDestroy() {
        client.close();
    }

    public void deleteDocument(Indexable deletedObject) {
        try {
            AbstractIndex index = Indexer.createIndexer(deletedObject).createUpdatedIndex();
            DeleteRequest deleteRequest = new DeleteRequest(SearchService.ELASTIC_SEARCH_INDEX_NAME, index.getType().name(),
                    deletedObject.getIndexId());
            client.delete(deleteRequest).get();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("An error occurred while deleting index of object '" + deletedObject.toString() + "'\n\n", e);
        }
    }

    /*
    @Action(hidden = Where.EVERYWHERE)
    public void deleteDocument(Bookmark bookmark) {
        try {
            Class<?> clazz = Class.forName(bookmark.getObjectType());
            String type = null;
            if (AbstractSubscription.class.isAssignableFrom(clazz)) {
                type = nl.pocos.services.search.elastic.Type.subscription.name();
            } else if (PortalCompany.class.isAssignableFrom(clazz)) {
                type = nl.pocos.services.search.elastic.Type.company.name();
            } else if (AbstractOrder.class.isAssignableFrom(clazz)) {
                type = nl.pocos.services.search.elastic.Type.order.name();
            } else if (Contact.class.isAssignableFrom(clazz)) {
                type = nl.pocos.services.search.elastic.Type.contact.name();
            } else if (AbstractPhoneNumber.class.isAssignableFrom(clazz)) {
                type = nl.pocos.services.search.elastic.Type.phonenumber.name();
            } else if (AbstractInport.class.isAssignableFrom(clazz)) {
                type = nl.pocos.services.search.elastic.Type.porting.name();
            } else {
                throw new Exception("Non-indexable class found: " + clazz.getName());
            }

            DeleteRequest deleteRequest = new DeleteRequest(SearchService.ELASTIC_SEARCH_INDEX_NAME, type, bookmark.getIdentifier());
            client.delete(deleteRequest).get();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("An error occurred while deleting index of object '" + bookmark.getIdentifier() + "'\n\n", e);
        }
    }
    */

    public void updateIndex(Indexable updatedObject) {
        try {
            if (updatedObject.isIndexable()) {
                insertOrUpdate(updatedObject, Indexer.createIndexer(updatedObject).createUpdatedIndex());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("An error occurred while updating/creating index of object '" + updatedObject.toString() + "'\n\n", e);
        }
    }

    @Action(commandExecuteIn = CommandExecuteIn.BACKGROUND)
    public boolean updateIndexRelated(@ParameterLayout(named = "className", describedAs = "The FQ classname of the portal class") String className,
                                      @ParameterLayout(named = "objectId", describedAs = "The database primary key of the objects record") Integer id) throws Exception {
        try {
            Class<?> indexableClazz = Class.forName(className);

            Bookmark bookmark = bookmarkServiceDefault.bookmarkFor(indexableClazz, "i_" + id);

            Indexable object = (Indexable) bookmarkServiceDefault.lookup(bookmark, BookmarkService2.FieldResetPolicy.DONT_RESET);

            if (object != null && object.isIndexable()) {
                //updateIndexRelated(object);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error("An error occurred while updating/creating related index of object '" + className + "[id=" + id + "]'.\n\n", e);
        }
        return true;
    }

    /*
    private void updateIndexRelated(Indexable changedObject) throws Exception {
        if (PortalCompany.class.isAssignableFrom(changedObject.getClass())) {
            updateIndexRelatedCompany(((PortalCompany) changedObject).getId());
        } else {
            throw new Exception("Non-indexable class found: " + changedObject.getClass().getName());
        }
    }


    /**
     * Update all contacts, subscriptions, inports, orders and phone numbers for which the name of the company changed
     *
     * @param companyId Id of the company to be updated
     * /
    @Action(commandExecuteIn = CommandExecuteIn.BACKGROUND)
    public void updateIndexRelatedCompany(Integer companyId) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        PortalCompany company = companyService.findCompanyById(companyId);

        company.getActiveEmployedContacts().forEach((indexable) -> addToBulkRequest(bulkRequest, indexable));

        subscriptionFilterService.listAllSubscriptionsForCompanyIgnoringAncient(company, false)
                .forEach((indexable) -> addToBulkRequest(bulkRequest, indexable));

        inportService.findInportsByCompanyAndStatus(null, company)
                .forEach((indexable) -> addToBulkRequest(bulkRequest, indexable));

        company.getOrders().forEach((indexable) -> addToBulkRequest(bulkRequest, indexable));

        company.getPhoneNumbers().forEach((indexable) -> addToBulkRequest(bulkRequest, indexable));

        if (bulkRequest.numberOfActions() > 0) {
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                log.error("An error occurred while initialising search engine.\n\n" + bulkResponse.buildFailureMessage());
            }
        }
    }

    @Action(commandExecuteIn = CommandExecuteIn.BACKGROUND)
    public boolean updateIndex(@ParameterLayout(named = "className", describedAs = "The FQ classname of the portal class") String className,
                               @ParameterLayout(named = "objectId", describedAs = "The database primary key of the objects record") Integer id) {
        try {
            Class<?> indexableClazz = Class.forName(className);

            Bookmark bookmark = bookmarkServiceDefault.bookmarkFor(indexableClazz, "i_" + id);

            nl.pocos.services.search.elastic.Indexable object = (nl.pocos.services.search.elastic.Indexable) bookmarkServiceDefault.lookup(bookmark);

            // ignore update if the object is not visible in the portal
            if (object == null || !object.isIndexable()) {
                return false;
            }

            updateIndex(object);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error("An error occurred while updating/creating index of object.\n\n", e);
        }
        return false;
    }
    */

    private void insertOrUpdate(Indexable updatedObject, AbstractIndex index) throws InterruptedException, ExecutionException {
        String json = index.createJson();
        //json = StringUtils.stripAccents(json);
        IndexRequest indexRequest = new IndexRequest(SearchService.ELASTIC_SEARCH_INDEX_NAME, index.getType().name(),
                updatedObject.getIndexId())
                .source(json, XContentType.JSON);

        UpdateRequest updateRequest = new UpdateRequest(SearchService.ELASTIC_SEARCH_INDEX_NAME, index.getType().name(),
                updatedObject.getIndexId())
                .doc(json, XContentType.JSON)
                .upsert(indexRequest);

        client.update(updateRequest).get();
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
        PutMappingResponse putMappingResponse = client.admin().indices()
                .preparePutMapping(SearchService.ELASTIC_SEARCH_INDEX_NAME)
                .setType("company")
                .setSource(mappingBuilder)
                .execute().actionGet();
    }

    public void initialiseIndex() {
        try {
            DeleteIndexRequest deleteRequest = new DeleteIndexRequest(SearchService.ELASTIC_SEARCH_INDEX_NAME);
            client.admin().indices().delete(deleteRequest).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(SearchService.ELASTIC_SEARCH_INDEX_NAME);
            createIndexRequestBuilder.setSettings(createSettings()).execute().actionGet();

            // tmp disable mappings
            // addMappings();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("An error occurred while initialising search engine.\n\n", e);
        }
    }

    public void initialiseSearchEngine() {
        try {
            initialiseIndex();

            BulkRequestBuilder bulkRequest = client.prepareBulk();

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
            log.error("An error occurred while initialising search engine.\n\n", e);

        }
    }

    private void addToBulkRequest(BulkRequestBuilder bulkRequest, Indexable indexable) {
        try {
            AbstractIndex index = Indexer.createIndexer(indexable).createUpdatedIndex();
            bulkRequest.add(client.prepareIndex(SearchService.ELASTIC_SEARCH_INDEX_NAME, index.getType().name(), indexable.getIndexId())
                    //.setSource(StringUtils.stripAccents(index.createJson())));
                    .setSource(index.createJson(), XContentType.JSON));
        } catch (Exception e) {
            log.error("Error creating index source for " + indexable.toString() + ".\n\n", e);
        }
    }

    private Client client;

    /**
     * For testing purposes
     *
     * @return
     */
    protected Client getClient() {
        return this.client;
    }

    @Inject
    private BookmarkService2 bookmarkServiceDefault;
}
