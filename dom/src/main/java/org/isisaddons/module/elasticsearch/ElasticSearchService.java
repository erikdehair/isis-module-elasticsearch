package org.isisaddons.module.elasticsearch;

import lombok.Getter;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by E. de Hair <e.dehair@pocos.nl> on 6/16/17.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class ElasticSearchService {

    @Programmatic
    @PostConstruct
    public void postConstruct() throws UnknownHostException {
        if(client == null) {
            client = createElasticSearchClient();
        }
    }

    @Programmatic
    @PreDestroy
    public void preDestroy() {
        client.close();
    }

    public static Client createElasticSearchClient() throws UnknownHostException {
        IsisConfiguration isisConfiguration = IsisContext.getSessionFactory().getConfiguration();

        String clusterName = isisConfiguration.getString("cluster.name", "elasticsearch");
        String clusterTransportAddress = isisConfiguration.getString("cluster.transport.address", "localhost");
        int clusterPort = isisConfiguration.getInteger("cluster.port", 9300);

        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .build();
        return new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(clusterTransportAddress), clusterPort));
    }

    public static String getIndexName(){
        return IsisContext.getSessionFactory().getConfiguration().getString("index.name", "defaultindex");
    }

    @Getter
    private static Client client;
}
