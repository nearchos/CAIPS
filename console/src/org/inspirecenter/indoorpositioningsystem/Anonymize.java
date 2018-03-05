package org.inspirecenter.indoorpositioningsystem;

import com.google.gson.Gson;
import org.inspirecenter.indoorpositioningsystem.model.Dataset;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.model.RadioScanEntry;

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

        // read dataset from file
        final Dataset dataset = new Gson().fromJson(new String(Files.readAllBytes(inputFile.toPath())), Dataset.class);
        final List<MeasurementEntry> measurementEntries = dataset.getMeasurements();

        final Map<String,Character> floorUuidToCode = new HashMap<>();
        char floorCode = 'a';
        final Map<String,Integer> macAddressToCode = new HashMap<>();
        int macAddressCode = 0;

        System.out.println("{\n  \"measurementEntries\": [");

        int counter = 0;
        for(final MeasurementEntry measurementEntry : measurementEntries) {
            if(!floorUuidToCode.containsKey(measurementEntry.getFloorUUID())) floorUuidToCode.put(measurementEntry.getFloorUUID(), floorCode);
            final List<RadioScanEntry> radioScanEntries = measurementEntry.getRadioScans();
            int measurementsCount = 0;

            System.out.println("    {");
            System.out.println("      \"uuid\": \"" + measurementEntry.getUuid() + "\",");
            System.out.println("      \"floorUUID\": \"" + floorUuidToCode.get(measurementEntry.getFloorUUID()) + "\",");
            System.out.println("      \"createdBy\": " + measurementEntry.getCreatedBy().hashCode() + ",");
            System.out.println("      \"timestamp\": " + measurementEntry.getTimestamp() + ",");
            System.out.println("      \"lat\": " + measurementEntry.getLat() + ",");
            System.out.println("      \"lng\": " + measurementEntry.getLng() + ",");
            System.out.println("      \"context\": " + getContextAsJSON(measurementEntry) + ",");
            System.out.println("      \"radioScanEntries\": [");
            for(final RadioScanEntry radioScanEntry : radioScanEntries) {
                if(!macAddressToCode.containsKey(radioScanEntry.getMacAddress())) macAddressToCode.put(radioScanEntry.getMacAddress(), macAddressCode++);
                System.out.println("        {\"macAddress\": \"" + macAddressToCode.get(radioScanEntry.getMacAddress()) + "\", \"ssid\": " + radioScanEntry.getLevelAsDecibel() + " }" + (measurementsCount++ < radioScanEntries.size() - 1 ? "," : ""));
            }
            System.out.println("      ]");
            System.out.println("    }" + (counter++ < measurementEntries.size() - 1 ? "," : ""));
        }

        System.out.println("  ]\n}");
    }

    private static String getContextAsJSON(final MeasurementEntry measurementEntry) {
        String context = "{";

        final Map<String,Object> contextMap = measurementEntry.getContext();

        int count = 0;
        for(final String key : contextMap.keySet()) {
            context += " \"" + key + "\": \"" + contextMap.get(key) + "\"" + (count++ < contextMap.size() - 1 ? "," : " }");
        }

        return context;
    }
}