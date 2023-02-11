package pl.polsl.pedometer;

public class Settings {
    private static String gender = "Male";
    private static Integer height = 180;
    private static Double weight = 70.0;

    public static String getGender() {
        return gender;
    }

    public static Integer getHeight() {
        return height;
    }

    public static Double getWeight() {
        return weight;
    }

    public static void setGender(String gender) {
        Settings.gender = gender;
    }

    public static void setHeight(Integer height) {
        Settings.height = height;
    }

    public static void setWeight(Double weight) {
        Settings.weight = weight;
    }
}