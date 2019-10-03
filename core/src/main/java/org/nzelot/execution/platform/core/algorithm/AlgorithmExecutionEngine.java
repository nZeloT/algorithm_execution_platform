package org.nzelot.execution.platform.core.algorithm;

import org.nzelot.execution.platform.core.eventbus.EventBroker;
import org.nzelot.execution.platform.core.eventbus.EventSink;
import org.nzelot.execution.platform.core.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class AlgorithmExecutionEngine {

    private EventBroker broker;

    private Thread runnerThread;
    private Map<String, Object> algorithmResults;

    public AlgorithmExecutionEngine(EventBroker broker) {
        this.broker = broker;
        this.algorithmResults = new HashMap<>();
        broker.registerSink(this);
    }

    @EventSink(AlgorithmRunnerEvent.class)
    public void receiveAlgorithmRunnerEvent(AlgorithmRunnerEvent event) {
        if (!event.isStarted())
            this.algorithmResults.putAll(event.getStoredResults());
    }

    @EventSink(AlgorithmExecutionEngineControlEvent.class)
    public void receiveControlCommand(AlgorithmExecutionEngineControlEvent event) {
        switch (event.getCommand()) {
            case CLEAR:
                clearStorage();
                break;
            case CANCLE:
                cancelRunningAlgorithm();
                break;
            case START:
                startAlgorithm(event.getAlgorithmClass(), event.getParameters());
                break;
        }
    }

    private void clearStorage() {
        algorithmResults.clear();
    }

    private void cancelRunningAlgorithm() {
        runnerThread.interrupt();
    }

    private void startAlgorithm(Class<?> algorithmClass, Object[] initialisation) {
        try {
            //first clear the flight recorder and start a new recording
            var con = ReflectionUtil.getConstructorWithAnnotation(algorithmClass, Initializer.class);
            var start = ReflectionUtil.getMethodWithAnnotation(algorithmClass, Starter.class);
            if (con == null || start == null) {
                throw new IllegalArgumentException("Passed a class without annotated constructor or method!");
            }

            if (algorithmClass.isAnnotationPresent(Algorithm.class)) {
                //special handling for algorithms to combine manual parameters with stored ones
                //Strategy: Iterate through all parameters and check whether a stored result or a initialisation value is requested
                if (con.getParameterCount() != initialisation.length) {
                    initialisation = readParametersFromStorage(initialisation, con);
                }
            }
            var runner = new AlgorithmRunner(broker, con, start, initialisation);
            runnerThread = new Thread(runner);
            runnerThread.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private Object[] readParametersFromStorage(Object[] initialisation, Constructor<?> con) {
        var paramValues = new Object[con.getParameterCount()];
        var initialisationCursor = 0;
        var params = con.getParameters();
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            var annotations = param.getDeclaredAnnotations();
            for (var ann : annotations) {
                if (ann.annotationType().equals(StoredInitialisationParameter.class)) {
                    //fill parameter from the storage
                    var stored = (StoredInitialisationParameter) ann;
                    var requestedValue = this.algorithmResults.get(stored.value());
                    if (!requestedValue.getClass().equals(param.getType()))
                        throw new IllegalArgumentException("Stored parameter value and requested parameter value do not match!");
                    paramValues[i] = requestedValue;
                    break;
                }
            }

            if (paramValues[i] == null)
                paramValues[i] = initialisation[initialisationCursor++];
        }
        return paramValues;
    }
}
