public class Management extends Department {

    Management(Company company){
        super(company);
        name = "Management";
    }

    @Override
    public double getTotalSalaryBudget() {
        double totalBudget = 0;
        for (Employee employee: employees) {
            totalBudget += employee.salary*1.16;
        }

        return totalBudget;
    }
}
