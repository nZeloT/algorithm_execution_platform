package org.nzelot.execution.platform.gui.initialisation;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.nzelot.execution.platform.core.algorithm.Algorithm;
import org.nzelot.execution.platform.core.algorithm.Generator;
import org.nzelot.execution.platform.core.algorithm.Initializer;
import org.nzelot.execution.platform.core.util.Pair;
import org.nzelot.execution.platform.core.util.ReflectionUtil;
import org.nzelot.execution.platform.core.util.Triple;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class InitializationDialog extends Dialog<Pair<Boolean, Object[]>> {

    private List<Callable> valueProvider = new ArrayList<>();
    private CheckBox recordEvents;

    public InitializationDialog(Class<?> targetClass) {
        String dialogTitle = null;
        if(targetClass.isAnnotationPresent(Generator.class))
            dialogTitle = targetClass.getAnnotation(Generator.class).name();
        if(targetClass.isAnnotationPresent(Algorithm.class))
            dialogTitle = targetClass.getAnnotation(Algorithm.class).name();

        if(dialogTitle == null)
            throw new IllegalArgumentException(targetClass.getCanonicalName() + " is not annotated with "
                    + Generator.class.getCanonicalName() + " or " + Algorithm.class.getCanonicalName());


        String dialogHeader = "Set the algorithm parameters ...";
        ButtonType dialogStartGenerator = new ButtonType("Start Generator", ButtonBar.ButtonData.OK_DONE);

        setTitle(dialogTitle);
        setHeaderText(dialogHeader);
        getDialogPane().getButtonTypes().addAll(dialogStartGenerator, ButtonType.CANCEL);

        //1. find the right constructor
        var constructor = ReflectionUtil.getConstructorWithAnnotation(targetClass, Initializer.class);
        Parameter[] parameters = constructor != null ? constructor.getParameters() : null;

        //2. if we found annotated parameters build the dialog accordingly
        GridPane dialogGrid = new GridPane();
        dialogGrid.setVgap(10);
        dialogGrid.setHgap(10);
        dialogGrid.setPadding(new Insets(20, 150, 10, 10));

        int rowId = 0;
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Triple<String, Node, Callable> dialogControl = null;
                var annotations = parameter.getDeclaredAnnotations();
                for (var annotation : annotations) {
                    var annotationType = annotation.annotationType();
                    if(annotationType.equals(IntInitialisationParameter.class)){
                        dialogControl = handleIntInitialisationParameter((IntInitialisationParameter)annotation);
                        break;
                    }
                    if(annotationType.equals(BooleanInitialisationParameter.class)){
                        dialogControl = handleBooleanInitialisationParameter((BooleanInitialisationParameter)annotation);
                        break;
                    }
                }

                if (dialogControl != null) {
                    dialogGrid.add(new Label(dialogControl.getA()), 0, i);
                    dialogGrid.add(dialogControl.getB(), 1, i);
                    valueProvider.add(dialogControl.getC());
                    rowId++;
                }
            }
        }else
            throw new IllegalArgumentException("No constructor annotated with " + Initializer.class.getCanonicalName() + " found for class " + targetClass.getCanonicalName());

        //add a visualisation wanted checkbox
        dialogGrid.add(new Label("Record Steps"), 0, rowId);
        dialogGrid.add(recordEvents = new CheckBox(), 1, rowId);
        recordEvents.setSelected(true);
        getDialogPane().setContent(dialogGrid);

        //3. set the result converter
        setResultConverter(btn -> {
            if(btn == dialogStartGenerator){
                //now go through all registered value provider and add their value to the result
                try {
                    var result = new Object[valueProvider.size()];
                    for (int i = 0; i < valueProvider.size(); i++) {
                        result[i] = valueProvider.get(i).call();
                    }
                    return new Pair<>(recordEvents.isSelected(), result);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }else
                return null;
        });
    }

    private Triple<String, Node, Callable> handleIntInitialisationParameter(IntInitialisationParameter parameter){
        var spinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                parameter.lowerBound(),
                parameter.upperBound(),
                parameter.defaultValue(),
                parameter.stepWidth()
        ));
        return new Triple<>(parameter.name(), spinner, spinner::getValue);
    }

    private Triple<String, Node, Callable> handleBooleanInitialisationParameter(BooleanInitialisationParameter parameter) {
        var checkBox = new CheckBox();
        checkBox.setSelected(parameter.defaultValue());
        return new Triple<>(parameter.name(), checkBox, checkBox::isSelected);
    }
}
