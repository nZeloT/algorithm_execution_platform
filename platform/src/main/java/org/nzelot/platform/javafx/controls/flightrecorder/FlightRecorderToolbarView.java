package org.nzelot.platform.javafx.controls.flightrecorder;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToolBar;

public class FlightRecorderToolbarView extends ToolBar {

  Button btnStart;
  Button btnEnd;
  Button btnNext;
  Button btnPrev;
  Label lblProg;

  Button btnRunAutomatically;
  Spinner<Integer> spiTiming;

  public FlightRecorderToolbarView() {
    getItems().add(btnStart = new Button("|<"));
    getItems().add(btnPrev = new Button("<"));
    getItems().add(lblProg = new Label("0 / 0"));
    getItems().add(btnNext = new Button(">"));
    getItems().add(btnEnd = new Button(">|"));
    getItems().add(btnRunAutomatically = new Button("Playback"));
    getItems().add(spiTiming = new Spinner<>(50, 5000, 150, 50));
  }

  void setup(BooleanProperty viewVisible) {
    btnEnd.visibleProperty().bind(viewVisible);
    btnStart.visibleProperty().bind(viewVisible);
    btnNext.visibleProperty().bind(viewVisible);
    btnPrev.visibleProperty().bind(viewVisible);
    lblProg.visibleProperty().bind(viewVisible);
    btnRunAutomatically.visibleProperty().bind(viewVisible);
    spiTiming.visibleProperty().bind(viewVisible);

    btnStart.managedProperty().bind(btnStart.visibleProperty());
    btnNext.managedProperty().bind(btnNext.visibleProperty());
    btnPrev.managedProperty().bind(btnPrev.visibleProperty());
    btnEnd.managedProperty().bind(btnEnd.visibleProperty());
    lblProg.managedProperty().bind(lblProg.visibleProperty());
    btnRunAutomatically.managedProperty().bind(btnRunAutomatically.visibleProperty());
    spiTiming.managedProperty().bind(spiTiming.visibleProperty());
  }
}
