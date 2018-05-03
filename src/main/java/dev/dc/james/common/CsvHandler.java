package dev.dc.james.common;

import java.util.HashMap;
import java.util.Map;

public class CsvHandler {

    public static String detectDelimiter(String header) {
        Map<String, Integer> count = new HashMap<>();

        String ignoreInsideQuotes = "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

        count.put("|", header.split("\\|" + ignoreInsideQuotes).length);
        count.put("\t", header.split("\t" + ignoreInsideQuotes).length);
        count.put(";", header.split(";" + ignoreInsideQuotes).length);
        count.put(",", header.split("," + ignoreInsideQuotes).length);

        int max = 0;
        String maxDel = "";
        for (String delimiter : count.keySet()) {
            int num = count.get(delimiter);
            if (num > max) {
                max = num;
                maxDel = delimiter;
            }
        }

        if (max == 0) {
            throw new IllegalArgumentException();
        }

        return maxDel;
    }

    public static String[] extractFields(String header, String delimiter) {
        String[] headerFields = header.split(delimiter);

        for (int i = 0; i < headerFields.length; i++) {
            headerFields[i] = headerFields[i].trim();
        }

        return headerFields;
    }
}
