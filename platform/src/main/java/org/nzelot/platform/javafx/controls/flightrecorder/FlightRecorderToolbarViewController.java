package org.nzelot.platform.javafx.controls.flightrecorder;

import javafx.beans.property.BooleanProperty;
import org.nzelot.platform.Platform;
import org.nzelot.platform.eventbus.EventSink;
import org.nzelot.platform.flightrecorder.FlightRecorderControlEvent;
import org.nzelot.platform.flightrecorder.FlightRecorderEvent;
import org.nzelot.platform.javafx.playback.PlaybackTimerEvent;
import org.nzelot.platform.javafx.playback.PlaybackTimerTask;

import java.util.Timer;

public class FlightRecorderToolbarViewController {

  private FlightRecorderToolbarView view;

  protected Timer playbackTimer;
  protected FlightRecorderEvent currentFlightRecorderState;

  public FlightRecorderToolbarViewController(FlightRecorderToolbarView view, BooleanProperty viewVisible) {
    this.view = view;
    this.view.setup(viewVisible);
    setup();
    receiveEvent(new FlightRecorderEvent(0, 0));
    Platform.registerSink(this);
  }

  private void setup() {
    this.view.btnNext.setOnAction(evt -> {
      disableControlButtons();
      Platform.distributeEvent(FlightRecorderControlEvent.PLAYBACK_NEXT_EVENT);
    });
    this.view.btnPrev.setOnAction(evt -> {
      disableControlButtons();
      Platform.distributeEvent(FlightRecorderControlEvent.PLAYBACK_PREV_EVENT);
    });
    this.view.btnStart.setOnAction(evt -> {
      disableControlButtons();
      Platform.distributeEvent(FlightRecorderControlEvent.CURSOR_TO_BEGINNING);
    });
    this.view.btnEnd.setOnAction(evt -> {
      disableControlButtons();
      Platform.distributeEvent(FlightRecorderControlEvent.CURSOR_TO_END);
    });
    this.view.btnRunAutomatically.setOnAction(evt -> handlePlaybackButton());
  }

  private void handlePlaybackButton() {
    if(playbackTimer != null){ //currently playing back, so stop now and reset the timer
      playbackTimer.cancel();
      playbackTimer = null;
      this.view.btnRunAutomatically.setText("Playback");
      this.view.spiTiming.setDisable(false);
      receiveEvent(currentFlightRecorderState);
    }else{
      var delay = this.view.spiTiming.getValue();
      playbackTimer = new Timer();
      disableControlButtons();
      this.view.btnRunAutomatically.setText("Stop");
      this.view.btnRunAutomatically.setDisable(false);
      this.view.spiTiming.setDisable(true);
      playbackTimer.schedule(new PlaybackTimerTask(currentFlightRecorderState), delay, delay);
    }
  }

  @EventSink(FlightRecorderEvent.class)
  public void receiveEvent(FlightRecorderEvent event) {
    javafx.application.Platform.runLater(() -> {
      this.currentFlightRecorderState = event;
      this.view.lblProg.setText(event.getCursorPos() + " / " + event.getNumEvents());

      if(this.playbackTimer == null) {
        this.view.btnPrev.setDisable(event.getCursorPos() == 1 || event.getCursorPos() == 0);
        this.view.btnStart.setDisable(event.getCursorPos() == 1 || event.getCursorPos() == 0);
        this.view.btnNext.setDisable(event.getCursorPos() == event.getNumEvents());
        this.view.btnEnd.setDisable(event.getCursorPos() == event.getNumEvents());
        this.view.btnRunAutomatically.setDisable(event.getCursorPos() == event.getNumEvents());
      }
    });
  }

  @EventSink(PlaybackTimerEvent.class)
  public void stopPlaybackTimer(PlaybackTimerEvent event){
    javafx.application.Platform.runLater(() -> {
      if(event == PlaybackTimerEvent.FINISHED){
        handlePlaybackButton();
      }
    });
  }

  private void disableControlButtons(){
    this.view.btnNext.setDisable(true);
    this.view.btnPrev.setDisable(true);
    this.view.btnStart.setDisable(true);
    this.view.btnEnd.setDisable(true);
    this.view.btnRunAutomatically.setDisable(true);
  }

}
