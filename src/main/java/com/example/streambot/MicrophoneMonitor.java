package com.example.streambot;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.Mixer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple microphone monitor that triggers a callback when sound is detected.
 */
public class MicrophoneMonitor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MicrophoneMonitor.class);
    private final Runnable onActivity;
    private final Thread thread;
    private final String deviceName;
    private volatile boolean running;

    public MicrophoneMonitor(Runnable onActivity) {
        this(onActivity, null);
    }

    public MicrophoneMonitor(Runnable onActivity, String deviceName) {
        this.onActivity = onActivity != null ? onActivity : () -> {};
        this.deviceName = deviceName;
        this.thread = new Thread(this, "mic-monitor");
    }

    /** Start capturing audio in a background thread. */
    public void start() {
        running = true;
        thread.start();
    }

    /** Stop capturing audio and wait briefly for the thread to finish. */
    public void stop() {
        running = false;
        try {
            thread.join(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        AudioFormat fmt = new AudioFormat(16000f, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, fmt);
        TargetDataLine line = null;
        try {
            if (deviceName != null && !deviceName.isBlank()) {
                for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
                    if (deviceName.equals(mi.getName())) {
                        Mixer m = AudioSystem.getMixer(mi);
                        if (m.isLineSupported(info)) {
                            line = (TargetDataLine) m.getLine(info);
                        }
                        break;
                    }
                }
            }
            if (line == null) {
                line = (TargetDataLine) AudioSystem.getLine(info);
            }
            line.open(fmt);
            line.start();
            byte[] buf = new byte[1024];
            while (running) {
                int read = line.read(buf, 0, buf.length);
                if (read > 0 && isLoud(buf, read)) {
                    onActivity.run();
                }
            }
        } catch (LineUnavailableException e) {
            logger.warn("Microphone unavailable", e);
        } catch (Exception e) {
            logger.warn("Error reading microphone", e);
        } finally {
            if (line != null) {
                line.close();
            }
        }
    }

    /**
     * Basic amplitude check for activity.
     */
    protected boolean isLoud(byte[] data, int len) {
        long sum = 0;
        for (int i = 0; i < len - 1; i += 2) {
            int sample = (data[i + 1] << 8) | (data[i] & 0xFF);
            sum += Math.abs(sample);
        }
        double avg = sum / (len / 2.0);
        return avg > 5000; // arbitrary threshold
    }
}
