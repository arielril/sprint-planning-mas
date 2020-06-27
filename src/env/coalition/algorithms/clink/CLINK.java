package coalition.algorithms.clink;

import java.util.*;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.google.common.primitives.Doubles;

import coalition.characteristicFunction.CharacteristicFunction;
import coalition.structures.Coalition;
import coalition.structures.CoalitionAgent;
import coalition.structures.CoalitionStructure;

public class CLINK {
  public enum eColors {
    green,
    red
  }

  private CharacteristicFunction characteristicFunction;
  private Map<String, Coalition> coalitionClass = new LinkedHashMap<>();
  private Map<String, Set> coalitionSet = new LinkedHashMap<>();
  private Set<Set> best;

  Map<String, CoalitionAgent> setOfAgents;

  public Coalition getBestCoalition() {
    return convertSetToCoalition(this.best);
  }

  public void run(
    Set<String> agents, 
    MutableGraph<Set> interactionGraph, 
    CharacteristicFunction cf,
    Map<String, CoalitionAgent> setOfAgents) {
      this.characteristicFunction = cf;
      this.setOfAgents = setOfAgents;

      Set<Set> cs0 = new HashSet<>();

      for (String ag : agents){
        Set<String> a = ImmutableSet.of(ag);
        cs0.add(a);
      }

      // init the network for the current partition with the interaction graph
      MutableGraph<Set> net0 = Graphs.copyOf(interactionGraph);

      // init PL for each agent pair, considering the network connectivity
      PartitionLinkage pl = new PartitionLinkage(cs0, interactionGraph);

      while (pl.max() >= 0 && cs0.size() > 1) {
        // update the partition
        Object idI = pl.argmax().getI();
        Object idJ = pl.argmax().getJ();

        Set newCoalition = Sets.union(
          coalitionSet.get(idI),
          coalitionSet.get(idJ));
        cs0.remove(idI);
        cs0.remove(idJ);
        cs0.add(newCoalition);

        // update net0
        net0 = edgeContraction(
          net0,
          coalitionSet.get(idI),
          coalitionSet.get(idJ));
        
          pl = new PartitionLinkage(cs0, net0);
      }

      this.best = cs0;
    }

    private MutableGraph<Set> edgeContraction(
      MutableGraph<Set> interactionGraph,
      Set s1,
      Set s2) {
        MutableGraph<Set> newGraph = Graphs.copyOf(interactionGraph);

        Set union = Sets.union(s1, s2);

        Set<Set> neighbor = Sets.union(
          newGraph.predecessors(s1),
          newGraph.predecessors(s2))
          .immutableCopy();

        for (Set set : neighbor) {
          newGraph.putEdge(union, set);
        }

        newGraph.removeNode(s1);
        newGraph.removeNode(s2);

        return newGraph;
      }

    private Double lf(Coalition nodeU, Coalition nodeV) {
      return gain(nodeU.getAgentsNames(), nodeV.getAgentsNames());
    }

    private double gain(Set c1, Set c2) {
      Set union = Sets.union(c1, c2);

      double gain = getCoalitionValue(union) - (getCoalitionValue(c1) + getCoalitionValue(c2));

      return gain;
    }

    private double getCoalitionValue(Set node) {
      Coalition coalition = convertSetToCoalition(node);

      double value = characteristicFunction.getCoalitionValue(coalition);

      return value;
    }

    private CoalitionStructure convertSetToCoalitionStructure(Set<Set> setCs) {
      CoalitionStructure cs = new CoalitionStructure();

      for (Set node : setCs) {
        Coalition c = convertSetToCoalition(node);
        c.setValue(characteristicFunction.getCoalitionValue(c));
        cs.addCoalition(c);
      }

      return cs;
    }

    private Coalition convertSetToCoalition(Set node) {
      Coalition c = new Coalition("c");

      String coalitionStr = node.toString().replaceAll("\\[|\\]| ", "");

      String[] agents = coalitionStr.split(",");

      for (String str : agents) {
        c.addAgent(setOfAgents.get(str));
      }

      if (!coalitionClass.containsKey(c.getId()))
        coalitionClass.put(c.getId(), c);
      if (!coalitionSet.containsKey(c.getId()))
        coalitionSet.put(c.getId(), node);

      return c;
    }

    private class PartitionLinkage {
      private Table<String, String, Double> partitionLinkage;
      private Pair bestIndex;
      private double bestValue;
      private Cell<String, String, Double> best;

      public PartitionLinkage(
        Set<Set> coalitionStructSingletons,
        MutableGraph<Set> interactionGraph) {
          this.partitionLinkage = HashBasedTable.create();
          initPlTable(coalitionStructSingletons, interactionGraph);
        }

        public Pair argmax() {
          return new Pair(best.getRowKey(), best.getColumnKey());
        }

        public double max() {
          return best.getValue();
        }

        private void initPlTable(
          Set<Set> coalitionStructSingletons,
          MutableGraph<Set> interactionGraph) {
            ArrayList<Set> targetList = Lists.newArrayList(coalitionStructSingletons);

            for (int i = 0; i < targetList.size(); i++) {
              Coalition c1 = convertSetToCoalition(targetList.get(i));

              for (int j = 0; j < targetList.size(); j++) {
                Coalition c2 = convertSetToCoalition(targetList.get(j));

                if (isVerticesConnected(c1, c2, interactionGraph))
                  partitionLinkage.put(c1.getId(), c2.getId(), lf(c1, c2));
                else
                  partitionLinkage.put(c1.getId(), c2.getId(), Double.NEGATIVE_INFINITY);
              }
            }

            updateMax();
          }

        private void updateMax() {
          Ordering<Cell<String, String, Double>> ord = new Ordering<Cell<String, String, Double>>() {
            public int compare(
              Cell<String, String, Double> left, 
              Cell<String, String, Double> right) {
                return Doubles.compare(left.getValue(), right.getValue());
              }
          };

          best = ord.max(this.partitionLinkage.cellSet());
        }

        private boolean isVerticesConnected(
          Coalition c1, 
          Coalition c2,
          MutableGraph<Set> graph) {
            for (EndpointPair<Set> e : graph.edges()) {
              Coalition s1 = convertSetToCoalition(e.nodeU());
              Coalition s2 = convertSetToCoalition(e.nodeV());
              
              if (
                (c1.getId().equals(s1.getId()) && c2.getId().equals(s2.getId()))
                || (c1.getId().equals(s2.getId()) && c2.getId().equals(s1.getId()))
              ) return true;
            }

            return false;
          }
    }

    private class Pair {
      private String idI;
      private String idJ;

      public Pair(String i, String j) {
        this.idI = i;
        this.idJ = j;
      }

      public String getI() { return this.idI; }
      
      public String getJ() { return this.idJ; }
    }
}
