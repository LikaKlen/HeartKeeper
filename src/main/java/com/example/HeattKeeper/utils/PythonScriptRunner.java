package com.example.HeattKeeper.utils;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class PythonScriptRunner {

    public String runScript(String scriptName, String inputJson) {
        try {
            String pythonPath = "python"; // or path to your python executable
            String scriptPath = "src/main/resources/ai/" + scriptName;

            ProcessBuilder pb = new ProcessBuilder(
                    pythonPath, scriptPath, inputJson
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException(
                        "Python script execution failed with code: " + exitCode
                );
            }

            return output.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Python script", e);
        }
    }
}