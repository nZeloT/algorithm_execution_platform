package org.nzelot.execution.platform.testbed;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public abstract class Testbed<T> {

    private final Function<Integer, T[]> newArray;
    private final Constructor<T> newInstance;
    private final Function<String, Object>[] paramConverter;

    protected void run(String[] args) {
        System.out.println("Launching testbed at: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        System.out.println("Running with " + System.getProperty("java.vendor") + " on " + System.getProperty("java.version"));
        System.out.println("Machine is a " + System.getProperty("os.name") + " version " + System.getProperty("os.version"));
        System.out.println("Currently available memory is " + ((Runtime.getRuntime().totalMemory()/1024)/1024) + " mb");
        var tuples = parseTuples(args);
        if(!checkTestbedFunctionality()) {
            System.out.println("Self check failed. Exiting.");
            return;
        }
        for (int i = 0; i < tuples.length; i++) {
            System.out.println("Currently using " + (((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024)/1024) + " of " + ((Runtime.getRuntime().totalMemory()/1024)/1024) + " mb");
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " Executing tuple " + (i+1) + " of " + tuples.length +
                    " with " + tuples[i]);
            if(executeTriple(tuples[i]))
                System.out.println("Tuple results passed all checks for all instances.");
        }
        System.out.println("Ending testbed at: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    public Testbed(Constructor<T> newInstance, Function<Integer, T[]> newArray, Function<String, Object>[] paramConverter) {
        this.newArray = newArray;
        this.newInstance = newInstance;
        this.paramConverter = paramConverter;

        if(newInstance.getParameterCount() != paramConverter.length){
            throw new IllegalArgumentException("Expected exactly as many parameter converter as constructor parameters!");
        }
    }

    protected T[] parseTuples(String[] stringTuples) {
        var tuples = newArray.apply(stringTuples.length);

        var expectedTupleVars = newInstance.getParameterCount();

        for (int i = 0; i < stringTuples.length; i++) {
            String s = stringTuples[i].trim();
            if (!s.startsWith("(") || !s.endsWith(")")) {
                throw new IllegalArgumentException(
                        "Input did not start with ( oder end with )! See -> " + s);
            }

            var sub = s.substring(1, s.length() - 1);
            var paramValueStrings = sub.split(",");

            if (paramValueStrings.length != expectedTupleVars) {
                throw new IllegalArgumentException("Expected " + expectedTupleVars + " by ',' separated entries! See -> " + s);
            }

            Object[] vars = new Object[expectedTupleVars];
            for (int j = 0; j < expectedTupleVars; j++) {
                vars[j] = this.paramConverter[j].apply(paramValueStrings[j].trim());
            }

            try {
                tuples[i] = newInstance.newInstance(vars);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            System.out.println("Found Tuple: " + tuples[i]);
        }
        return tuples;
    }

    protected abstract boolean checkTestbedFunctionality();

    protected abstract boolean executeTriple(T triple);
}
