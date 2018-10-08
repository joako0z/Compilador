package Analisis;

import Estructura.Nodos;
import java.util.ArrayList;
import java.util.List;

public class CodGen {

    public static List<String> codigogenerado;
    int emitLoc = 0;
    int highEmitLoc = 0;
    int mp = 6, ac = 0, ac1 = 1, pc = 7, gp = 5, tmpOffset = 0;
    //int savedLoc1,savedLoc2;
    Semantico as;
    private List<String> Error = new ArrayList<String>();//

    CodGen(Nodos st) {
        as = new Semantico(Error);
        as.asignarTipos(st);
        as.asignarValor(st);
        //as.typeCheck(st);
        //as.symtab(st);
        //savedLoc1 = savedLoc2 = 0;
        codigogenerado = new ArrayList<String>();
    }

    void codeGen(Nodos syntaxTree) {
        emitComment("Compilación TM");
        /* Genera el preludio estándar */
        emitComment("Preludio estándar:");
        emitRM("LD", mp, 0, ac, "load maxaddress from location 0");
        emitRM("ST", ac, 0, ac, "clear location 0");
        emitComment("Fin del preludio estándar");
        cGen(syntaxTree);
        /* Termina */
        emitComment("Fin de la ejecución.");
        emitRO("HALT", 0, 0, 0, "");
    }

    void emitComment(String c) {
        codigogenerado.add("*" + c + "\n");
    }

    void emitRM(String op, int r, int d, int s, String c) {
        if (emitLoc > 9) {
            codigogenerado.add(" ");
        } else {
            codigogenerado.add("  ");
        }
        codigogenerado.add((emitLoc++) + ":" + "    " + op + "  " + r + "," + d + "(" + s + ")");
        codigogenerado.add("\t" + c);
        codigogenerado.add("\n");
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

    void cGen(Nodos tree) {
        if (tree != null) {
//            System.out.println("NodeType: " + tree.getNodeType());
            switch (tree.getNodeType()) {
                case "StructK":
                case "StmtK":
                    genStmt(tree);
                    break;
                case "ExpK":
                    genExp(tree);
                    break;
                default:
                    break;
            }
            cGen(tree.getHermano());
        }
    }

    void genStmt(Nodos tree) {
        Nodos p1;
        Nodos p2;
        Nodos p3;
        int savedLoc1, savedLoc2, currentLoc;
        int loc = 0;
        if (tree != null) {
            if (tree.getNodeType() != null) {
                switch (tree.getNodeType()) {
                    case "StructK":
                        String St = (String) tree.getType();
                        switch (St) {
                            case "TrueK":
                                if (tree.getSon(0) != null) {
                                    cGen(tree.getSon(0));
                                }
                                if (tree.getSon(1) != null) {
                                    cGen(tree.getSon(1));
                                }

                                break;
                            case "ConK":
                                if (tree.getSon(0) != null) {
                                    cGen(tree.getSon(0));
                                }
                                if (tree.getSon(1) != null) {
                                    cGen(tree.getSon(1));
                                }
                                break;
                            case "FalseK":
                                if (tree.getSon(0) != null) {
                                    cGen(tree.getSon(0));
                                }
                                if (tree.getSon(1) != null) {
                                    cGen(tree.getSon(1));
                                }
                                if (tree.getSon(2) != null) {
                                    cGen(tree.getSon(2));
                                }
                                break;
                        }
                        break;
                    case "StmtK":
                        String sT = (String) tree.getType();
                        switch (sT) {
                            case "MainK":
                                cGen(tree.getSon(0));
                                break;
                            case "IfK":                              
                                p1 = tree.getSon(0);
                                p2 = tree.getSon(1);
                                p3 = tree.getSon(2);
                                /* generate code for test expression */
                                cGen(p1);
                                savedLoc1 = emitSkip(1);
                                /* recurse on then part */
                                cGen(p2);
                                savedLoc2 = emitSkip(1);
                                currentLoc = emitSkip(0);
                                emitBackup(savedLoc1);
                                emitRM_Abs("JEQ", ac, currentLoc, "if");
                                emitRestore();
                                /* recurse on else part */
                                cGen(p3);
                                currentLoc = emitSkip(0);
                                emitBackup(savedLoc2);
                                emitRM_Abs("LDA", pc, currentLoc, "jmp to end");
                                emitRestore();                               
                                break; /* if_k */
                            case "WhileK":                                
                                p1 = tree.getSon(0);
                                p2 = tree.getSon(1);
                                savedLoc1 = emitSkip(0);                               
                                /* generate code for test */
                                cGen(p2);
                                /* generate code for body */
                                cGen(p1);
                                emitRM_Abs("JNE", ac, savedLoc1, "while");                               
                                break; /* while */
                            case "DoK":                                
                                p1 = tree.getSon(0);
                                p2 = tree.getSon(1);
                                savedLoc1 = emitSkip(0);
                                /* generate code for body */
                                cGen(p1);
                                /* generate code for test */
                                cGen(p2);
                                emitRM_Abs("JEQ", ac, savedLoc1, "do");
//                                emitRM_Abs("JNE", ac, savedLoc1, "do: jmp back to body");                                
                                break; /* repeat */

                            case "AssigmK":                               
                                /* generate code for rhs */
                                cGen(tree.getSon(0));
                                /* now store value */
                                loc = as.st.st_lookup(tree.getToken().getLexema());
                                emitRM("ST", ac, loc, gp, "assign: store value");
                                //if (TraceCode)                                
                                break; /* assign_k */

                             case "ReadK":
                                //emitRO("IN", ac, 0, 0, "read integer value");
                                emitRO("IN", ac, 0, 0, "read value");
                                loc = as.st.st_lookup(tree.getToken().getLexema());
                                emitRM("ST", ac, loc, gp, "read: store value");
                                break;
                            case "WriteK":
                                /* generate code for expression to write */
                                cGen(tree.getSon(0));
                                /* now output it */
                                emitRO("OUT", ac, 0, 0, "write ac");
                                break;
                            default:
                                break;
                        }
                }
            }
        }

    }

    int emitSkip(int howMany) {
        int i = emitLoc;
        emitLoc += howMany;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
        return i;
    }

    void emitBackup(int loc) {
        if (loc > highEmitLoc) {
            emitComment("BUG in emitBackup");
        }
        emitLoc = loc;
    }

    void emitRM_Abs(String op, int r, int a, String c) {
        if (emitLoc > 9) {
            codigogenerado.add(" ");
        } else {
            codigogenerado.add("  ");
        }
        codigogenerado.add(emitLoc + ":" + "    " + op + "  " + r + "," + (a - (emitLoc + 1)) + "(" + pc + ")");
        ++emitLoc;
        codigogenerado.add("\t" + c);
        codigogenerado.add("\n");
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

    void emitRestore() {
        emitLoc = highEmitLoc;
    }

    void emitRO(String op, int r, int s, int t, String c) {
        if (emitLoc > 9) {
            codigogenerado.add(" ");
        } else {
            codigogenerado.add("  ");
        }
        codigogenerado.add((emitLoc++) + ":" + "    " + op + "  " + r + "," + s + "," + t);
        codigogenerado.add("\t" + c);
        codigogenerado.add("\n");
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

    void genExp(Nodos tree) {
        int loc = 0;
        Nodos p1;
        Nodos p2;
        if (tree != null) {
            if (tree.getNodeType() != null) {
                switch (tree.getNodeType()) {
                    case "ExpK":
                        String E = (String) tree.getType();
                        switch (E) {
                            case "ConstK":                                
                                /* gen code to load integer constant using LDC */
                                if (tree.getToken().getType().equals("TKN_INTEGER")) {
                                    emitRM("LDC", ac, Double.valueOf(tree.getToken().getLexema()).intValue(), 0, "load const int");
                                } else if (tree.getToken().getType().equals("TKN_REAL")) {
                                   emitRM("LDC", ac, Double.valueOf(tree.getToken().getLexema()).doubleValue(), 0, "load const real");
                                }                                
                                break; /* ConstK */

                            case "IdK":                                
                                loc = as.st.st_lookup(tree.getToken().getLexema());
                                emitRM("LD", ac, loc, gp, "load id value");
                                //if (TraceCode)
                                break; /* IdK */

                            case "OpK":                                
                                p1 = tree.getSon(0);
                                p2 = tree.getSon(1);
                                /* gen code for ac = left arg */
                                cGen(p1);
                                /* gen code to push left operand */
                                emitRM("ST", ac, tmpOffset--, mp, "op: push left");
                                /* gen code for ac = right operand */
                                cGen(p2);
                                /* now load left operand */
                                emitRM("LD", ac1, ++tmpOffset, mp, "op: load left");
                                switch (tree.getToken().getType()) {
                                    case "TKN_PLUS":
                                        emitRO("ADD", ac, ac1, ac, "op +");
                                        break;
                                    case "TKN_MINUS":
                                        emitRO("SUB", ac, ac1, ac, "op -");
                                        break;
                                    case "TKN_TIMES":
                                        emitRO("MUL", ac, ac1, ac, "op *");
                                        break;
                                    case "TKN_OVER":
                                        emitRO("DIV", ac, ac1, ac, "op /");
                                        break;
                                    case "TKN_LT":
                                        emitRO("SUB", ac, ac1, ac, "op <");
                                        emitRM("JLT", ac, 2, pc, "br if true");
                                        emitRM("LDC", ac, 0, ac, "false case");
                                        emitRM("LDA", pc, 1, pc, "unconditional jmp");//
                                        emitRM("LDC", ac, 1, ac, "true case");
                                        break;
                                    case "TKN_LTE":
                                        emitRO("SUB", ac, ac1, ac, "op <=");
                                        emitRM("JLE", ac, 2, pc, "br if true");
                                        emitRM("LDC", ac, 0, ac, "false case");
                                        emitRM("LDA", pc, 1, pc, "unconditional jmp");
                                        emitRM("LDC", ac, 1, ac, "true case");
                                        break;
                                    case "TKN_GT":
                                        emitRO("SUB", ac, ac1, ac, "op >");
                                        emitRM("JGT", ac, 2, pc, "br if true");
                                        emitRM("LDC", ac, 0, ac, "false case");
                                        emitRM("LDA", pc, 1, pc, "unconditional jmp");
                                        emitRM("LDC", ac, 1, ac, "true case");
                                        break;
                                    case "TKN_GTE":
                                        emitRO("SUB", ac, ac1, ac, "op >=");
                                        emitRM("JGE", ac, 2, pc, "br if true");
                                        emitRM("LDC", ac, 0, ac, "false case");
                                        emitRM("LDA", pc, 1, pc, "unconditional jmp");
                                        emitRM("LDC", ac, 1, ac, "true case");
                                        break;
                                    case "TKN_COMPAR":
                                        emitRO("SUB", ac, ac1, ac, "op ==");
                                        emitRM("JEQ", ac, 2, pc, "br if true");
                                        emitRM("LDC", ac, 0, ac, "false case");
                                        emitRM("LDA", pc, 1, pc, "unconditional jmp");
                                        emitRM("LDC", ac, 1, ac, "true case");
                                        break;
                                    case "TKN_NOTEQUAL":
                                        emitRO("SUB", ac, ac1, ac, "op <>");
                                        emitRM("JNE", ac, 2, pc, "br if true");
                                        emitRM("LDC", ac, 0, ac, "false case");
                                        emitRM("LDA", pc, 1, pc, "unconditional jmp");
                                        emitRM("LDC", ac, 1, ac, "true case");
                                        break;

                                    default:
                                        emitComment("BUG: Unknown operator");
                                        break;
                                }
                                break;

                            default:
                                break;
                        }
                }
            }
        }
    }

    void emitRM(String op, int r, double d, int s, String c) {
        if (emitLoc > 9) {
            codigogenerado.add(" ");
        } else {
            codigogenerado.add("  ");
        }
        codigogenerado.add((emitLoc++) + ":" + "    " + op + "  " + r + "," + d + "(" + s + ")");
        codigogenerado.add("\t" + c);
        codigogenerado.add("\n");
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }
}
