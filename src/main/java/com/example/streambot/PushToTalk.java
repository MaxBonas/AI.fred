package com.example.streambot;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.streambot.ChatBotController;

/**
 * Listener that records audio while F12 is held down and processes the speech
 * when released.
 */
public class PushToTalk implements NativeKeyListener {
    private static final Logger logger = LoggerFactory.getLogger(PushToTalk.class);
    private final ChatBotController controller;
    private AudioRecorder recorder;
    private boolean pushToTalkActive;

    public PushToTalk(ChatBotController controller) {
        this.controller = controller;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F12 && !pushToTalkActive) {
            logger.debug("F12 pressed - starting recorder");
            recorder = new AudioRecorder();
            recorder.start();
            pushToTalkActive = true;
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F12 && pushToTalkActive) {
            logger.debug("F12 released - stopping recorder");
            byte[] audio = recorder != null ? recorder.stop() : new byte[0];
            pushToTalkActive = false;
            String transcript = SpeechToText.transcribe(audio);
            if (controller != null) {
                controller.onUserSpeech(transcript);
            }
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // No-op
    }
}
