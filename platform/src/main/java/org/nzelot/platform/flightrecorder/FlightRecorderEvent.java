package org.nzelot.platform.flightrecorder;

public class FlightRecorderEvent {
    private int cursorPos;
    private int numEvents;

    public FlightRecorderEvent(int cursorPos, int numEvents) {
        this.cursorPos = cursorPos;
        this.numEvents = numEvents;
    }

    public int getCursorPos() {
        return cursorPos;
    }

    public int getNumEvents() {
        return numEvents;
    }
}
