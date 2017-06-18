package org.isisaddons.module.elasticsearch.search.elastic;

import com.google.common.eventbus.Subscribe;
import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.eventbus.ObjectPersistedEvent;
import org.apache.isis.applib.services.eventbus.ObjectRemovingEvent;
import org.apache.isis.applib.services.eventbus.ObjectUpdatedEvent;
import org.isisaddons.module.elasticsearch.search.elastic.indexing.IndexService;
import org.isisaddons.module.elasticsearch.search.elastic.indexing.Indexable;

import javax.inject.Inject;

@DomainService(nature = NatureOfService.DOMAIN)
public class ElasticSearchSubscriber extends AbstractSubscriber {
    @Subscribe
    public void on(ObjectPersistedEvent<Indexable> ev) {
        indexService.updateIndex(ev.getSource());
    }

    @Subscribe
    public void on(ObjectUpdatedEvent<Indexable> ev) {
        indexService.updateIndex(ev.getSource());
    }

    @Subscribe
    public void on(ObjectRemovingEvent<Indexable> ev) {
        indexService.deleteDocument(ev.getSource());
    }

    /*
    @Subscribe
    public void on(Company.NameChangedEvent ev) {
        if (ev.getSource() instanceof PortalCompany) {
            switch (ev.getEventPhase()) {
                case EXECUTED:
                    indexService.updateIndexRelatedCompany(ev.getSource().getId());
                    break;
                default:
                    break;
            }
        }
    }
    */

    @Inject
    private IndexService indexService;
}
