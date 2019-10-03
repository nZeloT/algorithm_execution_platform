package org.nzelot.platform.algorithm;

import java.util.HashMap;
import java.util.Map;

public class AlgorithmRunnerEvent {
    private boolean started;
    private String algoName;
    private Map<String, Object> storedResults;
    private Map<String, Object> results;

    public static AlgorithmRunnerEvent START(String algoName) {
        return new AlgorithmRunnerEvent(algoName,true, null, null);
    }

    public static AlgorithmRunnerEvent FINISH(String algoName, Map<String, Object> storedResults) {
        return FINISH(algoName, storedResults, new HashMap<>());
    }

    public static AlgorithmRunnerEvent FINISH(String algoName, Map<String, Object> storedResults, Map<String, Object> results) {
        return new AlgorithmRunnerEvent(algoName, false, storedResults, results);
    }

    private AlgorithmRunnerEvent(String algoName, boolean started, Map<String, Object> storedResults, Map<String, Object> results) {
        this.algoName = algoName;
        this.started = started;
        this.storedResults = storedResults;
        this.results = results;
    }

    public boolean isStarted() {
        return started;
    }

    public Map<String, Object> getStoredResults() {
        return storedResults;
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public String getAlgoName() {
        return algoName;
    }
}
