package org.nzelot.execution.platform.core.algorithm;

public class AlgorithmExecutionEngineControlEvent {
    private AlgorithmExecutionEngineControlEventCommand command;

    private Class<?> algorithmClass;
    private Object[] parameters;

    public static AlgorithmExecutionEngineControlEvent CLEAR() {
        return new AlgorithmExecutionEngineControlEvent(AlgorithmExecutionEngineControlEventCommand.CLEAR);
    }

    public static AlgorithmExecutionEngineControlEvent START(Class<?> algorithmClass, Object[] parameters) {
        return new AlgorithmExecutionEngineControlEvent(AlgorithmExecutionEngineControlEventCommand.START, algorithmClass, parameters);
    }

    public static AlgorithmExecutionEngineControlEvent CANCLE() {
        return new AlgorithmExecutionEngineControlEvent(AlgorithmExecutionEngineControlEventCommand.CANCLE);
    }

    private AlgorithmExecutionEngineControlEvent(AlgorithmExecutionEngineControlEventCommand command) {
        this.command = command;
    }

    private AlgorithmExecutionEngineControlEvent(AlgorithmExecutionEngineControlEventCommand command, Class<?> algorithmClass, Object[] parameters) {
        this.command = command;
        this.algorithmClass = algorithmClass;
        this.parameters = parameters;
    }

    public AlgorithmExecutionEngineControlEventCommand getCommand() {
        return command;
    }

    public void setCommand(AlgorithmExecutionEngineControlEventCommand command) {
        this.command = command;
    }

    public Class<?> getAlgorithmClass() {
        return algorithmClass;
    }

    public void setAlgorithmClass(Class<?> algorithmClass) {
        this.algorithmClass = algorithmClass;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
