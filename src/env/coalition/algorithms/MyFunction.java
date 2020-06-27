package coalition.algorithms;

import java.util.*;

import coalition.characteristicFunction.CharacteristicFunction;
import coalition.structures.Coalition;
import coalition.structures.ConstraintSize;
import coalition.structures.Rule;

import com.google.common.collect.Sets;

public class MyFunction extends CharacteristicFunction {
  private Map<String, Double> coalitionValues = new LinkedHashMap<>();
  private Set<Rule> rules;
  private double numberOfAgents;
  private Set<ConstraintSize> constraintsSize;

  public MyFunction(
    double numberOfAgents,
    Set<Rule> rules,
    Set<ConstraintSize> constraintSize
  ) {
    this.setNumberOfAgents(numberOfAgents);
    this.rules = rules;
    this.constraintsSize = constraintSize;
  }

  public double getNumberOfAgents() {
    return numberOfAgents;
  }

  public void setNumberOfAgents(double numberOfAgents) {
    this.numberOfAgents = numberOfAgents;
  }

  @Override
  public double getCoalitionValue(Coalition coalition) {
    if (!this.coalitionValues.containsKey(coalition.generateHashCode())) {
      double coalitionValue = characteristicFunction(coalition);
      this.coalitionValues.put(coalition.generateHashCode(), coalitionValue);
    }

    return this.coalitionValues.get(coalition.generateHashCode());
  }

  @Override
  public double[] getCoalitionValues() {
    return null;
  }

  private double characteristicFunction(Coalition coalition) {
    double punishmentValue = constraintPunishment(coalition);
    if (punishmentValue < 0)
      return punishmentValue;
    return subAdditive(coalition) + superAdditive(coalition);
  }

  private double superAdditive(Coalition coalition) {
    double value = 0;

    for (Rule r: this.rules)
      if (Sets.difference(r.positiveRule, coalition.getAgentsNames()).size() == 0)
        if (
          (r.negativeRule.size() == 0)
          || (
            Sets.difference(r.negativeRule, coalition.getAgentsNames()).size() > 0
          )
        ) {
          value += r.value;
        }
    
    return value;
  }

  public double subAdditive(Coalition coalition) {
    double value = 0;
    value += subCoalitionSize(coalition);
    return value;
  }

  private double subCoalitionSize(Coalition coalition) {
    double value = 0;
    value = -1 * (Math.pow(coalition.getAgents().size()-1, 3.0));
    return value;
  }

  private double constraintPunishment(Coalition coalition) {
    if (coalition.getNumberByType("job") != 1)
      return -10.0;

    int numberOfPunishment = 0;
    for (ConstraintSize cSize : this.constraintsSize) {
      double temp = cSize.getSize() - coalition.getNumberByType(cSize.getType());
      numberOfPunishment += (int) Math.max(temp, 0.0);
    }

    return -1 * numberOfPunishment;
  }

  public void clear() {}

  public void generateValues(int numOfAgents) {}

  public void removeAdditionalInformation(Object info) {}

  public void putAdditionalInformation(Object... infos) {}
}
