package unalcol.agents.search;
import unalcol.agents.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Kunsamu</p>
 *
 * @author Jonatan Gómez
 * @version 1.0
 */
public interface ActionCost {
  public double evaluate( State state, Action action );
}
