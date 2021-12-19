package com.amrit.practice.chitchat.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amrit.practice.chitchat.Adapters.ImageAdapter;
import com.amrit.practice.chitchat.Loader.ImageLoader;
import com.amrit.practice.chitchat.R;

import java.util.ArrayList;

public class ImagePickerActivity extends AppCompatActivity
                implements LoaderManager.LoaderCallbacks<ArrayList<String>>,
                            ImageAdapter.OnImageClickListener {

    private static final String LOG_TAG = ImagePickerActivity.class.getSimpleName();

    private RecyclerView mImage;
    private ProgressBar progressBar;
    ArrayList<String> mediaList;

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{"android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        checkPermissionStatus();
        progressBar = findViewById(R.id.image_progress_bar);

        int loaderManger_ID = 10;
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        loaderManager.initLoader(loaderManger_ID, null, this);

    }

    @NonNull
    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, @Nullable Bundle args) {
        mImage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        return new ImageLoader(getApplicationContext());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<String>> loader, ArrayList<String> data) {
        mImage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        Log.e(LOG_TAG, data.size() + "");
        mediaList = data;
        ImageAdapter mImageAdapter = new ImageAdapter(this, data, this);
        mImage.setAdapter(mImageAdapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<String>> loader) { }

    ActivityResultLauncher<Intent> getImageUri = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    assert data != null;
                    Bundle bundle = data.getExtras();
                    Intent mainIntent = new Intent();
                    mainIntent.putExtras(bundle);
                    setResult(Activity.RESULT_OK, mainIntent);
                    finish();
                }
            }
    );

    @Override
    public void OnImageClick(int position) {
        Intent intent = new Intent(this, ShowImageActivity.class);
        intent.putExtra(ShowImageActivity.INTENT_FLAG, 1);
        intent.putExtra("uri", mediaList.get(position));
        getImageUri.launch(intent);
    }

    private void initialiseRecyclerView(){
        mImage = findViewById(R.id.image_picker_recycler_view);
        mImage.setNestedScrollingEnabled(false);
        mImage.setHasFixedSize(false);
        GridLayoutManager mImageLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        mImage.setLayoutManager(mImageLayoutManager);
    }

    //  Method for permission handling
    private void checkPermissionStatus() {

//      check weather the permission is given or not
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initialiseRecyclerView();

        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                if not permission is denied than show the message to user showing him the benefits of the permission.
                Toast.makeText(this, "Permission is needed to show the Media..", Toast.LENGTH_SHORT).show();
                finish();
            }
//            request permission
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initialiseRecyclerView();
            } else {
                Toast.makeText(this, "Permissions not granted!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}