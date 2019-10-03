package org.nzelot.platform.flightrecorder;

import org.nzelot.platform.eventbus.EventBroker;
import org.nzelot.platform.eventbus.EventSink;

import java.util.ArrayList;
import java.util.List;

public class FlightRecorder {

    private List<RecordedEvent> recordedEvents = new ArrayList<>();
    private int cursorPosition = 0;
    private EventBroker broker;
    private boolean recording;
    private boolean fullDeltaPlayback;

    public FlightRecorder(EventBroker broker) {
        broker.registerSink(this);
        this.broker = broker;
        this.recording = false;
        this.fullDeltaPlayback = false;
    }

    @EventSink
    public void receiveEvent(Object event) {
        if(!recording) {
            return;
        }

        if (event instanceof RecordedEvent) {
            recordedEvents.add((RecordedEvent)event);
            cursorPosition = recordedEvents.size()-1;
            //now drop an event to inform everyone interested about the new FR state
            distributeNewFlightRecorderState();
        }
    }

    @EventSink(FlightRecorderControlEvent.class)
    public void handleControlEvent(FlightRecorderControlEvent event){
        switch (event) {
            case PLAYBACK_NEXT_EVENT:
                nextEvent();
                break;
            case PLAYBACK_PREV_EVENT:
                previousEvent();
                break;
            case CLEAR_RECORDING:
                clearFlightRecorder();
                break;
            case START_RECORDING:
                recording = true;
                break;
            case END_RECORDING:
                recording = false;
                break;
            case CURSOR_TO_BEGINNING:
                cursorToStart();
                break;
            case CURSOR_TO_END:
                cursorToEnd();
                break;
            default:
                break;
        }
    }

    public void nextEvent() {
        if (cursorPosition < recordedEvents.size()-1) {
            broker.distributeEvent(new FlightRecorderPlaybackEvent(
                    FlightRecorderPlaybackDirection.FORWARD,
                    recordedEvents.get(cursorPosition),
                    recordedEvents.get(cursorPosition+1)));
            ++cursorPosition;
            distributeNewFlightRecorderState();
        } else
            throw new IllegalStateException("FlightRecorder has already reached end of tape!");
    }

    public void previousEvent() {
        if (cursorPosition > 0) {
            broker.distributeEvent(new FlightRecorderPlaybackEvent(
                    FlightRecorderPlaybackDirection.BACKWARD,
                    recordedEvents.get(cursorPosition),
                    recordedEvents.get(cursorPosition-1)));
            --cursorPosition;
            distributeNewFlightRecorderState();
        }
    }

    public void clearFlightRecorder() {
        recordedEvents.clear();
        cursorPosition = 0;
        distributeNewFlightRecorderState();
    }

    public void cursorToStart(){
        if(!fullDeltaPlayback) {
            cursorPosition = 0;
            broker.distributeEvent(recordedEvents.get(cursorPosition));
            distributeNewFlightRecorderState();
        }else{
            while(cursorPosition > 0)
                previousEvent();
        }
    }

    public void cursorToEnd() {
        if(!fullDeltaPlayback) {
            cursorPosition = recordedEvents.size() - 1;
            broker.distributeEvent(cursorPosition);
            distributeNewFlightRecorderState();
        }else{
            while(cursorPosition < recordedEvents.size()-1){
                nextEvent();
            }
        }
    }

    private void distributeNewFlightRecorderState() {
        broker.distributeEvent(new FlightRecorderEvent(recordedEvents.size() == 0 ? 0 : cursorPosition+1, recordedEvents.size()));
    }

    public boolean isRecording() {
        return recording;
    }

    public void setFullDeltaPlayback(boolean fullDeltaPlayback) {
        this.fullDeltaPlayback = fullDeltaPlayback;
    }
}
