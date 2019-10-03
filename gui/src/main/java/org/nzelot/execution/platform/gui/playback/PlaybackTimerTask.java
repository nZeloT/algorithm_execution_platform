package org.nzelot.execution.platform.gui.playback;

import org.nzelot.execution.platform.core.Platform;
import org.nzelot.execution.platform.core.flightrecorder.FlightRecorderControlEvent;
import org.nzelot.execution.platform.core.flightrecorder.FlightRecorderEvent;

import java.util.TimerTask;

public class PlaybackTimerTask extends TimerTask {

    private int numRounds;
    private int currentRound;

    public PlaybackTimerTask(FlightRecorderEvent currentFRState) {
        numRounds = currentFRState.getNumEvents();
        currentRound = currentFRState.getCursorPos();
    }

    @Override
    public void run() {
        if(currentRound >= numRounds) {
            cancel();
            Platform.distributeEvent(PlaybackTimerEvent.FINISHED);
        }else {
            Platform.distributeEvent(FlightRecorderControlEvent.PLAYBACK_NEXT_EVENT);
            currentRound++;
        }
    }
}
