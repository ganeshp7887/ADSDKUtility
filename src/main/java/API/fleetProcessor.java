package API;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class fleetProcessor {

    private  final static List<String> TRACK2_MATCH = Arrays.asList(";", "=", "D", "F");
    private  final static List<String> TRACK1_MATCH = Arrays.asList("%", "B", "^");

    public static  String trackDataReader(String trackData, String cardDataSource, String cardType) {
        String promptValue = "";
        if ("2".equals(cardDataSource) || "6".equals(cardDataSource)) {
            if (containsAny(trackData, TRACK2_MATCH)) {
                if (trackData.contains("?") || trackData.contains("=") || trackData.contains("D") || trackData.contains("F")) {
                    trackData = trackData.replaceFirst(";", "");
                    trackData = trackData.replaceAll("\\?", "");
                    trackData = trackData.replaceAll("F", "");
                    if (trackData.contains("=")) {
                        String[] parts = trackData.split("=");
                        trackData = parts[parts.length - 1];
                    }
                    if (trackData.contains("D")) {
                        String[] parts = trackData.split("D");
                        trackData = parts[parts.length - 1];
                    }
                    String cardData = trackData;
                    if ("VIF".equalsIgnoreCase(cardType) || "MCF".equalsIgnoreCase(cardType)) {
                        promptValue = cardData.substring(cardData.length() - 1);
                    }
                    if ("VGF".equalsIgnoreCase(cardType)) {
                        promptValue = cardData.substring(4, 5);
                    }
                    if ("WXF".equalsIgnoreCase(cardType)) {
                        String key = cardData.substring(4, 5);
                        String value = cardData.substring(cardData.length() - 1);
                        promptValue = key + value;
                    }
                }
                return promptValue;
            }
        }
        if ("1".equals(cardDataSource)) {
            if (containsAny(trackData, TRACK1_MATCH)) {
                if (trackData.contains("%") || trackData.contains("^")) {
                    trackData = trackData.replaceFirst("%", "");
                    trackData = trackData.replaceAll("\\?", "");
                    String[] parts = trackData.split("\\^");
                    String cardData = parts[parts.length - 1];
                    if ("VIF".equalsIgnoreCase(cardType) || "MCF".equalsIgnoreCase(cardType)) {
                        promptValue = cardData.substring(cardData.length() - 1);
                    }
                    if ("VGF".equalsIgnoreCase(cardType)) {
                        promptValue = cardData.substring(4, 5);
                    }
                    if ("WXF".equalsIgnoreCase(cardType)) {
                        String key = cardData.substring(4, 5);
                        String value = cardData.substring(cardData.length() - 1);
                        promptValue = key + value;
                    }
                }
                return promptValue;
            }
        }
        if ("4".equals(cardDataSource)) {
            if (containsAny(trackData, TRACK1_MATCH)) {
                if (trackData.contains("%") || trackData.contains("^")) {
                    trackData = trackData.replaceFirst("%", "");
                    trackData = trackData.replaceAll("\\?", "");
                    String[] parts = trackData.split("\\^");
                    String cardData = parts[parts.length - 1];
                    if ("VIF".equalsIgnoreCase(cardType) || "MCF".equalsIgnoreCase(cardType)) {
                        promptValue = cardData.substring(cardData.length() - 1);
                    }
                    if ("VGF".equalsIgnoreCase(cardType)) {
                        promptValue = cardData.substring(4, 5);
                    }
                    if ("WXF".equalsIgnoreCase(cardType)) {
                        String key = cardData.substring(4, 5);
                        String value = cardData.substring(cardData.length() - 1);
                        promptValue = key + value;
                    }
                }
                return promptValue;
            }
        }
        return promptValue;
    }

    public static  String trackDataPromptFinder(String trackData, String cardDataSource, String cardType) {
        String data = trackDataReader(trackData, cardDataSource, cardType);
        switch (cardType.toUpperCase()) {
            case "VIF":
                return FleetPromptValues.vifPromptValues(data);
            case "MCF":
                return FleetPromptValues.mcfPromptValues(data);
            case "VGF":
                return FleetPromptValues.vgfPromptValues(data);
            case "WXF":
                return FleetPromptValues.wexPromptValues(data);
            case "FOF":
                return FleetPromptValues.fofPromptValues();
            case "FWF":
                return FleetPromptValues.fwfPromptValues();
            case "FMF":
                return FleetPromptValues.fmfPromptValues();
            default:
                return "";
        }
    }

    public static  String cardNumberFinder(String trackData, String cds) {
        List<String> track2Match = Arrays.asList(";", "=", "D", "F");
        List<String> track1Match = Arrays.asList("%", "B", "^");
        switch (cds) {
            case "2":
                if (containsAny(trackData, track2Match)) {
                    trackData = trackData.replaceFirst(";", "");
                    trackData = trackData.replaceAll("\\?", "");
                    if (trackData.contains("=")) {
                        trackData = trackData.substring(0, trackData.indexOf("="));
                    }
                    return trackData;
                }
                break;
            case "1":
                if (containsAny(trackData, track1Match)) {
                    trackData = trackData.replaceFirst("%B", "");
                    trackData = trackData.replaceAll("\\?", "");
                    if (trackData.contains("^")) {
                        trackData = trackData.substring(0, trackData.indexOf("^"));
                    }
                    return trackData;
                }
                break;
            case "4":
                if (containsAny(trackData, track1Match)) {
                    trackData = trackData.replaceFirst("%B", "");
                    if (trackData.contains("~")) {
                        trackData = trackData.substring(0, trackData.indexOf("~"));
                    }
                    if (trackData.contains("^")) {
                        trackData = trackData.substring(0, trackData.indexOf("^"));
                    }
                    return trackData;
                }
                break;
            case "6":
                if (containsAny(trackData, track2Match)) {
                    if (trackData.contains("D")) {
                        trackData = trackData.substring(0, trackData.indexOf("D"));
                    }
                    return trackData;
                }
                break;
        }
        return "";
    }

    private static  boolean containsAny(String str, List<String> targets) {
        for (String target : targets) {
            if (str.contains(target)) {
                return true;
            }
        }
        return false;
    }


static class FleetPromptValues {

    public static String vgfPromptValues(String prompt) {
        Map<String, String> promptValue = new HashMap<>();
        promptValue.put("0", "No prompts");
        promptValue.put("1", "DriverIDNumberFlag");
        promptValue.put("2", "OdometerFlag");
        promptValue.put("3", "DriverIDNumberFlag, OdometerFlag");

        return promptValue.containsKey(prompt) ? promptValue.get(prompt) : "Prompts not found in list";
    }

    public static String vifPromptValues(String prompt) {
        Map<String, String> promptValue = new HashMap<>();
        promptValue.put("0", "No prompts");
        promptValue.put("1", "EmployeeIDNumberFlag, OdometerFlag");
        promptValue.put("2", "VehicleIDNumberFlag, OdometerFlag");
        promptValue.put("3", "DriverIDNumberFlag, OdometerFlag");
        promptValue.put("4", "OdometerFlag");
        promptValue.put("5", "No prompts");
        promptValue.put("6", "DriverIDNumberFlag, VehicleIDNumberFlag");

        return promptValue.containsKey(prompt) ? promptValue.get(prompt) : "Prompts not found in list";
    }

    public static  String mcfPromptValues(String prompt) {
        Map<String, String> promptValue = new HashMap<>();
        promptValue.put("0", "No prompts");
        promptValue.put("1", "EmployeeIDNumberFlag, OdometerFlag");
        promptValue.put("2", "VehicleIDNumberFlag, OdometerFlag");
        promptValue.put("3", "DriverIDNumberFlag, OdometerFlag");
        promptValue.put("4", "OdometerFlag");
        promptValue.put("5", "No prompts");

        return promptValue.containsKey(prompt) ? promptValue.get(prompt) : "Prompts not found in list";
    }

    public static  String wexPromptValues(String prompt) {
        Map<String, String> promptValue = new HashMap<>();
        promptValue.put("00", "No Prompts");
        promptValue.put("10", "OdometerFlag, DriverIDNumberFlag");
        promptValue.put("11", "OdometerFlag, VehicleIDNumberFlag");
        promptValue.put("12", "OdometerFlag");
        promptValue.put("13", "DriverIDNumberFlag, VehicleIDNumberFlag");
        promptValue.put("14", "DriverIDNumberFlag");
        promptValue.put("15", "VehicleIDNumberFlag");
        promptValue.put("16", "DriverIDNumberFlag, JobNumberFlag");
        promptValue.put("17", "VehicleIDNumberFlag, JobNumberFlag");
        promptValue.put("18", "OdometerFlag, VehicleIDNumberFlag, DriverIDNumberFlag");
        promptValue.put("19", "OdometerFlag, DriverIDNumberFlag, JobNumberFlag");
        promptValue.put("20", "OdometerFlag, VehicleIDNumberFlag, JobNumberFlag");
        promptValue.put("21", "OdometerFlag, UserIDFlag, JobNumberFlag");
        promptValue.put("22", "OdometerFlag, DriverIDNumberFlag, CustomerDataFlag");
        promptValue.put("23", "OdometerFlag, VehicleIDNumberFlag, CustomerDataFlag");
        promptValue.put("24", "CustomerDataFlag, DriverIDNumberFlag, JobNumberFlag");
        promptValue.put("25", "CustomerDataFlag, VehicleIDNumberFlag, JobNumberFlag");
        promptValue.put("26", "UserIDFlag");
        promptValue.put("27", "OdometerFlag, UserIDFlag");
        promptValue.put("28", "OdometerFlag, DriverIDNumberFlag, UserIDFlag");
        promptValue.put("29", "OdometerFlag, VehicleIDNumberFlag, UserIDFlag");
        promptValue.put("30", "OdometerFlag, UserIDFlag, CustomerDataFlag");
        promptValue.put("31", "OdometerFlag, CustomerDataFlag, UserIDFlag");
        promptValue.put("32", "UserIDFlag, JobNumberFlag");
        promptValue.put("33", "VehicleIDNumberFlag, UserIDFlag");
        promptValue.put("34", "DriverIDNumberFlag, UserIDFlag");
        promptValue.put("35", "DriverIDNumberFlag, DeptNumberFlag");
        promptValue.put("36", "UserIDFlag, DeptNumberFlag");
        promptValue.put("37", "VehicleIDNumberFlag, DeptNumberFlag");
        promptValue.put("38", "OdometerFlag, DriverIDNumberFlag, DeptNumberFlag");
        promptValue.put("39", "OdometerFlag, UserIDFlag, DeptNumberFlag");
        promptValue.put("40", "OdometerFlag, VehicleIDNumberFlag, DeptNumberFlag");
        promptValue.put("41", "DeptNumberFlag");
        promptValue.put("42", "CustomerDataFlag, UserIDFlag, DeptNumberFlag");
        promptValue.put("43", "CustomerDataFlag, VehicleIDNumberFlag, DeptNumberFlag");
        promptValue.put("44", "CustomerDataFlag, DriverIDNumberFlag, DeptNumberFlag");
        promptValue.put("45", "CustomerDataFlag, DriverIDNumberFlag, UserIDFlag");
        promptValue.put("46", "CustomerDataFlag, UserIDFlag, LicenseNumberFlag");
        promptValue.put("47", "CustomerDataFlag, VehicleIDNumberFlag, LicenseNumberFlag");
        promptValue.put("48", "CustomerDataFlag");
        promptValue.put("49", "DriverIDNumberFlag, CustomerDataFlag");
        promptValue.put("50", "UserIDFlag, CustomerDataFlag");
        promptValue.put("51", "VehicleIDNumberFlag, CustomerDataFlag");
        promptValue.put("52", "Reserved for future use");

        return promptValue.containsKey(prompt) ? promptValue.get(prompt) : "Prompts not found in list";
    }

    public static  String fofPromptValues() {
        Map<String, String> promptValue = new HashMap<>();
        promptValue.put("0", "OdometerFlag, VehicleNumberFlag");

        return promptValue.get("0");
    }

    public static  String fwfPromptValues() {
        Map<String, String> promptValue = new HashMap<>();
        promptValue.put("0", "OdometerFlag, DriverIDNumberFlag");

        return promptValue.get("0");
    }

    public static  String fmfPromptValues() {
        Map<String, String> promptValue = new HashMap<>();
        promptValue.put("0", "OdometerFlag, DriverIDNumberFlag");

        return promptValue.get("0");
    }
}

class FleetServiceIndicator {

    public  String vgfServiceIndicator(String service) {
        Map<String, String> mastercard = new HashMap<>();
        mastercard.put("0", "Fuel and other products");
        mastercard.put("1", "Fuel");

        return mastercard.get(service);
    }

    public  String vifServiceIndicator(String service) {
        Map<String, String> visa = new HashMap<>();
        visa.put("0", "Fleet No Restriction(fuel, maintenance, and nonfuel purchases)");
        visa.put("1", "Fleet(fuel and maintenance purchases)");
        visa.put("2", "Fleet / Fuel (fuel purchases)");
        visa.put("3", "Reserved");
        visa.put("4", "Reserved");
        visa.put("5", "Reserved");
        visa.put("6", "Reserved");
        visa.put("7", "Reserved");
        visa.put("8", "Reserved");
        visa.put("9", "Reserved");

        return visa.get(service);
    }

    public  String mcfServiceIndicator(String service) {
        Map<String, String> mastercard = new HashMap<>();
        mastercard.put("1", "Fuel and other products");
        mastercard.put("2", "Fuel");

        return mastercard.get(service);
    }

    public  String wexServiceIndicator(String service) {
        Map<String, String> wex = new HashMap<>();
        wex.put("00", "Fuel");
        wex.put("01", "Unrestricted");

        return wex.get(service);
    }
}

public static class FleetDataAppender {

    public static  Map<String, String> promptFinderByValue(String prompts, String cardType) {
        Map<String, String> fleetPromptsData = new HashMap<>();
        String promptText = "";

        if (prompts != null) {
            for (String prompt : prompts.split(", ")) {
                if ("NO PROMPTS".equalsIgnoreCase(prompt) || "PROMPTS NOT FOUND IN LIST".equalsIgnoreCase(prompt)) {
                    fleetPromptsData.put("FleetPromptCount", "0");
                    return fleetPromptsData;
                }
                String key = prompt.substring(0, prompt.length() - 4);
                switch (key.toUpperCase()) {
                    case "ODOMETER":
                        promptText = "000000";
                        break;
                    case "DRIVERIDNUMBER":
                        promptText = "123450";
                        break;
                    case "VEHICLEIDNUMBER":
                    	promptText = "123450";
                        break;
                    case "EMPLOYEEIDNUMBER":
                        promptText = "123456";
                        break;
                    case "DEPTNUMBER":
                        promptText = "789000";
                        break;
                    case "LICENSENUMBER":
                        promptText = "100000";
                        break;
                    case "CUSTOMERDATA":
                        promptText = "111111";
                        break;
                    case "USERID":
                        promptText = "012345";
                        break;
                    case "JOBNUMBER":
                        promptText = "456456";
                        break;
                    default:
                        promptText = "";
                        break;
                }
                if (!promptText.isEmpty()) {
                    fleetPromptsData.put(key, promptText);
                }
            }
            fleetPromptsData.put("FleetPromptCount", String.valueOf(fleetPromptsData.size()));
        } else {
            fleetPromptsData.put("FleetPromptCount", "0");
        }
        return fleetPromptsData;
    }

}

}

