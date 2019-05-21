package com.mytoilet.whereisit;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.mytoilet.R;

public class CommentDialog implements Dialog{
    Context context;
    public CommentDialog(Context context)
    {
        this.context=context;
    }
    @Override
    public void openDialog() {
        android.app.Dialog dlg = new android.app.Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.comments_dialog);
        dlg.show();
        Button closeButton = (Button)dlg.findViewById(R.id.comment_close);
        closeButton.setOnClickListener(v -> dlg.dismiss());
    }
}
