package coalition.structures;

public class ConstraintBasic extends Constraint {
  private String[] constraint;

  public ConstraintBasic(String agentThatAdded, String[] constraint) {
    super(agentThatAdded);
    this.constraint = constraint;
  }

  public String[] getConstraint() {
    return this.constraint;
  }

  public String[] getTotalConstraint() {
    String[] totalConstraints = new String[constraint.length + 1];

    System.arraycopy(constraint, 0, totalConstraints, 0, constraint.length);
    totalConstraints[totalConstraints.length - 1] = getAgentThatAdded();
    return totalConstraints;
  }
}
