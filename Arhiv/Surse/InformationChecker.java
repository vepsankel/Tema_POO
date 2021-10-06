import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InformationChecker {
    static boolean isGoodDate(String dateString){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try{
            LocalDate date = LocalDate.parse(dateString, dtf);
            if (date.isBefore(LocalDate.of(1900,1,1)) || date.isAfter(LocalDate.now())){
                return false;
            }
        } catch (DateTimeParseException e){
            return false;
        }

        return true;
    }
}
