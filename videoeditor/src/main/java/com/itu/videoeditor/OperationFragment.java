package com.itu.videoeditor;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import VideoHandle.OnEditorListener;
import VideoHandle.VEDraw;
import VideoHandle.VEEditor;
import VideoHandle.VEText;
import VideoHandle.VEVideo;

import static android.content.ContentValues.TAG;

/**
 * Created by Jia Liu on 3/17/2019.
 */
public class OperationFragment extends Fragment {
    private MainActivity mMainActivity;
    ArrayList<Uri> mUriList;

    int mCurrentRotation = 0;
    boolean mCurrentMirror = false;
    VEVideo mVideo;
    Button importButton;
    Button cutButton;
//    Button cropButton;
//    Button rotateButton;
//    Button mirrorButton;
    Button addTextButton;
    Button addLogoButton;
    Button addFilterButton;
    Button exportButton;
    Button addBackgroundButton;
    Button separateVideoMusicButton;
    Button changeSpeedButton;
    Button musicRevertPlayButton;
    ProgressBar mProgressBar;

    ImageButton trimButton;
    ImageButton speedButton;
    ImageButton rotateButton;
    ImageButton mirrorButton;
    ImageButton cropButton;
    ImageButton textButton;
    ImageView mImageView;

    String videoPath="sdcard/VideoEditor/original/test.mp4";
    String outfilePath="sdcard/VideoEditor/output/newTest.mp4";
    String imagePath="sdcard/VideoEditor/original/image.jpg";
    String ttfPath="sdcard/VideoEditor/original/Montserrat-Regular.ttf";
    String audioPath="sdcard/VideoEditor/original/TheMonster.mp3";

    public void setUriList(ArrayList<Uri> uriList){
        mUriList = uriList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.operation_screen, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mMainActivity = (MainActivity) getActivity();

        mImageView = view.findViewById(R.id.previewImageView);
        mImageView.setImageURI(mUriList.get(0));

        importButton = view.findViewById(R.id.importButton);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVideo = new VEVideo("sdcard/VideoEditor/original/test.mp4");

            }
        });

        cutButton = view.findViewById(R.id.cutButton);
        cutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVideo.clip(10,20);//from 10th sec cut 20 sec

            }
        });
        trimButton = view.findViewById(R.id.trimButton);

        cropButton = view.findViewById(R.id.cropButton);
        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVideo.crop(480,360,0,0);
            }
        });

        rotateButton = view.findViewById(R.id.rotateButton);
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentRotation+=90;
                mVideo.rotation(mCurrentRotation%360, false);
            }
        });

        mirrorButton = view.findViewById(R.id.mirrorButton);
        mirrorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentMirror = !mCurrentMirror;
                mVideo.rotation(0, mCurrentMirror);
            }
        });

        textButton = view.findViewById(R.id.textButton);
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVideo.addText(new VEText(10,10,35, VEText.Color.Green,ttfPath,"Add fancy text",new VEText.Time(3,10)));
            }
        });

//        addLogoButton = view.findViewById(R.id.addLogoButton);
//        addLogoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mVideo.addDraw(new VEDraw(imagePath,10,10,50,50,false,10,10));//最后两个参数为显示的起始时间和持续时间
//            }
//        });
//
//        addFilterButton = view.findViewById(R.id.addFilterButton);
//        addFilterButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String filter = "lutyuv=y=maxval+minval-val:u=maxval+minval-val:v=maxval+minval-val";//底片效果
//                mVideo.addFilter(filter);
//            }
//        });
        exportButton = view.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processSingleVideo("sdcard/VideoEditor/original/test.mp4",
                        "sdcard/VideoEditor/output/newTest.mp4");
            }
        });
//
//        addBackgroundButton = view.findViewById(R.id.addBackgroundButton);
//        addBackgroundButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                VEEditor.music(videoPath, audioPath, outfilePath, 1, 0.7f, new OnEditorListener() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onFailure() {
//
//                    }
//
//                    @Override
//                    public void onProgress(float progress) {
//
//                    }
//                });
//            }
//        });
//        separateVideoMusicButton = view.findViewById(R.id.separateVideoMusicButton);
//        separateVideoMusicButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                VEEditor.demuxer(videoPath, outfilePath,VEEditor.Format.MP3, new OnEditorListener() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onFailure() {
//
//                    }
//
//                    @Override
//                    public void onProgress(float progress) {
//
//                    }
//                });
//            }
//        });
//
        speedButton = view.findViewById(R.id.speedButton);
        speedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VEEditor.changePTS(videoPath, outfilePath, 2.0f, VEEditor.PTS.ALL, new OnEditorListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure() {

                    }

                    @Override
                    public void onProgress(float progress) {

                    }
                });
            }
        });
//
//        musicRevertPlayButton = view.findViewById(R.id.musicRevertPlayButton);
//        musicRevertPlayButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                VEEditor.reverse(videoPath, outfilePath, true, true, new OnEditorListener() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onFailure() {
//
//                    }
//
//                    @Override
//                    public void onProgress(float progress) {
//
//                    }
//                });
//            }
//        });
//        mProgressBar = view.findViewById(R.id.progressBar);
    }

    void processSingleVideo(String url, String outFile){
        // only mp4 output format is supported for now
        VEEditor.OutputOption outputOption = new VEEditor.OutputOption(outFile);
        outputOption.setWidth(480);//Output video width, default is original video width
        outputOption.setHeight(360); // Output video height
        outputOption.frameRate = 30;
        outputOption.bitRate = 10;
        VEEditor.exec(mVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onProgress(float progress) {
                Log.e(TAG, "Processing Speed: "+progress);
                mProgressBar.setProgress((int)(progress*100));
            }
        });
    }



}
