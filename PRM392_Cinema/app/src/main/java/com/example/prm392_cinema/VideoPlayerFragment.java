package com.example.prm392_cinema;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class VideoPlayerFragment extends DialogFragment {

    private static final String ARG_VIDEO_URL = "video_url";
    private VideoView videoView;
    private ImageButton playPauseButton, forwardButton, rewindButton, closeButton;
    private SeekBar seekBar;
    private Handler handler = new Handler();

    public static VideoPlayerFragment newInstance(String videoUrl) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_URL, videoUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoView = view.findViewById(R.id.video_view);
        playPauseButton = view.findViewById(R.id.play_pause_button);
        forwardButton = view.findViewById(R.id.forward_button);
        rewindButton = view.findViewById(R.id.rewind_button);
        closeButton = view.findViewById(R.id.close_button);
        seekBar = view.findViewById(R.id.seek_bar);

        String videoUrl = getArguments().getString(ARG_VIDEO_URL);

        if (videoUrl != null && !videoUrl.isEmpty()) {
            Uri videoUri = Uri.parse(videoUrl);
            videoView.setVideoURI(videoUri);

            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                seekBar.setMax(videoView.getDuration());
                videoView.start();
                updateSeekBar();
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            });

            videoView.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(getContext(), "Không thể phát video này.", Toast.LENGTH_SHORT).show();
                dismiss();
                return true;
            });

            closeButton.setOnClickListener(v -> dismiss());

            playPauseButton.setOnClickListener(v -> {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    videoView.start();
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                }
            });

            forwardButton.setOnClickListener(v -> videoView.seekTo(videoView.getCurrentPosition() + 5000));
            rewindButton.setOnClickListener(v -> videoView.seekTo(videoView.getCurrentPosition() - 5000));

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        videoView.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        } else {
            Toast.makeText(getContext(), "Không có URL video.", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    private void updateSeekBar() {
        if (videoView.getDuration() > 0) {
            seekBar.setProgress(videoView.getCurrentPosition());
        }

        Runnable runnable = this::updateSeekBar;
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }
}
