package com.lamont.demo.itunetop100.itunes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface IiTunesRss {
    public String ENDPOINT = "https://rss.itunes.apple.com";


    // https://rss.itunes.apple.com/api/v1/tw/apple-music/top-songs/all/100/explicit.json
    @GET("api/v1/tw/apple-music/top-songs/all/100/explicit.json")
    Call<RssResult> getTWTop100();


    @GET("api/v1/{country}/apple-music/top-songs/all/100/explicit.json")
    Call<RssResult> getTop100ByCountry(@Path("country") String country);

}