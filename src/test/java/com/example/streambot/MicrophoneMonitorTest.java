package com.example.streambot;

import org.junit.jupiter.api.Test;

/** Tests for the MicrophoneMonitor utility. */
public class MicrophoneMonitorTest {

    @Test
    public void startCanBeCalledTwice() {
        MicrophoneMonitor mon = new MicrophoneMonitor(() -> {}) {
            @Override
            public void run() { /* no-op */ }
        };

        mon.start();
        // second start should not throw IllegalThreadStateException
        mon.start();
        mon.stop();
    }
}
