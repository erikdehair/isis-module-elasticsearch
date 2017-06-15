package org.isisaddons.module.elasticsearch.search.elastic.indexer;

import org.isisaddons.module.elasticsearch.search.elastic.AbstractIndex;
import org.isisaddons.module.elasticsearch.search.elastic.Indexable;


public class CompanyIndexer extends Indexer {

    public CompanyIndexer(Indexable indexable) {
        super(indexable);
    }

    @Override
    public AbstractIndex createUpdatedIndex() {
        /*
        PortalCompany p = (PortalCompany) indexable;
        CompanyIndex index = new CompanyIndex();
        index.setTenancy(p.getTenancy());
        index.setName(p.getName1());
        index.setPartnerCustomerNumber(p.getPartnerCustomerNumber());
        index.setKvkNumber(p.getKvkNumber());
        index.setAddress(p.getPostalAddress());
        index.setZipCode(p.getPostalCode());
        index.setResidence(p.getResidence());

        String phoneNumber = p.getPhone();
        index.setPhoneNumberAsInt(createPhoneNumberAsInt(phoneNumber));
        index.setPhoneNumberAsIntPlus(createPhoneNumberAsIntPlus(phoneNumber));
        index.setPhoneNumberAsNational(createPhoneNumberAsNational(phoneNumber));

        index.setFaxNumberAsInt(null);
        index.setFaxNumberAsIntPlus(null);
        index.setFaxNumberAsNational(null);

        return index;
        */
        return null;
    }

}
