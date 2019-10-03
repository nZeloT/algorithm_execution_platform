package org.nzelot.platform.util;

import java.io.*;

public class StreamProxy implements Runnable {

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final String prefix;

    public StreamProxy(InputStream inputStream, OutputStream outputStream, String prefix) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.prefix = prefix;
    }

    @Override
    public void run() {
        try {
            var brIn = new BufferedReader(new InputStreamReader(inputStream));
            var brOut = new BufferedWriter(new OutputStreamWriter(outputStream));

            var line = "";
            while((line = brIn.readLine()) != null){
                line = prefix + line;
                brOut.write(line, 0, line.length());
                brOut.newLine();
                brOut.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Exiting StreamProxy");
    }
}
