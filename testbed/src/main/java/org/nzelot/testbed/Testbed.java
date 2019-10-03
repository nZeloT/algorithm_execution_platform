package org.nzelot.testbed;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Testbed {

    protected void run(String[] args) {
        System.out.println("Launching testbed at: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        System.out.println("Running with " + System.getProperty("java.vendor") + " on " + System.getProperty("java.version"));
        System.out.println("Machine is a " + System.getProperty("os.name") + " version " + System.getProperty("os.version"));
        System.out.println("Currently available memory is " + ((Runtime.getRuntime().totalMemory()/1024)/1024) + " mb");
        var triples = parseTriples(args);
        if(!checkTestbedFunctionality()) {
            System.out.println("Self check failed. Exiting.");
            return;
        }
        for (int i = 0; i < triples.length; i++) {
            System.out.println("Currently using " + (((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024)/1024) + " of " + ((Runtime.getRuntime().totalMemory()/1024)/1024) + " mb");
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " Executing triple " + (i+1) + " of " + triples.length +
                    " with " + triples[i]);
            if(executeTriple(triples[i]))
                System.out.println("Triple results passed all checks for all instances.");
        }
        System.out.println("Ending testbed at: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    protected Triple[] parseTriples(String[] stringTriples) {
        var triples = new Triple[stringTriples.length];
        for (int i = 0; i < stringTriples.length; i++) {
            String s = stringTriples[i].trim();
            if (!s.startsWith("(") || !s.endsWith(")")) {
                throw new IllegalArgumentException(
                        "Input did not start with ( oder end with )! See -> " + s);
            }

            var sub = s.substring(1, s.length() - 1);
            var nums = sub.split(",");

            if (nums.length != 3) {
                throw new IllegalArgumentException("Expected three by ',' separated entries! See -> " + s);
            }

            var numInstances = Integer.parseInt(nums[0].trim());
            var numNodes = Integer.parseInt(nums[1].trim());
            var numMaxCapa = Integer.parseInt(nums[2].trim());

            triples[i] = new Triple(numInstances, numNodes, numMaxCapa);
            System.out.println("Found Triple: " + triples[i]);
        }
        return triples;
    }

    protected abstract boolean checkTestbedFunctionality();

    protected abstract boolean executeTriple(Triple triple);
}
