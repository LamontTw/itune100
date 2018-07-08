package com.lamont.demo.itunetop100.database.model;

import com.lamont.demo.itunetop100.itunes.RssResult;

import java.util.ArrayList;
import java.util.List;

public class ITuneItem {
    private long id;
    private int rank;
    private String artworkUrl100 = ""; // icon
    private String songName = "";
    private String artistName = "";


    public ITuneItem() {

    }

    public ITuneItem(RssResult.result result) {
        this.artworkUrl100 = result.artworkUrl100;
        this.songName = result.name;
        this.artistName = result.artistName;
    }


    public static List<ITuneItem> convertITuneItemList(RssResult.result[] results) {
        List<ITuneItem> itemList = new ArrayList<>();
        for (int idx = 0 ; idx < results.length; idx++) {
            RssResult.result result = results[idx];
            ITuneItem iTuneItem = new ITuneItem(result);
            iTuneItem.setRank(idx);
            itemList.add(iTuneItem);
        }
        return itemList;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRank() { return  rank;}
    public void setRank(int rank) { this.rank = rank; }

    public String getArtistName() {
        return artistName;
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }
    public void setArtworkUrl100(String artworkUrl100) {
        this.artworkUrl100 = artworkUrl100;
    }

    public String getSongName() {
        return songName;
    }
    public void setSongName(String songName) {
        this.songName = songName;
    }
}