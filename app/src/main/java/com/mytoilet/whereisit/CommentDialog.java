package com.mytoilet.whereisit;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mytoilet.R;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class CommentDialog implements Dialog{

    private Context context;
    private Realm mRealm;
    private Toilet toilet;
    private ArrayAdapter<String> adapter;

    public CommentDialog(Context context, Realm mRealm, Toilet toilet)
    {
        this.context=context;
        this.mRealm=mRealm;
        this.toilet = toilet;

    }
    @Override
    public void openDialog() {
        android.app.Dialog dlg = new android.app.Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.comments_dialog);
        dlg.show();

        RatingBar rating = (RatingBar)dlg.findViewById(R.id.rating);
        TextView rating_text = (TextView)dlg.findViewById(R.id.ratingText);
        ListView commentList = (ListView)dlg.findViewById(R.id.commentList);

        final Comment comment = mRealm.where(Comment.class).equalTo("comment_id",toilet.toilet_id).findFirst();
        if(comment==null)
        {
            rating.setRating(0);
            rating_text.setText(String.valueOf(rating.getRating()));
            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        }
        else
        {
            rating.setRating(comment.getRating());
            rating_text.setText(String.valueOf(rating.getRating()));
            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, comment.getComments());
            commentList.setAdapter(adapter);
        }

        Button addCommentButton = (Button)dlg.findViewById(R.id.addComment);
        addCommentButton.setOnClickListener(v -> {
            final RealmResults<Comment> comments = mRealm.where(Comment.class).equalTo("comment_id",toilet.toilet_id).findAll();

            if(comments.size()==0)
            {
                mRealm.beginTransaction();
                Comment temp_comment = mRealm.createObject(Comment.class,toilet.toilet_id);
                RatingBar userRating = (RatingBar)dlg.findViewById(R.id.user_rating);
                float userRate = userRating.getRating();
                temp_comment.setRating(userRate);
                EditText content = (EditText)dlg.findViewById(R.id.commentText);
                String text = content.getText().toString();
                adapter.add(text);
                temp_comment.setSum(userRate);
                temp_comment.addComment(text);
                mRealm.commitTransaction();
                rating.setRating(userRate);

                adapter.notifyDataSetChanged();
            }
            else
            {
                Comment temp_comment = comments.first();
                mRealm.beginTransaction();
                RealmList<String> contents = temp_comment.getComments();
                EditText content = (EditText)dlg.findViewById(R.id.commentText);
                String text = content.getText().toString();
                RatingBar userRating = (RatingBar)dlg.findViewById(R.id.user_rating);
                temp_comment.addComment(text);
                float sum = temp_comment.getSum()+userRating.getRating();
                temp_comment.setSum(sum);
                temp_comment.setRating(sum/contents.size());
                adapter.add(text);
                rating.setRating(temp_comment.getRating());
                mRealm.commitTransaction();


                adapter.notifyDataSetChanged();
            }
        });
        Button closeButton = (Button)dlg.findViewById(R.id.comment_close);
        closeButton.setOnClickListener(v -> {
            dlg.dismiss();
            mRealm.close();
        });
    }
}
