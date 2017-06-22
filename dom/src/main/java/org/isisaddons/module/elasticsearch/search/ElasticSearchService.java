package org.isisaddons.module.elasticsearch.search;

import lombok.Getter;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created by E. de Hair <e.dehair@pocos.nl> on 6/16/17.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class ElasticSearchService {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchService.class);

    @Programmatic
    @PostConstruct
    public void postConstruct() throws UnknownHostException {
        if(elasticSearchConfig == null) {
            elasticSearchConfig = getElasticSearchNodeClusterProperties();
        }
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
        String clusterName = elasticSearchConfig.getProperty("cluster.name", "elasticsearch");
        String clusterTransportAddress = elasticSearchConfig.getProperty("cluster.transport.address", "localhost");
        int clusterPort = Integer.valueOf(elasticSearchConfig.getProperty("cluster.port", "9300"));

        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .build();
        return new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(clusterTransportAddress), clusterPort));
    }

    public String getIndexName(){
        return elasticSearchConfig.getProperty("index.name", "defaultindex");
    }

    private static Properties getElasticSearchNodeClusterProperties(){
        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = "elasticsearch.properties";
            input = SearchService.class.getClassLoader().getResourceAsStream(filename);
            if(input==null){
                log.error("Sorry, unable to find " + filename);
                return new Properties();
            }

            //load a properties file from class path, inside static method
            prop.load(input);
            return prop;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new Properties();
    }

    @Getter
    private static Client client;

    private static Properties elasticSearchConfig;
}
