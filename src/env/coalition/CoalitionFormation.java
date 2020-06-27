package coalition;

import java.util.*;
import java.util.logging.Logger;
import java.lang.reflect.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import cartago.Artifact;

import coalition.algorithms.ICoalitionFormationSolver;
import coalition.structures.*;
import coalition.algorithms.clink.*;

public class CoalitionFormation extends Artifact {
  private final static String DEFAULT_TYPE = "NA";
  private final static String FAKE_TYPE = "fake";

  private final Logger logger = Logger.getLogger(CoalitionFormation.class.getName());

  private final String runningAlgo = "running algorithm";
  private final String waitingInput = "waitingForInputs";

  private List<String> owners = new ArrayList<String>();

  private Set<ConstraintBasic> positiveConstraintsSet = new HashSet<ConstraintBasic>();
  private Set<ConstraintBasic> negativeConstraintsSet = new HashSet<ConstraintBasic>();
  private Set<ConstraintSize> constraintSizeSet = new HashSet<ConstraintSize>();

  private Set<Rule> rulesSet = new HashSet<Rule>();
  private Set<Task> tasksSet = new HashSet<Task>();

  private BiMap<Integer, CoalitionAgent> agentIds;

  private ICoalitionFormationSolver solver;

  void init(String owner, String algorithm) {
    setup(owner, algorithm, false);
  }

  void init(String owner, String algorithm, boolean onlyOneCoalition) {
    setup(owner, algorithm, onlyOneCoalition);
  }

  private void setup(String owner, String algorithm, boolean onlyOneCoalition) {
    logger.info("creating coalition formation");

    addOwner(owner);

    try {
      setupAlgorithm(algorithm);
    } catch (Exception e) {
      logger.warning("error setting up the coalition formation algorithm. "+ e.getMessage());
      e.printStackTrace();
    }

    agentIds = HashBiMap.create();

    logger.info("coalition formation created");
  }

  void runSolver() {
    logger.info("running coalition formation solver");

    defineObsProperty(runningAlgo);
    
    try {
      solver.solveCoalitionStructureGeneration(
        agentIds.values(),
        positiveConstraintsSet, 
        negativeConstraintsSet,
        constraintSizeSet,
        tasksSet,
        rulesSet);
    } catch (Exception e) {
      logger.warning("failed to solve the coalition. "+e.getMessage());
      e.printStackTrace();
    }

    removeObsProperty(runningAlgo);
    removeObsProperty(waitingInput);
    logger.info("coalition formation algorithm has finished");
  }

  private void addOwner(String owner) {
    if (owner.equals("")) return;
    if (!owners.contains(owner)) owners.add(owner);
  }

  private void setupAlgorithm(String algorithm) throws InstantiationException, 
    IllegalAccessException, IllegalArgumentException, InvocationTargetException,
    NoSuchMethodException, SecurityException, ClassNotFoundException {
    Class<?> algoClass = Class.forName(algorithm);
    // Constructor<?> algConstructor = algoClass.getConstructor();

    this.solver = new AdapterClink();
    this.solver.setup();
  }
}
























