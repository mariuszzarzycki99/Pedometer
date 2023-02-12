package pl.polsl.pedometer;

import java.time.LocalDate;

public class DateSteps {
    private LocalDate date;
    private Integer steps;
    public DateSteps(int year, int month, int day, Integer steps)
    {
        date = LocalDate.of(year,month,day);
        this.steps = steps;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return "DateSteps(date="+date+  " steps=" + steps + ")";
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }
}
