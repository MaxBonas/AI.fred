package com.example.streambot;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to capture audio from the microphone.
 */
public class AudioRecorder implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AudioRecorder.class);
    private final String deviceName;
    private final Thread thread;
    private volatile boolean running;
    private volatile boolean started;
    private TargetDataLine line;
    private ByteArrayOutputStream buffer;

    public AudioRecorder() {
        this(null);
    }

    public AudioRecorder(String deviceName) {
        this.deviceName = deviceName;
        this.thread = new Thread(this, "audio-recorder");
    }

    /** Start capturing audio in a background thread. */
    public synchronized void start() {
        if (started || thread.isAlive()) {
            return;
        }
        started = true;
        running = true;
        thread.start();
    }

    /**
     * Stop capturing and return the recorded bytes.
     * The audio is PCM 16-bit, 16 kHz mono.
     */
    public byte[] stop() {
        running = false;
        try {
            thread.join(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (line != null) {
            line.stop();
            line.close();
        }
        return buffer != null ? buffer.toByteArray() : new byte[0];
    }

    @Override
    public void run() {
        AudioFormat fmt = new AudioFormat(16000f, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, fmt);
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
            buffer = new ByteArrayOutputStream();
            byte[] data = new byte[4096];
            while (running) {
                int n = line.read(data, 0, data.length);
                if (n > 0) {
                    buffer.write(data, 0, n);
                }
            }
        } catch (LineUnavailableException e) {
            logger.warn("Micrófono no disponible", e);
        } catch (Exception e) {
            logger.warn("Error al grabar audio", e);
        } finally {
            if (line != null) {
                line.close();
            }
        }
    }
}
