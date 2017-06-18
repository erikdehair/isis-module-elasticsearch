package org.isisaddons.module.elasticsearch.search.elastic.indexing;

import org.junit.Assert;
import org.junit.Test;

import java.net.UnknownHostException;


public class IndexServiceTest {
    @Test
    public void testInitializeIndex() throws UnknownHostException {
        IndexService service = new IndexService();
        service.postConstruct();

        service.initialiseIndex();

        Assert.assertNotNull(service.getClient());
    }
}
