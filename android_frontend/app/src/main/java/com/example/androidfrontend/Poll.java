package com.example.androidfrontend;

import java.util.List;

/**
 * Model class representing a poll.
 */
public class Poll {
    private String id;
    private String title;
    private List<String> options;
    private List<Integer> results;

    public Poll(String id, String title, List<String> options, List<Integer> results) {
        this.id = id;
        this.title = title;
        this.options = options;
        this.results = results;
    }

    // PUBLIC_INTERFACE
    public String getId() { return id; }
    public String getTitle() { return title; }
    public List<String> getOptions() { return options; }
    public List<Integer> getResults() { return results; }
}
