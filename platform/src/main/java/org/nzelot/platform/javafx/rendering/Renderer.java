package org.nzelot.platform.javafx.rendering;

import javafx.scene.canvas.GraphicsContext;

public interface Renderer {
    void render(GraphicsContext ctx, double zoomLevel, double offsetX, double offsetY);
    void reset();
}
