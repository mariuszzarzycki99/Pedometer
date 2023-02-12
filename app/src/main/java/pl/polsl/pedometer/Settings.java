package pl.polsl.pedometer;

public class Settings {
    private static String gender = "Female";
    private static Double height = 180.0;
    private static Double weight = 70.0;

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
}