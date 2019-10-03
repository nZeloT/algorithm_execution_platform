package org.nzelot.execution.platform.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.nzelot.execution.platform.core.algorithm.Algorithm;
import org.nzelot.execution.platform.core.algorithm.Generator;
import org.nzelot.execution.platform.gui.controls.algorithm.AlgorithmToolbarView;
import org.nzelot.execution.platform.gui.controls.algorithm.AlgorithmToolbarViewController;
import org.nzelot.execution.platform.gui.controls.flightrecorder.FlightRecorderToolbarView;
import org.nzelot.execution.platform.gui.controls.flightrecorder.FlightRecorderToolbarViewController;
import org.nzelot.execution.platform.gui.rendering.RenderCanvas;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public abstract class AbstractPlatformWindowView extends BorderPane {

    protected AbstractPlatformWindowController controller;

    private BooleanProperty menuBarActive;
    private MenuBar menuBar;

    private BooleanProperty toolBarActive;
    private ToolBar toolBar;


    private BooleanProperty playbackRunning;

    private BooleanProperty algorithmRunning;

    private Node mainContent;

    public AbstractPlatformWindowView(AbstractPlatformWindowController controller) {
        this.controller = controller;
        this.mainContent = new RenderCanvas();
        this.menuBar = new MenuBar();
        this.toolBar = new ToolBar();
        this.menuBarActive = new SimpleBooleanProperty(true);
        this.toolBarActive = new SimpleBooleanProperty(true);
        this.algorithmRunning = new SimpleBooleanProperty(false);
        this.playbackRunning = new SimpleBooleanProperty(false);
    }

    protected void initialize(int width, int height) {
        var menuTooBarBox = new VBox(menuBar, toolBar);
        menuBar.visibleProperty().bind(menuBarActive);
        toolBar.visibleProperty().bind(toolBarActive);

        setCenter(initializeRenderer());
        setTop(menuTooBarBox);
        setPrefSize(width, height);

        buildMenus(this.menuBar);
        buildToolBar(this.toolBar);

        this.controller.initializeController(this);
    }

    protected void buildMenus(MenuBar menuBar){
        Menu newMenu = new Menu("Instanz");
        var itm = new MenuItem("Löschen");
        itm.setOnAction(evt -> this.controller.clearInstance());
        newMenu.getItems().add(itm);

        // find all generators using Reflection lib :D; needs opening the algorithm and generator package to the platform module
        addClassesToMenu(newMenu, Generator.class);

        Menu algoMenu = new Menu("Algorithmus");
        itm = new MenuItem("Zurücksetzen");
        itm.setOnAction(evt -> this.controller.resetInstance());
        algoMenu.getItems().add(itm);

        addClassesToMenu(algoMenu, Algorithm.class);

        newMenu.disableProperty().bind(algorithmRunning);
        algoMenu.disableProperty().bind(algorithmRunning);

        menuBar.getMenus().addAll(newMenu, algoMenu);
    }

    private void addClassesToMenu(Menu menu, Class<? extends Annotation> targetAnnotation){
        //noinspection SpellCheckingInspection
        Reflections ref = new Reflections("org.nzelot");
        System.out.println("Scanning: " + Arrays.toString(ref.getConfiguration().getUrls().toArray()));
        var foundClasses = ref.getTypesAnnotatedWith(targetAnnotation);

        var menutItems = new ArrayList<MenuItem>();
        for (Class<?> foundClass : foundClasses) {
            String title;
            try {
                var annotation = foundClass.getAnnotation(targetAnnotation);
                title = (String)targetAnnotation.getDeclaredMethod("name").invoke(annotation);
            } catch (IllegalAccessException | NullPointerException |
                    InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                continue;
            }
            var menuItem = new MenuItem(title);
            menuItem.setOnAction(evt -> controller.initializeAlgorithm(foundClass));
            menutItems.add(menuItem);
        }
        menutItems.sort(Comparator.comparing(MenuItem::getText));
        menu.getItems().addAll(menutItems);
    }

    protected void buildToolBar(ToolBar toolBar){
        toolBar.getItems().addAll(setUpFlightRecorderView().getItems());
        toolBar.getItems().addAll(setUpAlgorithmView().getItems());
    }

    protected abstract Node initializeRenderer();

    void setAlgorithmRunning(boolean running){
        algorithmRunning.setValue(running);
    }

    public void setPlaybackRunning(boolean playbackRunning) {
        this.playbackRunning.setValue(playbackRunning);
    }

    private ToolBar setUpFlightRecorderView() {
        var view = new FlightRecorderToolbarView();
        var visibleProp = new SimpleBooleanProperty();
        visibleProp.bind(algorithmRunning.not());
        new FlightRecorderToolbarViewController(view, visibleProp);
        return view;
    }

    private ToolBar setUpAlgorithmView() {
        var view = new AlgorithmToolbarView();
        new AlgorithmToolbarViewController(view, algorithmRunning);
        return view;
    }
}
