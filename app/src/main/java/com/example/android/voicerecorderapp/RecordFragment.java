package com.example.android.voicerecorderapp;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {


    private NavController navController;

    private ImageButton listBtn;
    private ImageButton recordBtn;
    private Button recordPause;
    private Button recordStopp;
    private ImageButton recordStop;
    private TextView fileNameText;

    private boolean isRecording = false;

    private static String recordPermission = Manifest.permission.RECORD_AUDIO;
    private static int PERMISSION_CODE = 21; // random

    private MediaRecorder mediaRecorder;
    private String recordFile;

    private Chronometer timer;

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       // navController = Navigation.findNavController(view);
     //   listBtn = view.findViewById(R.id.record_list_btn);
        recordBtn = view.findViewById(R.id.record_btn);

       // recordStop = view.findViewById(R.id.record_stop);
        timer = view.findViewById(R.id.record_timer);
        fileNameText = view.findViewById(R.id.record_filename);
        recordPause=view.findViewById(R.id.record_pause);
        recordStopp=view.findViewById(R.id.record_stopp);
//        listBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        recordStopp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
        recordPause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                stoppRecording();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {


            case R.id.record_btn:
                if (isRecording) {
                    // Stop Recording
                    stopRecording();
                    recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped, null));
                    isRecording = false;
                } else {
                    if (checkPermissions()) {
                        // Start Recording
                        startRecording();
                        recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording, null));
                        isRecording = true;
                    }
                }



        }
    }

///
private void stoppRecording() {
    timer.stop();
 //   fileNameText.setText("Recording Stopped, File Saved: " + recordFile);

    mediaRecorder.stop();
    mediaRecorder.release();

    mediaRecorder = null;
}
    ///
    private void stopRecording() {
        timer.stop();
        fileNameText.setText("Recording Stopped, File Saved: " + recordFile);

        mediaRecorder.stop();
        mediaRecorder.release();

        mediaRecorder = null;
    }

    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyy_MM_dd_hh_mm_ss", Locale.ENGLISH);
        Date now = new Date();

        recordFile = "Recording_"  + formatter.format(now) + ".3gp";

        fileNameText.setText("Recording, File Name: " + recordFile);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE );
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording(); // prevent Audio file from crashing
        }
    }
}
