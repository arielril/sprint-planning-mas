package coalition.structures;

import java.util.ArrayList;

public class Task {
  private String name = "";
  private ArrayList<String> skills = new ArrayList<String>();

  public Task(String name, ArrayList<String> skills) {
    this.name = name;
    this.skills = skills;
  }

  public String getName() {
    return this.name;
  }

  public ArrayList<String> getSkills() {
    return this.skills;
  }

  public boolean hasSkill(String skill) {
    return this.skills
      .stream()
      .anyMatch(s -> s.equals(skill));
  }

  @Override
  public boolean equals(Object o) {
    return o.hashCode() == this.hashCode();
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }
}
