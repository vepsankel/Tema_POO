import org.json.JSONObject;
import java.time.LocalDate;

public class Employee extends Consumer {
    String companyName;
    double salary;

    Employee(){
        super();
    }

    Employee(JSONObject employee){
        super(employee);
        this.salary = employee.getDouble("salary");

        if (getCurrentCompany()!=null)
            Application.getInstance().getCompany(getCurrentCompany()).add(this, getCurrentDepartment());
        setPassword();
    }

    Employee(JSONObject employee, String department){
        super(employee);
        this.resume = new Resume(employee);
        this.salary = employee.getDouble("salary");

        if (getCurrentExperience().getDepartment() == null)
            this.getCurrentExperience().setDepartment(department);
        if (department != null)
            Application.getInstance().getCompany(getCurrentCompany()).add(this, department);
    }

    String getCurrentCompany(){
        for (Experience experience : resume.experience) {
            if (experience.finish == null){
                companyName = experience.company;
                return experience.company;
            }
        }
        return null;
    }

    String getCurrentDepartment(){
        Experience currentExperience = getCurrentExperience();
        if (currentExperience != null) return currentExperience.getDepartment();
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "\nIn company "+companyName+" with salary "+salary + "\n";
    }

    synchronized void fired(){
        for (Experience experience: resume.experience) {
            if (experience.company.equals(companyName) && experience.finish == null){
                experience.finish = LocalDate.now();
            }
        }

        User user = new User();
        user.resume = this.resume;
        user.notifications = this.notifications;

        while (social.size() > 0){
            Consumer friend = social.get(0);
            remove(friend);
            friend.add(user);
        }

        if (user.getSocial().size() == 0){
            try{
                throw new Exception(new IllegalAccessError());
            } catch (Exception e){
                e.printStackTrace();
            }

        }

//        for (Consumer friend: getSocial()) {
//            friend.add(user);
//        }
//        while (!getSocial().isEmpty()){
//            remove(social.get(0));
//        }

        Application.getInstance().removeConsumer(this);
        Application.getInstance().add(user);
        Application.getInstance().addConsumer(user);
    }

    void accepted(Job job){
        this.companyName = job.companyName;
        resume.experience.add(new Experience(LocalDate.now(), null, job.name, job.companyName, job.department));
        this.salary = job.salary;
        System.out.println(resume.information.name + " accepted on "+job.companyName);
    }

    public JSONObject toJSON() {
        return super.toJSON().put("salary",salary);
    }

    public double getSalary() {
        return salary;
    }
}
