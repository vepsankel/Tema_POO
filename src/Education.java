import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Education implements Comparable<Education> {
    LocalDate start;
    LocalDate finish;
    String institutionName;
    String educationLevel;
    double grade;

    public Education(LocalDate start, LocalDate finish, String institutionName, String educationLevel, double grade) throws InvalidDatesException {
        this.start = start;
        this.finish = finish;
        if (finish != null) if (start.isAfter(finish)) throw new InvalidDatesException();
        this.institutionName = institutionName;
        this.educationLevel = educationLevel;
        this.grade = grade;
    }

    private Education(){}

    Education(JSONObject education) {
        this();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.start = LocalDate.parse(education.getString("start_date"),dtf);
        if (!education.isNull("end_date"))
            this.finish = LocalDate.parse(education.getString("end_date"),dtf);
        this.institutionName = education.getString("name");
        this.educationLevel = education.getString("level");
        this.grade = education.getDouble("grade");
    }

    @Override
    public int compareTo(Education education) {
        {
            int ret = 0;
            int crescator;

            LocalDate cmpDate1;
            LocalDate cmpDate2;

            if (education.finish == null || finish == null){
                cmpDate1 = start;
                cmpDate2 = education.start;
                crescator = 1;
            } else {
                cmpDate1 = finish;
                cmpDate2 = education.finish;
                crescator = -1;
            }

            if (education.finish != null) ret = crescator*cmpDate1.compareTo(cmpDate2);

            if (ret != 0) return ret;
        }
        {
            return (int) Math.signum(grade - education.grade);
        }


    }

    public JSONObject toJSON() {
        JSONObject education = new JSONObject();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        education.put("level",educationLevel);
        education.put("name",institutionName);
        education.put("start_date",start.format(dtf));
        if (finish != null) {
            education.put("end_date",finish.format(dtf));
        } else {
            education.put("end_date",JSONObject.NULL);
        }
        education.put("grade",grade);


        return education;
    }

    enum EducationLevel{
        School,
        Bachelor,
        Master,
        Doctor
    }

    @Override
    public String toString() {
        return "\n\tInstitute "+institutionName+" from "+start+" to "+finish+" , "+educationLevel+" with grade "+grade;
    }

    private class InvalidDatesException extends NumberFormatException{

    }
}


