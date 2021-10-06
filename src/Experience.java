import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class Experience implements Comparable<Experience> {
    LocalDate start;
    LocalDate finish;
    String position;
    String company;
    private String department;

    Experience(JSONObject experience) {
        this();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.start = LocalDate.parse(experience.getString("start_date"),dtf);
        if (!experience.isNull("end_date")){
            this.finish = LocalDate.parse(experience.getString("end_date"),dtf);
        }
        this.position = experience.getString("position");
        this.company = experience.getString("company");
        if (!experience.isNull("department"))
            this.department = experience.getString("department");
    }

    String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    Experience(String start, String company, String department, String position){
        this.company = company;
        if (department != null) this.department = department;
        this.position = position;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.start = LocalDate.parse(start, dtf);
    }

    Experience(String start, String finish, String company, String department, String position){
        this(start,company,department,position);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.finish = LocalDate.parse(finish, dtf);
    }

    Experience(LocalDate start, LocalDate finish, String position, String company, String department) {
        this.department = department;
        this.start = start;
        this.finish = finish;
        if (finish != null && start.isAfter(finish)) throw new InvalidDatesException();
        this.position = position;
        this.company = company;
    }

//    public Experience(LocalDate start, LocalDate finish, String position, String company, String department) {
//        this(start,finish,position,company);
//        this.department = department;
//    }

    private Experience(){}

    @Override
    public int compareTo(Experience experience) {
        if ((finish == null || experience.finish == null) || (finish.isEqual(experience.finish))){
            int rez = company.compareTo(experience.company);
            return rez==0? 1 : rez;
        } else {
            return -finish.compareTo(experience.finish);
        }

    }

    @Override
    public String toString() {
        return "\n\t"+company+" from "+start+" to "+finish+" on position "+position;
    }

    public void closeExperience(String companyName, String department){

    }

    public JSONObject toJSON() {
        JSONObject experience = new JSONObject();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        experience.put("company",company);
        experience.put("position",position);
        experience.put("start_date",start.format(dtf));
        if (finish != null){
            experience.put("end_date",finish.format(dtf));
        } else {
            experience.put("end_date",JSONObject.NULL);
        }
        experience.put("position",position);
        experience.put("department",department);

        return experience;
    }
}

class InvalidDatesException extends NumberFormatException{

}
