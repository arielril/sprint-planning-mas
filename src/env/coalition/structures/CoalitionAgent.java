package coalition.structures;

import java.util.ArrayList;

public class CoalitionAgent {
  private String name = "";
  private String type = "";
  private ArrayList<String> skills = new ArrayList<String>();

  public CoalitionAgent(String name) {
    init(name, "none");
  }

  public CoalitionAgent(String name, String type) {
    init(name, "none");
  }

  private void init(String name, String type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return this.name;
  }

  public String getType() {
    return this.type;
  }

  public ArrayList<String> getSkills() {
    return this.skills;
  }

  public boolean hasSkill(String skill) {
    return this.skills
      .stream()
      .anyMatch(s -> s.equals(skill));
  }

  public void addSkill(String skill) {
    this.skills.add(skill);
  }

  public void addSkills(ArrayList<String> skills) {
    this.skills.addAll(skills);
  }

  @Override
  public boolean equals(Object o) {
    return o.hashCode() == this.hashCode();
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
