package org.nzelot.platform.flightrecorder;

public class FlightRecorderPlaybackEvent {

    private FlightRecorderPlaybackDirection direction;
    private RecordedEvent previousEvent;
    private RecordedEvent newEvent;

    FlightRecorderPlaybackEvent(FlightRecorderPlaybackDirection direction, RecordedEvent previousEvent, RecordedEvent newEvent) {
        this.direction = direction;
        this.previousEvent = previousEvent;
        this.newEvent = newEvent;
    }

    public FlightRecorderPlaybackDirection getDirection() {
        return direction;
    }

    public RecordedEvent getPreviousEvent() {
        return previousEvent;
    }

    public RecordedEvent getNewEvent() {
        return newEvent;
    }
}
