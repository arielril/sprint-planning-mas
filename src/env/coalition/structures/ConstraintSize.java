package coalition.structures;

public class ConstraintSize extends Constraint {
  private long size = 0;
  private String type = "";

  public ConstraintSize(String agentThatAdded, long size, String type) {
    super(agentThatAdded);
    this.size = size;
    this.type = type;
  }

  public long getSize() {
    return this.size;
  }

  public String getType() {
    return this.type;
  }
}
