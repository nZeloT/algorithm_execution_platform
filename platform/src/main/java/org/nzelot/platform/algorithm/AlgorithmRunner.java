package org.nzelot.platform.algorithm;

import org.nzelot.platform.eventbus.EventBroker;
import org.nzelot.platform.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AlgorithmRunner implements Runnable {

    private EventBroker broker;

    private Constructor con;
    private Method start;
    private Object[] parameters;
    private Object instance;
    private Class<?> targetClass;

    private Map<String, Object> storedResults;
    private Map<String, Object> results;

    public AlgorithmRunner(EventBroker broker, Constructor con, Method start, Object... parameters) {
        this.broker = broker;
        this.parameters = parameters;
        this.con = con;
        this.start = start;
        this.targetClass = con.getDeclaringClass();
    }

    @Override
    public void run() {
        try {
            broker.distributeEvent(AlgorithmRunnerEvent.START(getAlgorithmName()));

            instance = con.newInstance(parameters);
            start.invoke(instance);

            collectResults();
            broker.distributeEvent(AlgorithmRunnerEvent.FINISH(getAlgorithmName(), storedResults, results));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * collect all possible results of a generator or algorithm for further use in other algorithms
     */
    private void collectResults() {
        var methods = ReflectionUtil.getMethodsWithAnnotation(instance.getClass(), Result.class);
        results = new HashMap<>();
        storedResults = new HashMap<>();
        for (Method method : methods) {
            try {
                var res  = method.invoke(instance);
                var ann = method.getAnnotation(Result.class);
                var name = ann.name();
                if(ann.type() == ResultType.STORED)
                    storedResults.put(name, res);
                else
                    results.put(name, res);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public String getAlgorithmName() {
        var ret = "";
        if(targetClass.isAnnotationPresent(Generator.class))
            ret = targetClass.getAnnotation(Generator.class).name();
        if(targetClass.isAnnotationPresent(Algorithm.class))
            ret = targetClass.getAnnotation(Algorithm.class).name();
        return ret;
    }

}
