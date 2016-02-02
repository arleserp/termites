package unalcol.agents.search;
import unalcol.agents.*;
import unalcol.types.collection.vector.*;

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
public abstract class PathCost{
  public double evaluate( State state, SearchSpace space,
                          Vector<Action> path, ActionCost cost ){
    double c = 0.0;
    for(int i=0; i<path.size(); i++ ){
      Action action = path.get(i);
      c += cost.evaluate( state, action);
      state = space.succesor( state, action );
    }
    return c;
  }
}
