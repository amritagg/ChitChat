package com.amrit.practice.chitchat.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.amrit.practice.chitchat.R;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ShowImageActivity extends AppCompatActivity {

    private static final String LOG_TAG = ShowImageActivity.class.getSimpleName();
    public static final String INTENT_FLAG = "for_result";
    public static final String INTENT_URI = "uri";
    private boolean show = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        show = getIntent().getIntExtra(INTENT_FLAG, 0) != 0;

        ImageButton cut = findViewById(R.id.cancel);
        EditText frame_edit_view = findViewById(R.id.frame_edit_view);
        FloatingActionButton returnData = findViewById(R.id.frame_send_button);

        if(show){
            cut.setVisibility(View.VISIBLE);
            frame_edit_view.setVisibility(View.VISIBLE);
            returnData.setVisibility(View.VISIBLE);

            ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.hide();

            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }else{
            cut.setVisibility(View.GONE);
            frame_edit_view.setVisibility(View.GONE);
            returnData.setVisibility(View.GONE);
        }

        String uri = getIntent().getStringExtra(INTENT_URI);
        PhotoView imageView = findViewById(R.id.show_selected_image);
        Glide.with(this).load(Uri.parse(uri)).into(imageView);
//        imageView.setImageURI(Uri.parse(uri));

        cut.setOnClickListener(view -> finish());
        returnData.setOnClickListener(view -> {
            Intent returnIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("mediaUri", uri);
            bundle.putString("mediaMessage", frame_edit_view.getText().toString());
            returnIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });

    }
}