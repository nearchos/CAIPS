package org.inspirecenter.indoorpositioningsystem;

import com.google.gson.Gson;
import org.inspirecenter.indoorpositioningsystem.model.Data;
import org.inspirecenter.indoorpositioningsystem.model.Measurement;
import org.inspirecenter.indoorpositioningsystem.model.Training;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class Anonymize {

    private static final String INPUT_FILE_NAME  = "data.txt";
    private static final String OUTPUT_FILE_NAME = "data.txt";

    public static void main(String[] args)
        throws IOException {

        // define input file
        final File inputFile = new File(INPUT_FILE_NAME);
        if(!inputFile.exists())
        {
            System.out.println("File does not exist: " + inputFile.getAbsolutePath());
            System.exit(-1);
        }

        // read data from file
        final Data data = new Gson().fromJson(new String(Files.readAllBytes(inputFile.toPath())), Data.class);
        final List<Training> trainings = data.getTrainings();

        final Map<String,Character> floorUuidToCode = new HashMap<>();
        char floorCode = 'a';
        final Map<String,Integer> macAddressToCode = new HashMap<>();
        int macAddressCode = 0;

        System.out.println("{\n  \"trainings\": [");

        int counter = 0;
        for(final Training training : trainings) {
            if(!floorUuidToCode.containsKey(training.getFloorUUID())) floorUuidToCode.put(training.getFloorUUID(), floorCode);
            final List<Measurement> measurements = training.getMeasurements();
            int measurementsCount = 0;

            System.out.println("    {");
            System.out.println("      \"uuid\": \"" + training.getUuid() + "\",");
            System.out.println("      \"floorUUID\": \"" + floorUuidToCode.get(training.getFloorUUID()) + "\",");
            System.out.println("      \"createdBy\": " + training.getCreatedBy().hashCode() + ",");
            System.out.println("      \"timestamp\": " + training.getTimestamp() + ",");
            System.out.println("      \"lat\": " + training.getLat() + ",");
            System.out.println("      \"lng\": " + training.getLng() + ",");
            System.out.println("      \"context\": " + getContextAsJSON(training) + ",");
            System.out.println("      \"measurements\": [");
            for(final Measurement measurement : measurements) {
                if(!macAddressToCode.containsKey(measurement.getMacAddress())) macAddressToCode.put(measurement.getMacAddress(), macAddressCode++);
                System.out.println("        {\"macAddress\": \"" + macAddressToCode.get(measurement.getMacAddress()) + "\", \"ssid\": " + measurement.getLevelAsDecibel() + " }" + (measurementsCount++ < measurements.size() - 1 ? "," : ""));
            }
            System.out.println("      ]");
            System.out.println("    }" + (counter++ < trainings.size() - 1 ? "," : ""));
        }

        System.out.println("  ]\n}");
    }

    private static String getContextAsJSON(final Training training) {
        String context = "{";

        final Map<String,Object> contextMap = training.getContext();

        int count = 0;
        for(final String key : contextMap.keySet()) {
            context += " \"" + key + "\": \"" + contextMap.get(key) + "\"" + (count++ < contextMap.size() - 1 ? "," : " }");
        }

        return context;
    }
}