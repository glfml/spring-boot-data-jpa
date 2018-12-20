package com.meli.datajpa.app.util.paginator;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class PageRender<T> {
    private String url;
    private Page<T> page;
    private int totalPages;
    private int itemsPerPage;
    private int currentPage;

    private List<PageItem> pages;

    public PageRender(String url, Page<T> page) {
        this.url = url;
        this.page = page;
        this.pages = new ArrayList<PageItem>();

        itemsPerPage = page.getSize();
        totalPages = page.getTotalPages();
        currentPage = page.getNumber() + 1;

        int from, to;
        if (totalPages <= itemsPerPage) {
            from = 1;
            to = totalPages;
        } else {
            if (currentPage <= itemsPerPage / 2) {
                from = 1;
                to = itemsPerPage;
            } else {
                if (currentPage >= totalPages - itemsPerPage / 2) {
                    from = totalPages - itemsPerPage + 1;
                    to = itemsPerPage;
                } else {
                    from = currentPage - itemsPerPage / 2 + 1;
                    to = itemsPerPage;
                }
            }
        }

        for (int i = 0; i < to; ++i)  {
            this.pages.add(new PageItem(from + i, currentPage == from + i));
        }
    }

    public String getUrl() {
        return url;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public List<PageItem> getPages() {
        return pages;
    }

    public boolean isFirst() {
        return page.isFirst();
    }

    public boolean isLast() {
        return page.isLast();
    }

    public boolean hasNext() {
        return page.hasNext();
    }

    public boolean hasPrevious() {
        return page.hasPrevious();
    }
}
