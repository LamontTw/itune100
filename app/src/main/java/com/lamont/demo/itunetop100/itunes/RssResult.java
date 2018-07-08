package com.lamont.demo.itunetop100.itunes;

public class RssResult {
    private feed feed;
    private static class feed {
        private String title;
        private String id;
        private author author;
        private String copyright;
        private String icon;
        private String updated;
        // link << skip
        private result[] results;
    }

    private static class author {
        private String name;
        private String uri;
    }

    public static class result {
        public String artistName;
        String id;
        String releaseDate;
        public String name;
        String collectionName;
        String kind;
        String artistId;
        String artistUrl;
        public String artworkUrl100;
        String url;
        // genreId << skip
    }

    public void showTest() {
        System.out.println(feed.title);
        System.out.println(feed.copyright);
        System.out.println(feed.results[0].artistName);
    }

    public result[] getResults() {
        return feed.results;
    }
}
