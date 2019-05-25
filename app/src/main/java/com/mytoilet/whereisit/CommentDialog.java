package com.mytoilet.whereisit;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytoilet.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class CommentDialog implements Dialog{

    private Context context;
    private Realm mRealm;
    private Toilet toilet;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> lists;


    public CommentDialog(Context context, Realm mRealm, Toilet toilet)
    {
        this.context=context;
        this.mRealm=mRealm;
        this.toilet = toilet;
        lists = new ArrayList<>();
    }
    @Override
    public void openDialog() {
        android.app.Dialog dlg = new android.app.Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.comments_dialog);
        dlg.show();

        Button addCommentButton = (Button)dlg.findViewById(R.id.addComment);
        addCommentButton.setEnabled(false);
        RatingBar rating = (RatingBar)dlg.findViewById(R.id.rating);
        TextView rating_text = (TextView)dlg.findViewById(R.id.ratingText);
        ListView commentList = (ListView)dlg.findViewById(R.id.commentList);
        final EditText[] content = {(EditText) dlg.findViewById(R.id.commentText)};
        content[0].addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0)
                {
                    addCommentButton.setEnabled(false);
                }
                else if(s.toString().trim().length()>5)
                {
                    addCommentButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final Comment comment = mRealm.where(Comment.class).equalTo("comment_id",toilet.toilet_id).findFirst();
        if(comment==null)
        {
            rating.setRating(0);
            rating_text.setText(String.valueOf(rating.getRating()));
            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,lists);
            commentList.setAdapter(adapter)
        }
        else
        {
            rating.setRating(comment.getRating());
            rating_text.setText(String.valueOf(rating.getRating()));
            for(String item:comment.getComments())
            {
                lists.add(item);
            }
            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, lists);
            commentList.setAdapter(adapter);
        }


        addCommentButton.setOnClickListener(v -> {
            final RealmResults<Comment> comments = mRealm.where(Comment.class).equalTo("comment_id",toilet.toilet_id).findAll();

            if(comments.size()==0)
            {
                mRealm.beginTransaction();
                Comment temp_comment = mRealm.createObject(Comment.class,toilet.toilet_id);
                RatingBar userRating = (RatingBar)dlg.findViewById(R.id.user_rating);
                float userRate = userRating.getRating();
                temp_comment.setRating(userRate);

                String text = content[0].getText().toString();
                lists.add(text);
                temp_comment.setSum(userRate);
                temp_comment.addComment(text);
                mRealm.commitTransaction();
                rating.setRating(userRate);
                rating_text.setText(String.valueOf(rating.getRating()));
                adapter.notifyDataSetChanged();
                content[0].setText("");
            }
            else
            {
                Comment temp_comment = comments.first();
                mRealm.beginTransaction();
                RealmList<String> contents = temp_comment.getComments();
                content[0] = (EditText)dlg.findViewById(R.id.commentText);
                String text = content[0].getText().toString();
                RatingBar userRating = (RatingBar)dlg.findViewById(R.id.user_rating);
                temp_comment.addComment(text);
                float sum = temp_comment.getSum()+userRating.getRating();
                temp_comment.setSum(sum);
                temp_comment.setRating(sum/contents.size());
                lists.add(text);
                adapter.notifyDataSetChanged();
                rating.setRating(temp_comment.getRating());
                rating_text.setText(String.valueOf(rating.getRating()));
                mRealm.commitTransaction();
                content[0].setText("");
            }
            Toast test = Toast.makeText(context, "댓글 입력이 완료되었습니다.", Toast.LENGTH_LONG);
            test.show();
        });

        Button closeButton = (Button)dlg.findViewById(R.id.comment_close);
        closeButton.setOnClickListener(v -> {
            dlg.dismiss();
            mRealm.close();
        });
    }
}
