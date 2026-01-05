package pantoja.chip8.ux;

public class Keypad {
    private final boolean[] keys;
    private int pressedSinceWait;
    private boolean waitingForKeyPress;


    public Keypad() {
        keys = new boolean[16];
        pressedSinceWait = -1;
        waitingForKeyPress = false;
    }


    public synchronized void press(int key) {
        if (key < 0 || key >= keys.length) {
            return;
        }

        if (!keys[key]) {
            keys[key] = true;
        }
    }


    public synchronized void release(int key) {
        if (key < 0 || key >= keys.length) {
            return;
        }
        if (keys[key]) {
            keys[key] = false;
            if (waitingForKeyPress) {
                pressedSinceWait = key;
            }
        }
    }


    public synchronized boolean isPressed(int key) {
        if (key < 0 || key >= keys.length) {
            return false;
        }
        return keys[key];
    }


    // loadKeyToReg (Fx0A - LD Vx, K) implementation - Waits for key RELEASE
    public synchronized int pollForKeyPress() {
        // First call, setup blocked until we have a pending key
        if (!waitingForKeyPress) {
            waitingForKeyPress = true;
            pressedSinceWait = -1;
            return pressedSinceWait;
        }
        // Currently waiting, but no pending key (no key arrived)
        else if (pressedSinceWait == -1) {
            return pressedSinceWait;
        } else {
            waitingForKeyPress = false;
            int keyToReturn = pressedSinceWait;
            pressedSinceWait = -1;
            return keyToReturn;
        }
    }
}
