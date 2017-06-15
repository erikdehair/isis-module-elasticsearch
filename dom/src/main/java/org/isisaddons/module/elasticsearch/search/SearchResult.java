package org.isisaddons.module.elasticsearch.search;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.isisaddons.module.elasticsearch.search.elastic.IndexService;
import org.isisaddons.module.elasticsearch.search.elastic.Indexable;

import javax.inject.Inject;

@ViewModel
public class SearchResult implements Comparable<SearchResult> {
    public SearchResult() {
    }

    public SearchResult(String bookmarkId, String resultClassName, float score, String source) {
        this.bookmarkId = bookmarkId;
        this.resultClassName = resultClassName;
        this.score = score;
        this.source = source;
    }

    // {{ BookmarkId (property)
    @Property(hidden = Where.EVERYWHERE)
    @Getter
    @Setter
    private String bookmarkId;
    // }}

    // {{ ResultClassName (property)
    @Property(hidden = Where.EVERYWHERE)
    @Getter
    @Setter
    private String resultClassName;
    // }}

    // {{ Source (property)
    @PropertyLayout(hidden = Where.EVERYWHERE)
    @Getter
    @Setter
    private String source;
    // }}

    // {{ Match (property)
    @MemberOrder(sequence = "20")
    @PropertyLayout(named = "Match")
    public String getMatch() {
        Indexable result = getResult();
        if (result != null) {
            return result.getSearchResultSummary();
        } else {
            return "Invalid search result. Probably removed from search index.";
        }
    }
    // }}

    // {{ Score (property)
    @MemberOrder(sequence = "30")
    @PropertyLayout(named = "Score")
    @Getter
    @Setter
    private float score;
    // }}

    // {{ Bookmark (property)
    @Programmatic
    public Bookmark getBookmark() throws ClassNotFoundException {
        return bookmarkServiceDefault.bookmarkFor(Class.forName(getResultClassName()), getBookmarkId());
    }
    // }}

    public Indexable getResult() {
        try {
            return (Indexable) bookmarkServiceDefault.lookup(getBookmark(), BookmarkService2.FieldResetPolicy.DONT_RESET);
        } catch (Exception e) {
            /*
            try {
                indexService.deleteDocument(getBookmark());
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            */
        }
        return null;
    }

    @Override
    public int compareTo(SearchResult o) {
        return ComparisonChain.start()
                .compare(getScore(), o.getScore(), Ordering.natural().reverse())
                .result();
    }

    @Inject
    private BookmarkService2 bookmarkServiceDefault;

    @Inject
    private IndexService indexService;
}
