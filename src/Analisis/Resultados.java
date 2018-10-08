package Analisis;

import Estructura.Token;
import Estructura.Nodos;
import javax.swing.tree.*;
import java.util.*;
import java.util.Iterator;

public class Resultados {

    //private List<String> Code = new ArrayList<String>();
    private List<String> Error = new ArrayList<String>();
    private List<String> Lexico = new ArrayList<String>();
    private List<Token> Tokens = new ArrayList<Token>();
    //
    private DefaultTreeModel TreeModel;
    private DefaultMutableTreeNode Root;

    public Resultados(List<Token> Tokens, List<String> Lexico, List<String> Error) {
        //this.Code = null;
        this.Error = Error;
        this.Lexico = Lexico;
        this.Tokens = Tokens;
    }

    private Token getToken(int indice) {
        return (Token) this.Tokens.get(indice);
    }

    // Errores Obtenidos en Todo el Analisis
    public String Display_Errores() {
        String E = "";
        Iterator iter = Error.iterator();
        while (iter.hasNext()) {
            E = E + "\n" + iter.next();
        }
        return E;
    }

    // Resultado de Analisis Lexico en texto
    public String Display_Lexico() {
        String L = "";
        Iterator iter = Lexico.iterator();
        while (iter.hasNext()) {
            L = L + "\n" + iter.next();
        }
        return " " + L;
    }

    public void Remove_Tokens() {
        Token token = new Token();
        for (int i = 0; i < Tokens.size(); i++) {
            token = getToken(i);
            if (token.getCat().equals("TKN_ERROR") || token.getCat().equals("TKN_COMMENT")) {
                Tokens.remove(i--);
            }
        }
    }

    public boolean Error_Lexico() {
        Token token = new Token();
        for (int i = 0; i < Tokens.size(); i++) {
            token = getToken(i);
            if (token.getCat().equals("TKN_ERROR")) {
                return false;
            }
        }
        Error.add("  ");
        return true;
    }

    // Arbol Sintactico
    public DefaultTreeModel Display_Sintactico(Nodos node) {
        Root = new DefaultMutableTreeNode("Arbol Sintactico");
        TreeModel = new DefaultTreeModel(Root);
        TreeSin(node, Root, 0);
        return TreeModel;
    }

    // Recorre el Arbol Sintactico
    public void TreeSin(Nodos node, DefaultMutableTreeNode Padre, int num_Pos) {
        if (node != null) {
            DefaultMutableTreeNode Hijo = new DefaultMutableTreeNode(treeTextSin(node));
            TreeModel.insertNodeInto(Hijo, Padre, num_Pos);
            TreeSin(node.getSon(0), Hijo, 0);
            TreeSin(node.getSon(1), Hijo, 1);
            TreeSin(node.getSon(2), Hijo, 2);
            //TreeSin(node.getSon(3), Hijo, 3);
            TreeSin(node.getHermano(), Padre, ++num_Pos);
        }
    }

    // Asigna etiquetas para mostrar en JTree sintactico
    private String treeTextSin(Nodos node) {
        String S = " ";
        if (node.getNodeType() != null) {
            switch (node.getNodeType()) {
                case "StmtK": {
                    String T = (String) node.getType();
                    String texto = "";
                    switch (T) {

                        case "DecIntK": {
                            texto = "Lista Declaraciones INT";
                            S = (S + texto);
                            break;
                        }
                        case "DecFloatK": {
                            texto = "Lista Declaraciones FLOAT";
                            S = (S + texto);
                            break;
                        }
                        case "DecBoolK": {
                            texto = "Lista Declaraciones BOOL";
                            S = (S + texto);
                            break;
                        }
                        case "IfK": {
                            texto = "If";
                            S = (S + texto);
                            break;
                        }
                        case "UntilK": {
                            texto = "Until";
                            S = (S + texto);
                            break;
                        }
                        case "MainK": {
                            texto = "Inicio Programa";
                            S = (S + texto);
                            break;
                        }
                        case "ForK": {
                            texto = "For";
                            S = (S + texto);
                            break;
                        }
                        case "WhileK": {
                            texto = "While";
                            S = (S + texto);
                            break;
                        }
                        case "DoK": {
                            texto = "Do";
                            S = (S + texto);
                            break;
                        }
                        case "WriteK": {
                            texto = "Write";
                            S = (S + texto);
                            break;
                        }
                        case "AssigmK": {
                            S = (S + "Asignacion a" + ":  " + node.getToken().getNombre());
                            break;
                        }
                        //case StringK:
                        case "IntK": {
                            texto = "Variable INT";
                            S = (S + texto + ":  " + node.getToken().getLexema());
                            break;
                        }
                        case "FloatK":
                            texto = "Variable FLOAT";
                             {
                                S = (S + texto + ":  " + node.getToken().getLexema());
                                break;
                            }
                        case "BoolK": {
                            texto = "Variable BOOL";
                            S = (S + texto + ":  " + node.getToken().getLexema());
                            break;
                        }
                        //case VoidK://
                        case "ReadK": {
                            texto = "Read";
                            S = (S + texto + ":  " + node.getToken().getLexema());
                            break;
                        }
                        default: {
                            S = "ERROR";
                            break;
                        }
                    }
                    break;
                }
                case "ExpK": {
                    String E = (String) node.getType();
                    String texto = "";

                    switch (E) {
                        case "OpK":
                            texto = "operador";
                            break;
                        case "ConstK":
                            texto = "Constante";
                            break;
                        case "IdK":
                            texto = "Identificador";
                            break;
                    }

                    S = (S + texto + ":  " + node.getToken().getLexema());
                    break;
                }
                case "StructK": {
                    String St = (String) node.getType();

                    String texto = "";

                    switch (St) {
                        case "ConK":
                            texto = "Condicion";
                            break;
                        case "IncK":
                            texto = "Incremento";
                            break;
                        case "IniK":
                            texto = "Inicializacion";
                            break;
                        case "TrueK":
                            texto = "caso verdadero";
                            break;
                        case "FalseK":
                            texto = "caso falso";
                            break;
                        case "MainK":
                            texto = "inicio del programa";
                            break;
                    }

                    S = (S + texto);
                    break;
                }
                default: {
                    S = S + "ERROR";
                    break;
                }
            }
        }
        return S;
    }

    // Arbol Semantico (Arbol Sintactico con Anotaciones)
    public DefaultTreeModel Display_Semantico(Nodos node) {
        Root = new DefaultMutableTreeNode("Arbol Semantico");
        TreeModel = new DefaultTreeModel(Root);
        TreeSem(node, Root, 0);
        return TreeModel;
    }

    // Recorre el Arbol Semantico
    public void TreeSem(Nodos node, DefaultMutableTreeNode Padre, int num_Pos) {
        if (node != null) {
            DefaultMutableTreeNode Hijo = new DefaultMutableTreeNode(treeTextSem(node));
            TreeModel.insertNodeInto(Hijo, Padre, num_Pos);
            TreeSem(node.getSon(0), Hijo, 0);
            TreeSem(node.getSon(1), Hijo, 1);
            TreeSem(node.getSon(2), Hijo, 2);
            //TreeSem(node.getSon(3), Hijo, 3);
            TreeSem(node.getHermano(), Padre, ++num_Pos);
        }
    }

    // Asigna etiquetas para mostrar en JTree semantico
    private String treeTextSem(Nodos node) {
        String S = " ";
        if (node.getNodeType() != null) {
            switch (node.getNodeType()) {
                case "StmtK": {
                    String T = (String) node.getType();
                    String texto = "";
                    switch (T) {
                        case "DecIntK": {
                            texto = "Lista Declaraciones INT";
                            S = (S + texto);
                            break;
                        }
                        case "DecFloatK": {
                            texto = "Lista Declaraciones FLOAT";
                            S = (S + texto);
                            break;
                        }
                        case "DecBoolK": {
                            texto = "Lista Declaraciones BOOL";
                            S = (S + texto);
                            break;
                        }
                        case "IfK": {
                            texto = "If";
                            S = (S + texto);
                            break;
                        }
                        case "UntilK": {
                            texto = "Until";
                            S = (S + texto);
                            break;
                        }
                        case "MainK": {
                            texto = "Inicio Programa";
                            S = (S + texto);
                            break;
                        }
                        case "ForK": {
                            texto = "For";
                            S = (S + texto);
                            break;
                        }
                        case "WhileK": {
                            texto = "While";
                            S = (S + texto);
                            break;
                        }
                        case "DoK": //case CoutK:
                        {
                            texto = "Do";
                            S = (S + texto);
                            break;
                        }
                        case "AssigmK": {
                            S = (S + "Asignacion a" + ":  " + node.getToken().getLexema());
                            if (node.getExpType() != null) {
                                S = S + "   Tipo: " + node.getExpType().toString() + "   Valor: " + node.getValor();
                            }
                            break;
                        }
                        //case StringK:

                        case "BoolK": {
                            S = (S + "Variable BOOL" + ":  " + node.getToken().getLexema());
                            if (node.getExpType() != null) {
                                S = S + "   Tipo: " + node.getExpType().toString();
                            }
                            if (node.getToken().getValor() != null) {
                                if (node.getToken().getValor().equals("0")) {
                                    node.getToken().setValor("FALSE");
                                    S = S + "   Valor: " + node.getToken().getValor();
                                } else {
                                    node.getToken().setValor("TRUE");
                                    S = S + "   Valor: " + node.getToken().getValor();
                                }
                            }
                            break;
                        }

                        case "IntK": {
                            texto = "Variable INT";
                            S = (S + texto + ":  " + node.getToken().getLexema());
                            if (node.getExpType() != null) {
                                S = S + "   Tipo: " + node.getExpType().toString();
                            }
                            if (node.getToken().getValor() != null) {
                                S = S + "   Valor: " + node.getToken().getValor();
                            } else {
                                node.getToken().setValor("0");
                                S = S + "   Valor: " + node.getToken().getValor();
                            }
                            break;
                        }

                        case "FloatK": {
                            texto = "Variable FLOAT";
                            S = (S + texto + ":  " + node.getToken().getLexema());
                            if (node.getExpType() != null) {
                                S = S + "   Tipo: " + node.getExpType().toString();
                            }
                            if (node.getToken().getValor() != null) {
                                S = S + "   Valor: " + node.getToken().getValor();
                            } else {
                                node.getToken().setValor("0");
                                S = S + "   Valor: " + node.getToken().getValor();
                            }
                            break;
                        }

                        //case VoidK://
                        case "ReadK": {
                            texto = "Read";
                            S = (S + texto + ":  " + node.getToken().getLexema());
                            if (node.getExpType() != null) {
                                S = S + "   Tipo: " + node.getExpType().toString();
                            }
                            if (node.getToken().getValor() != null) {
                                if (node.getToken().getValor().equals("0")) {
                                } else {
                                    S = S + "   Valor: " + node.getToken().getValor();
                                }
                            }
                            break;
                        }
                        case "WriteK": {
                            texto = "Write";
                            S = (S + texto);
//                            if (node.getExpType() != null) {
//                                S = S + "   Tipo: " + node.getExpType().toString();
//                            }
                            if (node.getValor() != null) {
                                S = S + "   Valor: " + node.getValor();
                            }
                            break;
                        }
                        default: {
                            S = "ERROR";
                            break;
                        }
                    }
                    break;
                }
                case "ExpK": {
                    String E = (String) node.getType();
                    String texto = "";
                    switch (E) {
                        case "OpK":
                            texto = "operador";
                            break;
                        case "ConstK":
                            texto = "Constante";
                            break;
                        case "IdK":
                            texto = "Identificador";
                            break;
                    }
                    S = (S + texto + ":  " + node.getToken().getLexema());
                    if (node.getToken().getLexema().equals("+") || node.getToken().getLexema().equals("-") || node.getToken().getLexema().equals("*") || node.getToken().getLexema().equals("/")) {
                        S = (S + "   Valor: " + node.getValor());
                    } else {
                        if (node.getToken().getValor() != null) {
                            if (node.getExpType() != null) {
                                S = (S + "   Tipo: " + node.getExpType().toString() + "   Valor: " + node.getToken().getValor());
                            }
                        } else {
                            if (node.getExpType() != null) {
                                S = (S + "   Tipo: " + node.getExpType().toString());
                            }
                        }
                    }
                    break;
                }
                case "StructK": {
                    String St = (String) node.getType();
                    String texto = "";

                    switch (St) {
                        case "ConK":
                            texto = "Condicion";
                            break;
                        case "IncK":
                            texto = "Incremento";
                            break;
                        case "IniK":
                            texto = "Inicializacion";
                            break;
                        case "TrueK":
                            texto = "caso verdadero";
                            break;
                        case "FalseK":
                            texto = "caso falso";
                            break;

                        case "MainK":
                            texto = "inicio del programa";
                            break;
                    }

                    S = (S + texto);
                    break;
                }
                default: {
                    S = S + "ERROR";
                    break;
                }
            }
        }
        return S;
    }

    public List<Token> getTokens() {
        return Tokens;
    }

    public List<String> getErrores() {
        return Error;
    }

    public List<String> getLexico() {
        return Lexico;
    }

    public void setError(List<String> Error) {
        this.Error = Error;
    }

}
