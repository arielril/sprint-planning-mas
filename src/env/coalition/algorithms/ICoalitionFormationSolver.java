package coalition.algorithms;

import java.util.Set;

import coalition.structures.*;

public interface ICoalitionFormationSolver {
  public CoalitionStructure solveCoalitionStructureGeneration(
      Set<CoalitionAgent> agents,
      Set<ConstraintBasic> positive,
      Set<ConstraintBasic> negative,
      Set<ConstraintSize> constraintSizeSet,
      Set<Task> tasks,
      Set<Rule> rules);

  public void setup();
}
