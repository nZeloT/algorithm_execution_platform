package org.nzelot.execution.platform.gui.util;

import javafx.scene.control.Dialog;
import javafx.stage.Window;

public class DialogUtils {

    public static void positionCenteredOnWindow(Window parentWin, Dialog diag) {
        var parentBounds = parentWin.getScene().getRoot().getLayoutBounds();

        diag.getDialogPane().layout();
        var dialogBounds = diag.getDialogPane().getLayoutBounds();

        diag.setX(parentWin.getX() + (parentBounds.getWidth() - dialogBounds.getWidth())/2);
        diag.setY(parentWin.getY() + (parentBounds.getHeight() - dialogBounds.getHeight())/2);
    }

}
