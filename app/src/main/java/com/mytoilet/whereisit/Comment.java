package com.mytoilet.whereisit;

import java.util.List;

public class Comment {
    public int comment_id;
    public List<Integer> rating;
    public List<String> contents;
    Comment() { }
    Comment(int comment_id, List<Integer> rating, List<String> contents)
    {
        this.comment_id = comment_id;
        this.rating = rating;
        this.contents = contents;
    }
}
