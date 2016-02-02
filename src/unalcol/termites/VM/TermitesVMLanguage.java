package unalcol.termites.VM;

public class TermitesVMLanguage {

    protected static String[] oper_code = {
        "SEEK", "CARRY", "GOTO", "IFSEEKING", "IFCARRYING", "IFHASNEIGHBORS", "DIAGNOSE", "IFHASMESSAGE", "RESPONSEMSG"
    };

    public static int index(String oper) {
        int k = 0;
        while (k < oper_code.length && !oper_code[k].equals(oper)) {
            //System.out.println("oper found!" + oper);
            k++;
        }
        if (k < oper_code.length) {
            return k;
        } else {
            return -1;
        }
    }

    public static String code(int oper) {
        return oper_code[oper];
    }
}
