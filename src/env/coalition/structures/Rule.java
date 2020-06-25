package coalition.structures;

import com.google.common.collect.Sets;
import java.util.Set;

public class Rule {
  public Set<String> positiveRule = null;
  public Set<String> negativeRule = null;
  public double value = 0;
  boolean hasNegation = false;

  public Rule(String[] positiveRule, String[] negativeRule, double value) {
    this.positiveRule = Sets.newHashSet(positiveRule);
    this.negativeRule = Sets.newHashSet(negativeRule);
    this.value = value;
  }
}
