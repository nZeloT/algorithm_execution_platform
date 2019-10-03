package org.nzelot.execution.platform.testbed;

public class Triple {

  private int numInstances;
  private int numNodes;
  private int maxCapa;

  public Triple(int numInstances, int numNodes, int maxCapa) {
    this.numInstances = numInstances;
    this.numNodes = numNodes;
    this.maxCapa = maxCapa;
  }

  public int getNumInstances() {
    return numInstances;
  }

  public int getNumNodes() {
    return numNodes;
  }

  public int getMaxCapa() {
    return maxCapa;
  }

  @Override
  public String toString() {
    return "Triple{" +
        "numInstances=" + numInstances +
        ", numNodes=" + numNodes +
        ", maxCapa=" + maxCapa +
        '}';
  }
}
