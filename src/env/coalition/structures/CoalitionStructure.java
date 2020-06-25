package coalition.structures;

import coalition.structures.Coalition;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

public class CoalitionStructure {
  private List<Coalition> coalitions;
  private String name;
  private Double value;

  public CoalitionStructure() {
    init("none");
  }

  public CoalitionStructure(String name) {
    init(name);
  }

  private void init(String name) {
    this.coalitions = new ArrayList<>();
    this.name = name;
    this.value = Double.NaN;
  }

  public void addCoalition(Coalition coalition) {
    this.coalitions.add(coalition);
    this.value += coalition.getValue();
  }

  public Coalition[] getCoalitions() {
    return this.coalitions.toArray(new Coalition[this.coalitions.size()]);
  }

  public Coalition getValuableCoalition() {
    this.coalitions.sort(new Comparator<Coalition>() {
        @Override
        public int compare(Coalition o1, Coalition o2) {
          return o1.getValue() >= o2.getValue() ? -1 : 1;
        }
      });

    return this.coalitions.get(0);
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
}
