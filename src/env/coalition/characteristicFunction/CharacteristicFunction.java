package coalition.characteristicFunction;

import coalition.structures.CoalitionAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coalition.structures.Coalition;

public abstract class CharacteristicFunction {
  private List<CoalitionAgent> setOfAgents;
  private Map<Integer, Coalition> maskToCoalition;

  public void setAgents(List<CoalitionAgent> setOfAgents) {
    this.setOfAgents = setOfAgents;
    this.maskToCoalition = new HashMap<Integer, Coalition>();
  }

  public abstract void putAdditionalInformation(Object... information);

  public abstract void removeAdditionalInformation(Object information);

  public abstract void generateValues(int numOfAgents);

  public abstract void clear();

  public abstract double getCoalitionValue(Coalition coalition);

  public abstract double[] getCoalitionValues();

  public double getCoalitionValue(int coalitionInBitFormat) {
    if (!this.maskToCoalition.containsKey(coalitionInBitFormat)) {
      Coalition c = new Coalition();

      int[] currCoalition = convertCombinationFromBitToByteFormat(
        coalitionInBitFormat,
        setOfAgents.size()
      );

      for (int i = 0; i < currCoalition.length; i++) 
        c.addAgent(setOfAgents.get(currCoalition[i]-1));

      this.maskToCoalition.put(coalitionInBitFormat, c);
    }

    return getCoalitionValue(
      this.maskToCoalition.get(coalitionInBitFormat)
    );
  }

  private int[] convertCombinationFromBitToByteFormat(
    int combinationInBitFormat,
    int numOfAgents) {
      int combinationSize = Integer.bitCount(combinationInBitFormat);

      int[] combinationInByteFormat = new int[combinationSize];

      int j = 0;
      for (int i = 0; i < numOfAgents; i++) {
        if ((combinationInBitFormat & (1 << i)) != 0) {
          combinationInByteFormat[j] = (int) (i + 1);
          j++;
        }
      }

      return combinationInByteFormat;
    }
}
