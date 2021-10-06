public class IT extends Department {
    IT(Company company){
        super(company);
        this.name = "IT";
    }

    @Override
    public double getTotalSalaryBudget() {
        double totalBudget = 0;
        for (Employee employee: employees) {
            totalBudget += employee.salary;
        }

        return totalBudget;
    }
}
