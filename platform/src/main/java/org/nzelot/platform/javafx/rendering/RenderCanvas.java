package org.nzelot.platform.javafx.rendering;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class RenderCanvas extends Canvas {

    private double offsetX = 0, offsetY = 0;
    private double zoomLevel = 1;

    private double dragStartX, dragStartY;
    private double dragStartOffsetX, dragStartOffsetY;

    private Renderer renderer;

    public RenderCanvas() {
        this(null);
    }

    public RenderCanvas(Renderer renderer) {
        this.renderer = renderer;
        addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        addEventFilter(ScrollEvent.ANY, this::handleScroll);

        widthProperty().addListener(evt -> repaint());
        heightProperty().addListener(evt -> repaint());
    }

    public void repaint() {
        var ctx = getGraphicsContext2D();
        if (renderer != null)
            renderer.render(ctx, zoomLevel, offsetX, offsetY);
    }

    public void resetRenderer() {
        renderer.reset();
    }

    private void handleMouseDragged(MouseEvent evt) {
        var dragTransformX = evt.getX() - dragStartX;
        var dragTransformY = evt.getY() - dragStartY;
        offsetX = dragStartOffsetX + dragTransformX;
        offsetY = dragStartOffsetY + dragTransformY;
        repaint();
    }

    private void handleMousePressed(MouseEvent evt) {
        dragStartX = evt.getX();
        dragStartY = evt.getY();
        dragStartOffsetX = offsetX;
        dragStartOffsetY = offsetY;
    }

    private void handleScroll(ScrollEvent evt) {
        double zoomDelta = evt.getDeltaY() / evt.getMultiplierY();
        zoomDelta /= 10;

        zoomLevel += zoomDelta;

        if (zoomLevel < 0.2d)
            zoomLevel = 0.2d;
        if (zoomLevel > 4.0d)
            zoomLevel = 4.0d;
        repaint();
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
}
