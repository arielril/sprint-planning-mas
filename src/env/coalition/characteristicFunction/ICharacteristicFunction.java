package coalition.characteristicFunction;

import coalition.structures.Coalition;

public interface ICharacteristicFunction {
  public void putAdditionalInformation(Object... information);

  public void removeAdditionalInformation(Object information);

  public void generateValues(int numOfAgents);

  public void clear();

  public double getCoalitionValue(Coalition coalition);

  public double[] getCoalitionValues();

  // public void storeToFile(String fileName);

  // public void readFromFile(String fileName)
}
