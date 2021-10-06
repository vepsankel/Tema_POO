import java.time.LocalDate;
import java.time.Period;

public class Finance extends Department {
    Finance(Company company){
        super(company);
        name = "Finance";
    }
    @Override
    public double getTotalSalaryBudget() {
        double budget = 0;

        for (Employee employee: employees) {
            Experience exp = employee.getCurrentExperience();

            if (Period.between(exp.start, LocalDate.now()).getYears() < 1){
                budget += 1.1*employee.getSalary();
            } else {
                budget += 1.16*employee.getSalary();
            }
        }

        return budget;
    }
}
