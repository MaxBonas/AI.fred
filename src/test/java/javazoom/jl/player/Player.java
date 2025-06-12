package javazoom.jl.player;

import java.io.InputStream;

/** Simple stub for JLayer Player used in tests. */
public class Player {
    public static boolean played = false;
    public final InputStream in;
    public Player(InputStream in) {
        this.in = in;
    }
    public void play() {
        played = true;
    }
}
