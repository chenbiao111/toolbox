package com.shixin.app.utils;

import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoProcessorUtils {

    //从视频中分离音频
    public static boolean splitAudioFile(String inputPath, String audioPath) throws IOException {
        MediaMuxer mediaMuxer = null;

        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(inputPath);

        int audioTrackIndex = -1;
        for (int index = 0; index < mediaExtractor.getTrackCount(); index++) {
            MediaFormat format = mediaExtractor.getTrackFormat(index);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (!mime.startsWith("audio/")) {
                continue;
            }
            mediaExtractor.selectTrack(index);

            mediaMuxer = new MediaMuxer(audioPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            audioTrackIndex = mediaMuxer.addTrack(format);
            mediaMuxer.start();
        }

        if (mediaMuxer == null) {
            return false;
        }
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        info.presentationTimeUs = 0;
        ByteBuffer buffer = ByteBuffer.allocate(500 * 1024);
        int sampleSize = 0;
        while ((sampleSize = mediaExtractor.readSampleData(buffer, 0)) > 0) {
            info.offset = 0;
            info.size = sampleSize;
            info.flags = mediaExtractor.getSampleFlags();
            info.presentationTimeUs = mediaExtractor.getSampleTime();
            mediaMuxer.writeSampleData(audioTrackIndex, buffer, info);
            mediaExtractor.advance();
        }

        mediaExtractor.release();
        mediaMuxer.stop();
        mediaMuxer.release();

        return true;
    }
    

    private static void savePicFile(Bitmap bitmap, String savePath) throws IOException {
        if (bitmap == null) {
            return;
        }
        File file = new File(savePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
    }
}
