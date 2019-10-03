package org.nzelot.execution.platform.core.eventbus;

import org.nzelot.execution.platform.core.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBroker {

    private Map<Class<?>, List<Pair<Method, Object>>> instanceEventSinks = new HashMap<>();

    public void registerSink(Object sink) {
        var methods = sink.getClass().getMethods();
        for (Method method : methods) {
            if(method.isAnnotationPresent(EventSink.class)){ //check whether the method is annotated as sink
                var ann = method.getAnnotation(EventSink.class);
                var eventClass = ann.value();
                if(method.getParameterCount() == 1 //check that there is only one parameter
                && method.getParameterTypes()[0].equals(eventClass)){ //and the parameter type needs to match the annotation specified type
//                    System.out.println("Event broker: Added " + sink + " as Sink for " + eventClass.getCanonicalName());

                    if (instanceEventSinks.containsKey(eventClass)) { //do we know the event type?
                        instanceEventSinks.get(eventClass).add(new Pair<>(method, sink));
                    }else {
                        instanceEventSinks.put(eventClass, new ArrayList<>());
                        instanceEventSinks.get(eventClass).add(new Pair<>(method, sink));
                    }
                }
            }
        }

    }

    public <T> void distributeEvent(T event) {
//        System.out.println("Event Broker: Distributing " + event);
        for (Class<?> aClass : instanceEventSinks.keySet()) {
            if (aClass.isInstance(event)) {
                for (Pair eventSink : instanceEventSinks.get(aClass)) {
                    try {
                        ((Method)eventSink.getKey()).invoke(eventSink.getValue(), event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
