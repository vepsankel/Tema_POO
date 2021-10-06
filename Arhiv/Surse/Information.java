import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Information {
    private String genre;
    String name;
    private String surname;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;

    public Map<String, LanguageLevel> getLanguages() {
        return languages;
    }

    private Map<String, LanguageLevel> languages;

    public Information(String name, String surname, String email, String phone, String dateOfBirth, String genre, Map<String, LanguageLevel> languages) {
        this(name, email, phone, dateOfBirth, genre);
        this.surname = surname;
        this.languages = languages;
    }

    public Information(String name, String email, String phone, String dateOfBirth, String genre) {
        this();
        this.name = name;
        this.email = email;
        this.phone = phone;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.dateOfBirth = LocalDate.parse(dateOfBirth,dtf);
        this.genre = genre;
    }

    static Information shortInfo(JSONObject shortInfo){
        String name = shortInfo.getString("name");
        String genre = shortInfo.getString("genre");
        String email = shortInfo.getString("email");
        String phone = shortInfo.getString("phone");
        String dateOfBirth = shortInfo.getString("date_of_birth");

        return new Information(name, null, email, phone, dateOfBirth, genre, null);
    }

    Information() {
        languages = new Hashtable<>();
    }

    Information(JSONObject information){
        this();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        this.name = information.getString("name");
        this.genre = information.getString("genre");
        this.email = information.getString("email");
        this.phone = information.getString("phone");
        this.dateOfBirth = LocalDate.parse(information.getString("date_of_birth"),dtf);

        JSONArray languages = information.getJSONArray("languages");
        JSONArray languages_level = information.getJSONArray("languages_level");
        for (int i = 0 ; i < languages.length() ; i++){
            this.languages.put(languages.getString(i), LanguageLevel.valueOf(languages_level.getString(i)));
        }
    }

    String getEmail(){
        return email;
    }

    public JSONObject writeInformation() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        JSONObject info = new JSONObject();

        info.put("name",name);
        info.put("email",email);
        info.put("phone",phone);
        info.put("date_of_birth",dateOfBirth.format(dtf));
        info.put("genre",genre);

        JSONArray languages = new JSONArray();
        JSONArray languages_level = new JSONArray();

        for (Map.Entry<String, LanguageLevel> entry : this.languages.entrySet()) {
            languages.put(entry.getKey());
            languages_level.put(entry.getValue().toString());
        }

        info.put("languages",languages);
        info.put("languages_level",languages_level);

        return info;
    }

    public Object toShortJSON() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        JSONObject info = new JSONObject();

        info.put("name",name);
        info.put("email",email);
        info.put("phone",phone);
        info.put("genre",genre);
        info.put("date_of_birth",dateOfBirth.format(dtf));

        return info;
    }

    public LocalDate getBirthDate() {
        return dateOfBirth;
    }

    public String getGenre() {
        return genre;
    }

    public String getPhone() {
        return phone;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    enum LanguageLevel{
        Beginner,
        Experienced,
        Advanced
    }

    @Override
    public String toString() {
        return "\tName: "+name+"\n\tSurname: "+surname+"\n\tEmail: "+email+"\n\tPhone: "+phone+
                "\n\tDate of birth: "+dateOfBirth+ "\n\tGenre: "+genre+"\n\tLanguages: "+languages;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Information)) return false;

        Information info = (Information) obj;
        try {
            if ( !(name == null && info.name == null) && ! info.name.equals(name))
                return false;
            if ( !(surname == null && info.surname == null) && ! info.surname.equals(surname))
                return false;
            if ( !(dateOfBirth == null && info.dateOfBirth == null) && ! info.dateOfBirth.equals(dateOfBirth))
                return false;
            if ( !(email == null && (info.email == null)) && ! info.email.equals(email))
                return false;
            if ( !(phone == null && info.phone == null) && ! info.phone.equals(phone))
                return false;
            if ( !(genre == null && info.genre == null) && ! info.genre.equals(genre))
                return false;
        } catch (NullPointerException e){
            return false;
        }
        return true;
    }
}
