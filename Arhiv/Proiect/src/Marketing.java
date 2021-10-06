public class Marketing extends Department {

    Marketing(Company company){
        super(company);
        name = "Marketing";
    }

    @Override
    public double getTotalSalaryBudget() {
        double totalBudget = 0;
        for (Employee employee: employees) {
            if (employee.salary < 3000){
                totalBudget += employee.salary;
            } else if (employee.salary > 5000){
                totalBudget += employee.salary*1.16;
            } else {
                totalBudget += employee.salary*1.1;
            }
        }

        return totalBudget;
    }
}
