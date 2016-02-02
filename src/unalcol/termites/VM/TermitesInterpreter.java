package unalcol.termites.VM;
import java.util.Observable;
import unalcol.termites.World.Termite;

public abstract class TermitesInterpreter extends Observable {
    boolean running;
    int id;
    int operationNumber;
    
    /* Sensor information */
    private float[] pheromone;
    private boolean[] termitesNeighbor;
    private int seekingStatus;
    private String message;
    
    public TermitesInterpreter() {
        operationNumber = 0;
    }

    public TermitesInterpreter(int ID, String code) {
        interpret(code);
        id = ID;
        ID++;
        operationNumber = 0;
    }

    public void updatePercepts(float[] temps, float[] pheromone, boolean[] termitesNeighbor, int seekingStatus, String message, boolean PI, boolean MT) {
        this.pheromone = pheromone;
        this.termitesNeighbor = termitesNeighbor;
        this.seekingStatus = seekingStatus;
        this.message = message;
    }

    public int runNextInstruction(String code) {
        //boolean flag = true;
        String[] code_lines = code.split("\n");
        int retCode = -1;
        //for (int i = 0; i < code_lines.length; i++) {
        //System.out.println("Code: " + code_lines[operationNumber]);
        try {
            int[] oper = operation(code_lines[operationNumber], operationNumber + 1);
            //System.out.println("operation number: " + oper[0]);
            if (oper != null) {
                retCode = operate(oper);

            }
        } catch (Exception e) {
            //  flag = false;
            System.err.println(e.getMessage());
        }

        operationNumber += 1;
        //}
        return retCode;
    }

    protected boolean interpret(String code) {
        boolean flag = true;
        String[] code_lines = code.split("\n");
        for (int i = 0; i < code_lines.length; i++) {
            //System.out.println(code_lines[i]);
            try {
                int[] oper = operation(code_lines[i], i + 1);
                if (oper != null) {
                    continue;
                }
            } catch (Exception e) {
                flag = false;
                System.err.println(e.getMessage());
            }
        }
        return flag;
    }

    public int[] operation(String code_line, int line) throws Exception {
        String[] oper = code_line.split(" ");
        if (oper.length > 0) {
            int k = 0;
            while (oper[k].length() == 0) {
                k++;
            }
            String code = oper[k].toUpperCase();
            k++;
            while (k < oper.length && oper[k].length() == 0) {
                k++;
            }
            if (k < oper.length) {
                String arg = oper[k];
                //System.out.println(code + "(" + arg + ")");
                int index = TermitesVMLanguage.index(code);
                if (index >= 0) {
                    try {
                        int value = Integer.parseInt(arg);
                        return new int[]{index, value};
                    } catch (NumberFormatException e) {
                        throw new Exception("[Syntax Error] Not a valid argument " + arg + " at line " + line);
                    }
                } else {
                    throw new Exception("[Syntax Error] Unknown operation " + code + " at line " + line);
                }
            } else {
                throw new Exception("[Syntax Error] No argument for operation " + code + " at line " + line);
            }
        }
        return null;
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        running = false;
    }

    public int getId() {
        return id;
    }

    //operate always will return an action
    private int operate(int[] oper) {
        int code = -1;
        switch (oper[0]) {
            case 0:
                code = seek();
                break;
            case 1:
                code = carry();
                break;
            case 2:
                code = gotoline(oper[1]);
                break;
            case 3:
                code = isSeeking();
                break;
            case 4:
                code = isCarrying();
                break;
            case 5:
                code = hasNeighbors();
                break;
            case 6:
                code = diagnose();
                break;
            case 7:
                code = ifhasMessage();
                break;
            case 8:
                code = responseMsg();
                break;
            default:
                return code;
        }
        return code;
    }

    public int seek() {
        int dirPos = 0;
        boolean useRandomdir = true;
        for (int k = 1; k < pheromone.length; k++) {
            if (pheromone[k] >= pheromone[dirPos]) {
                dirPos = k;
                useRandomdir = false;
            }
            
        }
        if(useRandomdir){
            dirPos = (int) (Math.random() * 8.0);
        }        
        return dirPos; 
    }

    public int carry() {
//        if (seekingStatus == Termite.CARRYING) {
       int dirPos = 0;
        boolean useRandomdir = true;
        for (int k = 1; k < pheromone.length; k++) {
            if ((pheromone[dirPos] >= pheromone[k])) {
                dirPos = k;
                useRandomdir = false;
            }
        }

        if(useRandomdir){
            dirPos = (int) (Math.random() * 8.0);
        }
        return dirPos;
    }

    private int gotoline(int i) {
        this.operationNumber = i - 1;
        return -1;
    }

    public int isSeeking() {
        if (this.getSeekingStatus() != Termite.SEEKING) {
            this.operationNumber++;
        }
        return -1;
    }

    public int isCarrying() {
        if (this.getSeekingStatus() != Termite.CARRYING) {
            this.operationNumber++;
        }
        return -1;
    }

    private int hasNeighbors() {
        boolean hasNeighbors = false;
        for (int k = 0; k < getTermitesNeighbor().length; k++) {
            //             System.out.println("Seeking - PH" + pheromone[k]);
            //choose the route with most pheromone
            if (getTermitesNeighbor()[k]) {
                //System.out.println("hello Term!");
                hasNeighbors = true;
                break;
            }
        }
        if (!hasNeighbors) {
            this.operationNumber++;
        }
        return -1;
    }

    public int diagnose() {
        return 9;
    }

    private int ifhasMessage() {
        if (this.message.equals("")) {
            this.operationNumber++;
        }
        return -1;
    }

    private int responseMsg() {
        return 10;
    }

    /**
     * @return the termitesNeighbor
     */
    public boolean[] getTermitesNeighbor() {
        return termitesNeighbor;
    }

    /**
     * @return the seekingStatus
     */
    public int getSeekingStatus() {
        return seekingStatus;
    }

    int runNextInstruction() {
        return -1;
    }

}
