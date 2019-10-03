package org.nzelot.platform.javafx.result;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Map;

public class ResultDialog extends Dialog {

    public ResultDialog(String algorithmName, Map<String, Object> results) {
        setHeaderText(algorithmName + " results ...");
        setTitle("Results for " + algorithmName);
        getDialogPane().getButtonTypes().add(ButtonType.OK);

        GridPane dialogGrid = new GridPane();
        dialogGrid.setVgap(10);
        dialogGrid.setHgap(10);
        dialogGrid.setPadding(new Insets(20, 150, 10, 10));

        var row = 0;
        for (var entry : results.entrySet()) {
            dialogGrid.add(new Label(entry.getKey()), 0, row);
            dialogGrid.add(new Label(entry.getValue().toString()), 1, row);
            row++;
        }

        getDialogPane().setContent(dialogGrid);
    }
}
