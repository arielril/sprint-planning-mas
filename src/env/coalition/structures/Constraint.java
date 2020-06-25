package coalition.structures;

public class Constraint {
  private String agentThatAdded = "";

  public Constraint(String agentThatAdded) {
    this.agentThatAdded = agentThatAdded;
  }

  public String getAgentThatAdded() {
    return this.agentThatAdded;
  }
}
