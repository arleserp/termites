import unalcol.agents.examples.labyrinth.teseo.simple.*;
import unalcol.agents.*;
import unalcol.agents.examples.labyrinth.teseo.*;
public class TheAgentProgram extends SimpleTeseoAgentProgram{
  public int hello;
  public int accion( boolean PF, boolean PD, boolean PA, boolean PI, boolean MT ){
     int dir = (int)(4*Math.random());
     while( (dir==0 && PF) || (dir==1 && PD) || (dir==2 && PI) || (dir==3 && PA) ){
        dir = (int)(4*Math.random());
     };
     return dir;
  }

}
