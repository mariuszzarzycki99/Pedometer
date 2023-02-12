package pl.polsl.pedometer;

public class Settings {
    private static String gender;
    private static Double height;
    private static Double weight;

    public static String getGender() {
        return gender;
    }

    public static Double getHeight() {
        return height;
    }

    public static Double getWeight() {
        return weight;
    }

    public static void setGender(String gender) {
        Settings.gender = gender;
    }

    public static void setHeight(Double height) {
        Settings.height = height;
    }

    public static void setWeight(Double weight) {
        Settings.weight = weight;
    }

    public static void loadDefault() {
        Settings.gender = "Female";
        Settings.height = 180.0;
        Settings.weight =  70.0;
    }

    public static String getAll() {
        return gender + "|" + height.toString() + "|" + weight.toString();
    }

    public static void setAll(String settings) {
        String[] split = settings.split("\\|");
        Settings.gender = split[0];
        Settings.height = Double.valueOf(split[1]);
        Settings.weight = Double.valueOf(split[2]);
    }
}