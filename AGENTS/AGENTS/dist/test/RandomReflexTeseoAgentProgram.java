import unalcol.agents.*;
import unalcol.types.collection.vector.*;
import unalcol.agents.exam ples.labyrinth.*;
import unalcol.agents.simulate.util.*;

public class RandomReflexTeseoAgentProgram implements AgentProgram{
  protected SimpleLanguage language;
  protected Vector<String> cmd = new Vector<String>();
  public RandomReflexTeseoAgentProgram() {
  }

  public void setLanguage(  SimpleLanguage _language ){
    language = _language;
  }

  public void init(){
    cmd.clear();
  }

  /**
   * execute
   *
   * @param perception Perception
   * @return Action[]
   */
  public Action compute(Percept p){
    if( cmd.size() == 0 ){

        //Este agente reflejo, verifica las direcciones disponibles y aleatoriamente escoge una
        // y avanza, algun dia saldra?
        //1. para poder acceder a los metodos de  PerceptionLabyrinth
        //   Referenciamos la subclase desde la supoerclase
        // 2. La salida es un arreglo de Action donde deben y pueden ir en cada
        //    uno de sus subindices clases Rotate y Advance, en teoria se puede
        //    avanzar mas de una vez en cada llamada a este metodo, aunque no se recomienda
        if (goalAchieved(p)){
          return new Action( language.getAction(0) ); // die
        }
        // 3. Pasa hacer un agente reflejo mas interesante, no tiene estrategia
        //    Es un egente loco que escoge la direccion al azar
        int direcciones = (int) ( (Math.random() * 100.0) % 4.0); //lo usaremos para almacenar la direccion
        boolean bandera = true;
        int i = 0;
        do{
          switch(direcciones){
            case 0: //Adelante
              if(!((Boolean)p.getAttribute(language.getPercept(0))).booleanValue()){
                cmd.add(language.getAction(2));
                bandera = false; //para que salga
              }
              else direcciones++;
              break;
            case 1: //Derecha
              if(!((Boolean)p.getAttribute(language.getPercept(1))).booleanValue()){
                cmd.add(language.getAction(3)); // rotate
                cmd.add(language.getAction(2)); // advance
                bandera = false; //para que salga
              }
              else direcciones++;
              break;
            case 2: //Atras
              if(!((Boolean)p.getAttribute(language.getPercept(2))).booleanValue()){
                cmd.add(language.getAction(3)); // rotate
                cmd.add(language.getAction(3)); // rotate
                cmd.add(language.getAction(2)); // advance
                bandera = false; //para que salga
              }else direcciones++;
            break;
            case 3: //Izquierda
              if(!((Boolean)p.getAttribute(language.getPercept(3))).booleanValue()) {
                cmd.add(language.getAction(3)); // rotate
                cmd.add(language.getAction(3)); // rotate
                cmd.add(language.getAction(3)); // rotate
                cmd.add(language.getAction(2)); // advance
                bandera = false; //para que salga
              }else direcciones = 0;
            break;
          }
          i++;
        }while (bandera && i < 4);
      }
      String x = cmd.get(0);
      cmd.del(0);
      return new Action(x);
  }

  /**
   * goalAchieved
   *
   * @param perception Perception
   * @return boolean
   */
  public boolean goalAchieved( Percept p ){
    return (((Boolean)p.getAttribute(language.getPercept(4))).booleanValue());
  }
}
