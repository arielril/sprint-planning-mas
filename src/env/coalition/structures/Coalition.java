package coalition.structures;

import java.nio.charset.StandardCharsets;
import java.util.*;

import coalition.structures.CoalitionAgent;

import com.google.common.hash.Hashing;

public class Coalition {
  private Set<CoalitionAgent> agents;
  private String name;
  private String id;
  private Double value;
  private Map<String, Integer> agentTypes = new HashMap<String, Integer>();

  public Coalition() {
    init("none");
  }

  public Coalition(String name) {
    init(name);
  }

  private void init(String name) {
    this.agents = new HashSet<CoalitionAgent>();
    this.name = name;
    this.value = Double.NaN;
  }

  public void addAgent(CoalitionAgent agent) {
    this.agents.add(agent);

    updateTypes(agent);

    this.id = generateHashCode();
  }

  private void updateTypes(CoalitionAgent agent) {
    String key = agent.getType();

    agentTypes.put(key, agentTypes.getOrDefault(key, 0) + 1);
  }

  public Integer getNumberByType(String type) {
    return agentTypes.getOrDefault(type, 0);
  }

  public Set<CoalitionAgent> getAgents() {
    return this.agents;
  }

  public Set<String> getAgentsNames() {
    Set<String> s = new HashSet<>();

    for (CoalitionAgent ag : this.agents)
      s.add(ag.getName());

    return s;
  }

  public String getCoalitionName() {
    return this.name;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public Double getValue() {
    return this.value;
  }

  public String generateHashCode() {
    StringBuilder sb = new StringBuilder();

    for (CoalitionAgent ag : this.agents)
      sb.append(ag.getName());

    return Hashing.sha256()
      .hashString(sb.toString(),StandardCharsets.UTF_8)
      .toString();
  }
}
