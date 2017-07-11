package org.isisaddons.module.elasticsearch.indexing;

import com.google.common.eventbus.Subscribe;
import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.eventbus.ObjectPersistedEvent;
import org.apache.isis.applib.services.eventbus.ObjectRemovingEvent;
import org.apache.isis.applib.services.eventbus.ObjectUpdatedEvent;

import javax.inject.Inject;

@DomainService(nature = NatureOfService.DOMAIN)
public class IndexableSubscriber extends AbstractSubscriber {
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

    @Inject
    private IndexService indexService;
}
