package coalition;

import java.util.*;
import java.util.logging.Logger;
import java.lang.reflect.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;

import cartago.Artifact;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.parser.ParserException;

import coalition.algorithms.ICoalitionFormationSolver;
import coalition.structures.CoalitionAgent;
import coalition.structures.Task;
import env.coalition.structures.Coalition;
import coalition.structures.CoalitionStructure;
import coalition.structures.ConstraintBasic;
import coalition.structures.Rule;
import coalition.algorithms.clink.*;

public class CoalitionFormation extends Artifact {
  // private final static String DEFAULT_TYPE = "NA";
  // private final static String FAKE_TYPE = "fake";

  private final Logger logger = Logger.getLogger(CoalitionFormation.class.getName());

  private final String runningAlgo = "running algorithm";
  private final String waitingInput = "waitingForInputs";

  private boolean onlyOneCoalition;

  private List<String> owners = new ArrayList<String>();
  private Set<ConstraintBasic> positiveConstraintsSet = new HashSet<ConstraintBasic>();
  private Set<ConstraintBasic> negativeConstraintsSet = new HashSet<ConstraintBasic>();
  private Set<ConstraintSize> constraintSizeSet = new HashSet<ConstraintSize>();
  private Set<Rule> rulesSet = new HashSet<Rule>();
  private Set<Task> tasksSet = new HashSet<Task>();

  private BiMap<Integer, CoalitionAgent> agentIds;

  private ICoalitionFormationSolver solver;

  // guard variables
  private int agNum, positiveConstraintNum, negativeConstraintNumber, rulesNumber = 0;
  // ---------------

  public void init(String owner, String algorithm) {
    setup(owner, algorithm, false);
  }

  public void init(String owner, String algorithm, boolean onlyOneCoalition) {
    setup(owner, algorithm, onlyOneCoalition);
  }

  private void setup(String owner, String algorithm, boolean onlyOneCoalition) {
    logger.info("creating coalition formation");

    this.onlyOneCoalition = onlyOneCoalition;

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

  private void addOwner(String owner) {
    if (owner.equals("")) return;
    if (!owners.contains(owner)) owners.add(owner);
  }

  private void setupAlgorithm(String algorithm) throws InstantiationException,
    IllegalAccessException, IllegalArgumentException, InvocationTargetException,
    NoSuchMethodException, SecurityException, ClassNotFoundException {
    // Class<?> algoClass = Class.forName(algorithm);
    // Constructor<?> algConstructor = algoClass.getConstructor();

    this.solver = new AdapterClink();
    this.solver.setup();
  }

  private Literal convertCoalitionToLiteral(Coalition c) {
    Literal l = Literal.parseLiteral("coalition");

    StringBuilder s = new StringBuilder();
    s.append('[');
    for (String ch : c.getAgentsNames()) {
      s.append(a);
      s.append(',');
    }

    if (s.charAt(s.length() - 1) == ',')
      s.deleteCharAt(s.length() - 1);
    s.append(']');

    try {
      l.addTerm(ASSyntax.parseTerm(c.getValue().toString()));
      l.addTerm(ASSyntax.parseTerm(s.toString()));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return l;
  }

  private Literal convertCoalitionStructureToLiteral(env.coalition.structures.CoalitionStructure cs) {
    Literal l = Literal.parseLiteral("coalitionStructure");

    if (cs != null) {
      StringBuilder s = new StringBuilder();

      try {
        s.append('[');
        for (Coalition c : cs.getCoalitions()) {
          s.append(convertCoalitionToLiteral(c).toString());
          s.append(',');
        }

        if (s.charAt(s.length() - 1) == ',')
          s.deleteCharAt(s.length() - 1);
        s.append(']');

        l.addTerm(ASSyntax.parseTerm(s.toString()));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    return l;
  }

  private void updateCoalitionStructure(CoalitionStructure cs) {
    Literal l;

    if (this.onlyOneCoalition) {
      if (cs != null) {
        Coalition c = cs.getValuableCoalition();
        l = convertCoalitionToLiteral(c);
      } else {
        l = Literal.parseLiteral("coalition");
      }
    } else {
      l = convertCoalitionStructureToLiteral(cs);
    }

    signal(l.getFunctor(), l.getTermArray());
  }

  /**
   * ---------------------------------------------------------------------------------
   * Operations
   * ---------------------------------------------------------------------------------
   */

  private void addAgent(String name, String type, String[] skills) {
    CoalitionAgent ag = new CoalitionAgent(name, type);
    ag.addSkills(new ArrayList<String>(Arrays.asList(skills)));

    if (!agentIds.containsKey(ag.hashCode())) 
      agentIds.puth(ag.hashCode(), ag);
  }

  @OPERATION
  public void addAgentToSet(String name, String type, Object[] skills) {
    addAgent(name, type, Arrays.copyOf(skills, skills.length, String[].class));
  } 

  @OPERATION
  public void addAgentToSet(String name, String type) {
    addAgent(name, type, new String[0]);
  }

  @OPERATION
  public void addTask(String name, Object[] skills) {
    Task task = new Task(
      name, 
      new ArrayList<>(Arrays.asList(
        Arrays.copyOf(skills, skills.length, String[].class))));

    this.tasksSet.add(task);
  }

  @OPERATION( guard = "hasAllInputs" )
  public void runSolver() {
    logger.info("running coalition formation solver");

    defineObsProperty(this.runningAlgo);
    
    try {
      CoalitionStructure cs = solver.solveCoalitionStructureGeneration(
        this.agentIds.values(),
        this.positiveConstraintsSet, 
        this.negativeConstraintsSet,
        this.constraintSizeSet,
        this.tasksSet,
        this.rulesSet);
      updateCoalitionStructure(cs);
    } catch (Exception e) {
      logger.warning("failed to solve the coalition. "+e.getMessage());
      e.printStackTrace();
    }

    removeObsProperty(this.runningAlgo);
    removeObsProperty(this.waitingInput);
    logger.info("coalition formation algorithm has finished");
  }

  /**
   * ---------------------------------------------------------------------------------
   * Guards
   * ---------------------------------------------------------------------------------
   */

  @GUARD
  public boolean hasAllInputs() {
    boolean ready = true;

    if (getObsProperty(this.waitingInput) == null)
      defineObsProperty(this.waitingInput);

    
    ready = (this.agentIds.size() >= this.agNum)
      & (positiveConstraintsSet.size() >= this.positiveConstraintNum)
      & (negativeConstraintsSet.size() >= this.negativeConstraintNumber)
      & (rulesSet.size() >= this.rulesNumber);

    return ready;
  }
}
























