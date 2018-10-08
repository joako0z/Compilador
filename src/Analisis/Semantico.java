
package Analisis;

import Estructura.Token;
import Estructura.Nodos;
import java.util.ArrayList;
import java.util.List;

public class Semantico {

    private boolean ERROR = false;
    private List<String> Error = new ArrayList<String>();
    private boolean aceptarTipo = true;
    //private boolean establecerValor = true;
    //private String Booleano = "true";
    private int location = 0;
    TablaDeSimbolos st;
    boolean condicion = false;
    boolean save = true;

    Semantico(List<String> Error) {
        st = new TablaDeSimbolos();
        this.Error = Error;
    }

    public List<String> getErrores() {
        return Error;
    }

    public boolean getError() {
        return ERROR;
    }

    public String getSymTab() {
        return st.printSymTab();
    }

    public String getSymTabLog() {
        return st.getSymTabLog();
    }

    public Nodos iniciaSemantico(Nodos t) {
        Nodos semTipo = asignarTipos(t);
        //st = new SymTab(semTipo);
        Nodos semValor = asignarValor(semTipo);
        /*TreeNode semT = evalType(semTipo);
        TreeNode semantico = typeCheck(semT);*/
        //symtab(semTipo);
        //st = new SymTab(semTipo);
        if (ERROR == false) {
            Error.add(" ");
        }
        return semValor;
    }

    void typeError(Nodos t, String message) {
        Error.add("\nError de tipo en la linea: " + t.getToken().getLine() + " " + message);
        ERROR = true;
    }

    public Nodos asignarTipos(Nodos t) {
        if (t != null) {

            if (t.getNodeType() != null) {
                switch (t.getNodeType()) {
                    case "StmtK": {
                        String sT = (String) t.getType();
                        switch (sT) {
                            case "MainK":
                                //t.setExpType(ExpType.Real);
                                if (t.getSon(0) != null) {
                                    asignarTipos(t.getSon(0));
                                }
                                break;
                            case "DecIntK":
                                if (t.getSon(0) != null) {
                                    asignarTipos(t.getSon(0));
                                }
                                if (t.getHermano() != null) {
                                    asignarTipos(t.getHermano());
                                }
                                break;
                            case "DecFloatK":
                                if (t.getSon(0) != null) {
                                    asignarTipos(t.getSon(0));
                                }
                                if (t.getHermano() != null) {
                                    asignarTipos(t.getHermano());
                                }
                                break;
                            case "DecBoolK":
                                if (t.getSon(0) != null) {
                                    asignarTipos(t.getSon(0));
                                }
                                if (t.getHermano() != null) {
                                    asignarTipos(t.getHermano());
                                }
                                break;
                            case "IntK":
                                t.setExpType("Integer");
                                st.st_insertDec(t.getToken(), t.getExpType());
                                if (t.getHermano() != null) {
                                    asignarTipos(t.getHermano());
                                }
                                break;
                            case "FloatK":
                                t.setExpType("Real");
                                st.st_insertDec(t.getToken(), t.getExpType());
                                if (t.getHermano() != null) {
                                    asignarTipos(t.getHermano());
                                }
                                break;
                            case "BoolK":
                                t.setExpType("Boolean");
                                st.st_insertDec(t.getToken(), t.getExpType());
                                if (t.getHermano() != null) {
                                    asignarTipos(t.getHermano());
                                }
                                break;
//                            case VoidK:
//                                t.setExpType(ExpType.Void);
//                                AgregarNodo(t.getToken().getLexema(), t.getExpType());
//                                if (t.getBro() != null) {
//                                    asignarTipos(t.getBro());
//                                }
//                                break;
                            case "IfK":
                                if (t.getSon(0) != null) {
                                    asignarTipos(t.getSon(0));
                                }
                                if (t.getSon(1) != null) {
                                    asignarTipos(t.getSon(1));
                                }
                                if (t.getSon(2) != null) {
                                    asignarTipos(t.getSon(2));
                                }
                                if (t.getHermano() != null) {
                                    asignarTipos(t.getHermano());
                                }
                                break;
                            case "WhileK":
                            case "DoK":
                                if (t.getSon(0) != null) {
                                    asignarTipos(t.getSon(0));
                                }
                                if (t.getSon(1) != null) {
                                    asignarTipos(t.getSon(1));
                                }
                                if (t.getHermano() != null) {
                                    asignarTipos(t.getHermano());
                                }
                                break;
                            case "AssigmK":
                                t.setExpType(establecerExpType(t.getToken()));
                                st.st_insert(t.getToken(), t.getExpType());
                                if (t.getSon(0) != null) {
                                    asignarTipos(t.getSon(0));
                                }
                                if (t.getHermano() != null) {
                                    asignarTipos(t.getHermano());
                                }
                                break;
                            case "WriteK":
                                if (t.getSon(0) != null) {
                                    t.getSon(0).setExpType(establecerExpType(t.getSon(0).getToken()));
                                    asignarTipos(t.getSon(0));
                                }
                                if (t.getSon(1) != null) {
                                    t.getSon(1).setExpType(establecerExpType(t.getSon(1).getToken()));
                                    asignarTipos(t.getSon(1));
                                }
                                if (t.getHermano() != null) {
                                    asignarTipos(t.getHermano());
                                }
                                break;
                            case "ReadK":
                                t.setExpType(establecerExpType(t.getToken()));
                                st.st_insert(t.getToken(), t.getExpType());
                                if (t.getHermano() != null) {
                                    asignarTipos(t.getHermano());
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    }

                    case "ExpK": {
                        String E = (String) t.getType();
                        switch (E) {
                            case "OpK":
                                t.setExpType("Boolean");
                                if (t.getSon(0) != null) {
                                    t.getSon(0).setExpType(establecerExpType(t.getSon(0).getToken()));
                                    asignarTipos(t.getSon(0));
                                }
                                if (t.getSon(1) != null) {
                                    t.getSon(1).setExpType(establecerExpType(t.getSon(1).getToken()));
                                    asignarTipos(t.getSon(1));
                                }
                                break;
                            case "ConstK":
                                if (t.getToken().getType().equals("TKN_INTEGER")) {
                                    t.setExpType("Integer");
                                }
                                if (t.getToken().getType().equals("TKN_REAL")) {
                                    t.setExpType("Real");
                                }
                                if (t.getHermano() != null) {
                                    asignarTipos(t.getHermano());
                                }
                                break;
                            case "IdK":
                                t.setExpType(st.st_contains(t.getToken()));
                                st.st_insert(t.getToken(), t.getExpType());
                                if (t.getHermano() != null) {
                                    asignarTipos(t.getHermano());
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    }

                    case "StructK": {
                        String St = (String) t.getType();
                        switch (St) {
                            case "TrueK":
                                if (t.getSon(0) != null) {
                                    if (t.getSon(0).getToken() != null) {
                                        t.getSon(0).setExpType(establecerExpType(t.getSon(0).getToken()));
                                    }
                                    asignarTipos(t.getSon(0));
                                }
                                if (t.getSon(1) != null) {
                                    if (t.getSon(1).getToken() != null) {
                                        t.getSon(1).setExpType(establecerExpType(t.getSon(1).getToken()));
                                    }
                                    asignarTipos(t.getSon(1));
                                }
                                break;
                            case "ConK":
                                if (t.getSon(0) != null) {
                                    if (t.getSon(0).getToken() != null) {
                                        t.getSon(0).setExpType(establecerExpType(t.getSon(0).getToken()));
                                    }
                                    asignarTipos(t.getSon(0));
                                }
                                if (t.getSon(1) != null) {
                                    if (t.getSon(1).getToken() != null) {
                                        t.getSon(1).setExpType(establecerExpType(t.getSon(1).getToken()));
                                    }
                                    asignarTipos(t.getSon(1));
                                }
                                break;
                            case "FalseK":
                                if (t.getSon(0) != null) {
                                    if (t.getSon(0).getToken() != null) {
                                        t.getSon(0).setExpType(establecerExpType(t.getSon(0).getToken()));
                                    }
                                    asignarTipos(t.getSon(0));
                                }
                                if (t.getSon(1) != null) {
                                    if (t.getSon(1).getToken() != null) {
                                        t.getSon(1).setExpType(establecerExpType(t.getSon(1).getToken()));
                                    }
                                    asignarTipos(t.getSon(1));
                                }
                                if (t.getSon(2) != null) {
                                    if (t.getSon(2).getToken() != null) {
                                        t.getSon(2).setExpType(establecerExpType(t.getSon(2).getToken()));
                                    }
                                    asignarTipos(t.getSon(2));
                                }
                                break;
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
        return t;
    }

    public String establecerExpType(Token tkn) {
        String type = st.st_contains(tkn);
        if (type != null) {
            return type;
        }
        return "Void";
    }

    public Nodos asignarValor(Nodos t) {

        if (t != null) {
            if (t.getNodeType() != null) {
                switch (t.getNodeType()) {
                    case "StmtK": {
                        String sT = (String) t.getType();
                        switch (sT) {
                            case "MainK":
                                if (t.getSon(0) != null) {
                                    asignarValor(t.getSon(0));
                                }
                                break;
                            case "DecIntK":
                                if (t.getHermano() != null) {
                                    asignarValor(t.getHermano());
                                }
                                break;
                            case "DecFloatK":
                                if (t.getHermano() != null) {
                                    asignarValor(t.getHermano());
                                }
                                break;
                            case "DecBoolK":
                                if (t.getHermano() != null) {
                                    asignarValor(t.getHermano());
                                }
                                break;
                            case "IntK":
//                                t.setExpType(ExpType.Integer);
//                                st.st_insert(t.getToken(), t.getExpType());
//                                if (t.getBro() != null) {
//                                    asignarTipos(t.getBro());
//                                }
//                                break;
                            case "FloatK":
//                                t.setExpType(ExpType.Real);
//                                st.st_insert(t.getToken(), t.getExpType());
//                                if (t.getBro() != null) {
//                                    asignarTipos(t.getBro());
//                                }
//                                break;
                            case "BoolK":
//                                t.setExpType(ExpType.Boolean);
//                                st.st_insert(t.getToken(), t.getExpType());
//                                if (t.getBro() != null) {
//                                    asignarTipos(t.getBro());
//                                }
                                break;
//                            case VoidK:
//                                t.setExpType(ExpType.Void);
//                                AgregarNodo(t.getToken().getLexema(), t.getExpType());
//                                if (t.getBro() != null) {
//                                    asignarTipos(t.getBro());
//                                }
//                                break;
                            case "IfK":
                                if (t.getSon(0) != null) {
                                    asignarValor(t.getSon(0));
                                }
                                if (t.getSon(1) != null) {
                                    asignarValor(t.getSon(1));
                                }
                                if (t.getSon(2) != null) {
                                    asignarValor(t.getSon(2));
                                }
                                if (t.getHermano() != null) {
                                    asignarValor(t.getHermano());
                                }
                                break;
                            case "WhileK":
                            case "DoK":
                                if (t.getSon(0) != null) {
                                    asignarValor(t.getSon(0));
                                }
                                if (t.getSon(1) != null) {
                                    asignarValor(t.getSon(1));
                                }
                                if (t.getHermano() != null) {
                                    asignarValor(t.getHermano());
                                }
                                break;
                            case "AssigmK": {
                                if (t.getSon(0) != null) {
                                    Nodos tmp = asignarValor(t.getSon(0));
                                    try {
                                        double val = Double.parseDouble(tmp.getValor());
                                        if (t.getExpType() != null) {
                                            if (t.getExpType().equals("Boolean")) {
//                                                if (tmp.getExpType().equals(t.getExpType())) {
                                                    if ((val % 1) == 0 && !tmp.getExpType().equals("Real")) {
                                                    if (val == 0 || val == 1) {
                                                        String[] str = tmp.getValor().split("\\.");
                                                        t.setValor(str[0]);
                                                        if (save) {
                                                            st.st_setVal(t.getToken(), t.getValor());
                                                        }
                                                    } else {
                                                        t.setValor("ERROR");
                                                        typeError(t, " valor boolean diferente de 0 / 1");
                                                    }
                                                } else {
                                                    t.setValor("ERROR");
                                                    typeError(t, " valor float a boolean");
                                                }
//                                                } else {
//                                                    t.setValue("ERROR");
//                                                    typeError(t, " Error de Asignacion");
//                                                }
                                            } else if (t.getExpType().equals("Integer")) {
                                                if (tmp.getExpType().equals(t.getExpType())) {
                                                    if ((val % 1) == 0) {
                                                        String[] str = tmp.getValor().split("\\.");
                                                        t.setValor(str[0]);
                                                        if (save) {
                                                            st.st_setVal(t.getToken(), t.getValor());
                                                        }
                                                    } else {
                                                        t.setValor("ERROR");
                                                        typeError(t, " valor float a int");
                                                    }
                                                } else {
                                                    t.setValor("ERROR");
                                                    typeError(t, " Error de Asignacion");
                                                }
                                            } else if (t.getExpType().equals("Real")) {
//                                                if (tmp.getExpType().equals(t.getExpType())) {
                                                t.setValor(tmp.getValor());
                                                if (save) {
                                                    st.st_setVal(t.getToken(), t.getValor());
                                                }
//                                                } else {
//                                                    t.setValue("ERROR");
//                                                    typeError(t, " Error de Asignacion");
//                                                }
                                            } else {
                                                t.setValor("ERROR");
                                                typeError(t, " Error de Asignacion");
                                            }
                                        }
                                    } catch (NumberFormatException ex) {
                                        t.setValor("ERROR");
                                        typeError(t, "Error de Asignacion");
                                    }

//                                    if (tmp.getExpType().equals(t.getExpType())) {
//                                        t.setValue(tmp.getValue());
//                                    } else {
//                                        t.setValue("ERROR");
//                                        typeError(t, "Asignacion de Tipos Diferentes");
//                                    }
                                }
                                if (t.getSon(1) != null) {
                                    //t.getSon(1).setExpType(establecerExpType(t.getSon(1).getToken()));
                                    t.setValor(asignarValor(t.getSon(1)).getValor().toString());
                                }
                                if (t.getHermano() != null) {
                                    asignarValor(t.getHermano());
                                }
                                break;
                            }
                            case "WriteK":
                                if (t.getSon(0) != null) {
                                    //t.getSon(0).setExpType(establecerExpType(t.getSon(0).getToken()));
                                    Nodos tmp = asignarValor(t.getSon(0));
                                    t.setExpType(tmp.getExpType());
                                    t.setValor(tmp.getValor());
                                }
                                if (t.getHermano() != null) {
                                    asignarValor(t.getHermano());
                                }
                                break;
                            case "ReadK":
//                                t.setExpType(establecerExpType(t.getToken()));
//                                st.st_insert(t.getToken(), t.getExpType());
                                if (t.getHermano() != null) {
                                    asignarValor(t.getHermano());
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    }

                    case "ExpK": {
                        String E = (String) t.getType();
                        switch (E) {
                            case "OpK": {
                                try {
                                    double lSon = 0;
                                    double rSon = 0;
                                    String val;
                                    String exp0 = "Integer";
                                    String exp1 = "Integer";

                                    if (t.getSon(0) != null) {
                                        val = asignarValor(t.getSon(0)).getValor();
//                                        System.out.println(" Val = " + val);
                                        lSon = Double.valueOf(val);
                                        exp0 = t.getSon(0).getExpType();
                                    }
                                    if (t.getSon(1) != null) {
                                        val = asignarValor(t.getSon(1)).getValor();
//                                        System.out.println(" Val =  " + val);
                                        rSon = Double.valueOf(val);
                                        exp1 = t.getSon(1).getExpType();
                                    }

                                    if (exp0 == exp1) {
                                        t.setExpType(exp0);
                                    }

                                    switch (t.getToken().getType()) {
                                        case "TKN_PLUS":
                                            double res = ((lSon + rSon) * 100) / 100;
//                                            System.out.println(" res = " + res);
                                            t.setValor(String.valueOf(res));
//                                            System.out.println(" -> " + t.getToken().getLexema());
                                            break;
                                        case "TKN_MINUS":
                                            res = lSon - rSon;
//                                            System.out.println(" res = " + res);
                                            t.setValor(String.valueOf(res));
//                                            System.out.println(" -> " + t.getToken().getLexema());
                                            break;
                                        case "TKN_TIMES":
                                            res = lSon * rSon;
//                                            System.out.println(" res = " + res);
                                            t.setValor(String.valueOf(res));
//                                            System.out.println(" -> " + t.getToken().getLexema());
                                            break;
                                        case "TKN_OVER":
                                            res = lSon / rSon;
//                                            System.out.println(" res = " + res);
                                            t.setValor(String.valueOf(res));
//                                            System.out.println(" -> " + t.getToken().getLexema());
                                            break;
                                        case "TKN_COMPAR":
                                            if (lSon == rSon) {
                                                condicion = true;
                                                t.getToken().setValor("TRUE");
                                            } else {
                                                condicion = false;
                                                t.getToken().setValor("FALSE");
                                            }
                                            break;
                                        case "TKN_NOTEQUAL":
                                            if (lSon != rSon) {
                                                condicion = true;
                                                t.getToken().setValor("TRUE");
                                            } else {
                                                condicion = false;
                                                t.getToken().setValor("FALSE");
                                            }
                                            break;
                                        case "TKN_LT":
                                            if (lSon < rSon) {
                                                condicion = true;
                                                t.getToken().setValor("TRUE");
                                            } else {
                                                condicion = false;
                                                t.getToken().setValor("FALSE");
                                            }
                                            break;
                                        case "TKN_LTE":
                                            if (lSon <= rSon) {
                                                condicion = true;
                                                t.getToken().setValor("TRUE");
                                            } else {
                                                condicion = false;
                                                t.getToken().setValor("FALSE");
                                            }
                                            break;
                                        case "TKN_GT":
                                            if (lSon > rSon) {
                                                condicion = true;
                                                t.getToken().setValor("TRUE");
                                            } else {
                                                condicion = false;
                                                t.getToken().setValor("FALSE");
                                            }
                                            break;
                                        case "TKN_GTE":
                                            if (lSon >= rSon) {
                                                condicion = true;
                                                t.getToken().setValor("TRUE");
                                            } else {
                                                condicion = false;
                                                t.getToken().setValor("FALSE");
                                            }
                                            break;

                                    }
//                                if (t.getSon(0) != null) {
//                                    t.getSon(0).setExpType(establecerExpType(t.getSon(0).getToken()));
//                                    asignarValor(t.getSon(0));
//                                }
//                                if (t.getSon(1) != null) {
//                                    t.getSon(1).setExpType(establecerExpType(t.getSon(1).getToken()));
//                                    asignarValor(t.getSon(1));
//                                }
                                    if (exp0.equals("Boolean") || exp1.equals("Boolean")) {
                                        t.setExpType("Boolean");
                                    } else if (exp0.equals("Real") || exp1.equals("Real")) {
                                        t.setExpType("Real");
                                    } else {
                                        t.setExpType("Integer");
                                    }

                                } catch (NumberFormatException ex) {
                                    t.setValor("ERROR");
                                }
                            }

                            save = true;
                            if (t.getHermano() != null) {
                                asignarValor(t.getHermano());
                            }
                            break;
                            case "ConstK":
                                t.setValor(t.getToken().getLexema());
                                //t.setExpType(establecerExpType(t.getToken().getLexema()));
                                if (t.getHermano() != null) {
                                    asignarValor(t.getHermano());
                                }
                                break;
                            case "IdK":
                                t.getToken().setValor(st.st_getVal(t.getToken()));
                                t.setValor(t.getToken().getValor());
                                if (t.getHermano() != null) {
                                    asignarValor(t.getHermano());
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    }

                    case "StructK": {
                        String St = (String) t.getType();
                        switch (St) {
                            case "TrueK":
                                if (condicion == true) {
                                    save = true;
                                } else {
                                    save = false;
                                }
                                if (t.getSon(0) != null) {
                                    asignarValor(t.getSon(0));
                                }
                                if (t.getSon(1) != null) {
                                    asignarValor(t.getSon(1));
                                }
                                break;
                            case "ConK":
                                if (t.getSon(0) != null) {
                                    asignarValor(t.getSon(0));
                                }
                                if (t.getSon(1) != null) {
                                    asignarValor(t.getSon(1));
                                }
                                break;
                            case "FalseK":
                                if (condicion == false) {
                                    save = true;
                                } else {
                                    save = false;
                                }
                                if (t.getSon(0) != null) {
                                    asignarValor(t.getSon(0));
                                }
                                if (t.getSon(1) != null) {
                                    asignarValor(t.getSon(1));
                                }
                                if (t.getSon(2) != null) {
                                    asignarValor(t.getSon(2));
                                }
                                break;
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
        return t;
    }
}
