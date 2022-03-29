package sg.edu.nus.iss.vttpssfassessmentpractice.model;

import java.util.LinkedList;
import java.util.List;

public class UserInput {
    private String name;
    private List<Integer> quantities = new LinkedList<>();

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Integer> getQuantities() {
        return quantities;
    }
    public void setQuantities(List<Integer> quantities) {
        this.quantities = quantities;
    }

}
