import java.util.ArrayList;

public class Test {
    public static boolean testIsRunning = false;

    public static void main(String[] args) {
        JsonRW.readJSON();
        testIsRunning = true;

        usersApply();
        evaluate();

        JsonRW.writeJSON();
    }

    private static void usersApply(){
        for (User user: Application.getInstance().listUsers) {
            ArrayList<Job> jobs = Application.getInstance().getJobs(user.companiesNames);

            for (Job job : jobs) {
                job.apply(user);
            }
        }
    }

    private static void evaluate(){
        for (Company company: Application.getInstance().getCompanies()) {
            for (Job job: company.getJobs()) {
                company.manager.process(job);
            }
        }
    }

}