package unalcol.agents.search.classic;
import unalcol.sort.*;

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
public class ClassicSearchNodeOrder implements Order<ClassicSearchNode>{
  public String getCanonicalName(){
    return ClassicSearchNode.class.getCanonicalName();
  }

  public ClassicSearchNodeOrder() {
  }
  public int compare( ClassicSearchNode one, ClassicSearchNode two ){
    return (int)(one.cost - two.cost);
  }
}
