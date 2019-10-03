package org.nzelot.execution.platform.gui.controls.algorithm;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToolBar;

public class AlgorithmToolbarView extends ToolBar {

  Button btnCancle;
  ProgressBar progressBar;

  public AlgorithmToolbarView() {
    getItems().add(btnCancle = new Button("Abbrechen"));
    getItems().add(progressBar = new ProgressBar(-1));
  }

  void setup(BooleanProperty visibleProperty) {
    btnCancle.visibleProperty().bind(visibleProperty);
    progressBar.visibleProperty().bind(visibleProperty);

    btnCancle.managedProperty().bind(btnCancle.visibleProperty());
    progressBar.managedProperty().bind(progressBar.visibleProperty());
  }
}
