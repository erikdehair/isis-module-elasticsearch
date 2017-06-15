package org.isisaddons.module.elasticsearch.search.elastic.indexer;


import org.isisaddons.module.elasticsearch.search.elastic.AbstractIndex;
import org.isisaddons.module.elasticsearch.search.elastic.Indexable;

public abstract class Indexer {
    protected Indexable indexable;

    public abstract AbstractIndex createUpdatedIndex();

    public Indexer(Indexable indexable) {
        this.indexable = indexable;
    }

    public static Indexer createIndexer(Indexable indexable) throws Exception {
        /*
	    if(AbstractSubscription.class.isAssignableFrom(indexable.getClass()))
		{
			return new SubscriptionIndexer(indexable);
		}
		else if(PortalCompany.class.isAssignableFrom(indexable.getClass()))
		{
			return new CompanyIndexer(indexable);
		}
		else if(AbstractOrder.class.isAssignableFrom(indexable.getClass()))
		{
			return new OrderIndexer(indexable);
		}
		else if(Contact.class.isAssignableFrom(indexable.getClass()))
		{
			return new ContactIndexer(indexable);
		}
		else if(AbstractPhoneNumber.class.isAssignableFrom(indexable.getClass()))
		{
			return new nl.pocos.services.search.elastic.indexer.PhoneNumberIndexer(indexable);
		}
		else if(AbstractPorting.class.isAssignableFrom(indexable.getClass()))
		{
			return new nl.pocos.services.search.elastic.indexer.PortingIndexer(indexable);
		}
		else
		{
			throw new Exception("Non-indexable class found: "+ indexable.getClass().getName());
		}
		*/
        return null;
    }
}
