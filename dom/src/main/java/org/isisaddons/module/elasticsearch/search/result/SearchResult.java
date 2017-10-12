package org.isisaddons.module.elasticsearch.search.result;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.isisaddons.module.elasticsearch.indexing.Indexable;

import javax.inject.Inject;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "searchResult")
@XmlType(
        propOrder = {
                "bookmark",
                "score",
                "source"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResult implements Comparable<SearchResult>, org.apache.isis.applib.ViewModel.Cloneable {
    public SearchResult() {
    }

    public SearchResult(Bookmark bookmark, float score, String source) {
        this.bookmark = bookmark;
        this.score = score;
        this.source = source;
    }

    @XmlElement(required = true)
    @Property(hidden = Where.EVERYWHERE)
    @Getter
    @Setter
    private Bookmark bookmark;

    @XmlElement(required = true)
    @PropertyLayout(hidden = Where.EVERYWHERE)
    @Getter
    @Setter
    private String source;

    @XmlTransient
    @MemberOrder(sequence = "20")
    @PropertyLayout(named = "Match")
    public String getMatch() {
        Indexable result = getResult();
        return result != null ? result.getSearchResultSummary() : "Invalid search result. Probably removed from search index.";
    }

    @XmlElement(required = true)
    @MemberOrder(sequence = "30")
    @PropertyLayout(named = "Score")
    @Getter
    @Setter
    private float score;

    @XmlTransient
    private Indexable result;

    @XmlTransient
    public Indexable getResult() {
        try {
            if(this.result == null){
                this.result = (Indexable) bookmarkServiceDefault.lookup(getBookmark(), BookmarkService2.FieldResetPolicy.DONT_RESET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.result;
    }

    @Override
    public int compareTo(SearchResult o) {
        return ComparisonChain.start()
                .compare(getScore(), o.getScore(), Ordering.natural().reverse())
                .compare(getBookmark().getObjectType(), o.getBookmark().getObjectType())
                .compare(getBookmark().getIdentifier(), o.getBookmark().getIdentifier())
                .result();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchResult)) return false;

        SearchResult that = (SearchResult) o;

        return getBookmark().equals(that.getBookmark());
    }

    @Override
    public int hashCode() {
        int result = getBookmark().hashCode();
        return result;
    }

    @Override
    public Object clone() {
        SearchResult clone = new SearchResult(getBookmark(), getScore(), getSource());
        clone.result = getResult();
        return clone;
    }

    @XmlTransient
    @Inject
    private BookmarkService2 bookmarkServiceDefault;
}
