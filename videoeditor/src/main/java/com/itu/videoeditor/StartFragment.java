package com.itu.videoeditor;

import android.app.Fragment;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import VideoHandle.OnEditorListener;
import VideoHandle.VEDraw;
import VideoHandle.VEEditor;
import VideoHandle.VEText;
import VideoHandle.VEVideo;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.itu.videoeditor.MainActivity.PICK_IMAGE_MULTIPLE;

/**
 * Created by Jia Liu on 3/17/2019.
 */
public class StartFragment extends Fragment {
    private MainActivity mMainActivity;
    ImageButton addButton;

//    String imageEncoded;
//    List<String> imagesEncodedList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.start_screen, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mMainActivity = (MainActivity) getActivity();

        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mMainActivity.mNavigationManager.startGalleryScreen();

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/* video/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: "+requestCode+"    "+resultCode);

        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
//                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();
                    Log.e(TAG, "If: "+mImageUri.toString());
                    // Get the cursor
                    Cursor cursor = mMainActivity.getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        mMainActivity.mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            Log.e(TAG, "else: "+uri.toString());

                            mMainActivity.mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = mMainActivity.getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                            imageEncoded  = cursor.getString(columnIndex);
//                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }
                        Log.e(TAG, "Selected Images: " + mMainActivity.mArrayUri.size());
                        mMainActivity.mNavigationManager.startOperationScreen(mMainActivity.mArrayUri);
                    }
                }
            } else {
//                Toast.makeText(this, "You haven't picked Image",
//                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
//                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
