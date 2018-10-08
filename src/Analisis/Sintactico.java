
package Analisis;

import Estructura.Token;
import Estructura.Nodos;
import java.util.*;

public class Sintactico {

    private boolean ERROR;
    private int TKN_Actual;
    private Token token = new Token();
    private List<String> Error = new ArrayList<String>();
    private List<Token> Tokens = new ArrayList<Token>();

    public Sintactico(List<Token> Tokens, List<String> Error) {
        ERROR = false;
        TKN_Actual = 0;
        this.Tokens = Tokens;
        this.Error = Error;
    }

    public List<String> getErrores() {
        return Error;
    }

    public boolean getError() {
        return ERROR;
    }

    public Nodos parse() {
        Nodos Apunta = new Nodos();
        token = getNextTKN();
        if (token.getType() != "TKN_EOF") {
            Apunta = programa();
        }
        match("TKN_EOF");//3
        if (ERROR == false) {
            Error.add(" ");
        }
        return Apunta;
    }

    private Token getNextTKN() {
        if (TKN_Actual == Tokens.size()) {
            TKN_Actual--;
        }
        return (Token) this.Tokens.get(TKN_Actual++);
    }

    private void match(String Expected) {
        if (token.getType().equals(Expected)) {
            token = getNextTKN();
        } else {
            SyntaxError("Token inesperado -> " + token.getLexema() + ", se esperaba: " + Expected);
        }
    }

    private void matchCat(String Expected) {
        if (token.getCat().equals(Expected)) {
            token = getNextTKN();
        } else {
            SyntaxError("Token inesperado -> " + token.getLexema());
        }
    }

    private void SyntaxError(String Msg) {
        ERROR = true;
        Error.add("Error SINTACTICO en la linea: " + token.getLine() + ", columna: " + token.getColumn() + ", Error: " + Msg);
    }

    public Nodos programa() {
        Nodos t = newStmtNode("MainK");
        t.setToken(token);
        match("TKN_PROG");
        match("TKN_LKEY");
        Nodos p = listaDeclaracion();
        if (p != null) {
            t.setSon(p, 0);
        }
        Nodos q = listaSentencias();
        if (q != null) {
            if (p != null) {
                while (p.getHermano() != null) {
                    p = p.getHermano();
                }
                p.setHermano(q);
            } else {
                t.setSon(q, 0);
            }
        }
        match("TKN_RKEY");
        return t;
    }

    
    public boolean vacio() {
        Nodos t = new Nodos();
        if (token.getType().equals("TKN_INT")
                || token.getType().equals("TKN_FLOAT")
                || token.getType().equals("TKN_BOOL")) {
            return false;
        }
        return true;
    }

    
    public Nodos listaDeclaracion() {
        Nodos t = null;
        Nodos p = t;
        while (!vacio()) {
            Nodos q = new Nodos();
            q = declaracion();
            if (q != null) {
                if (t == null) {
                    t = p = q;
                } else {
                    Nodos aux = p;
                    while (aux.getHermano() != null) {
                        aux = aux.getHermano();
                    }
                    aux.setHermano(q);
                    p = aux;
                }
            }
        }
        return t;
    }

    
    public Nodos declaracion() {
        Nodos t = listaVariables();
        return t;
    }

    
    public Nodos listaVariables() {
        String stmt = null;

        Nodos NodeDec = new Nodos();
        Token tAux = new Token();

        Nodos t = new Nodos();
        switch (token.getType()) {
            case "TKN_INT":
                t = newStmtNode(stmt = "IntK");
                NodeDec = newStmtNode("DecIntK");
                tAux.setLexema("Declaracion Enteros");
                tAux.setType("TKN_DECINT");
                NodeDec.setToken(tAux);
                match("TKN_INT");
                break;
            case "TKN_FLOAT":
                t = newStmtNode(stmt = "FloatK");
                NodeDec = newStmtNode("DecFloatK");
                tAux.setLexema("Declaracion Flotantes");
                tAux.setType("TKN_DECFLOAT");
                NodeDec.setToken(tAux);
                match("TKN_FLOAT");
                break;
            case "TKN_BOOL":
                t = newStmtNode(stmt = "BoolK");
                NodeDec = newStmtNode("DecBoolK");
                tAux.setLexema("Declaracion Boleanos");
                tAux.setType("TKN_DECBOOL");
                NodeDec.setToken(tAux);
                match("TKN_BOOL");
                break;
            default:
                SyntaxError("ERROR SINTACTICO Token inesperado -> " + token.getLexema());
                token = getNextTKN();
                break;
        }



        t.setToken(token);
        match("TKN_ID");
        while (token.getType().equals("TKN_COMA")) {
            Nodos bro = newStmtNode(stmt);
            match("TKN_COMA");
            bro.setToken(token);
            if (token.getType().equals("TKN_ID")) {
                match("TKN_ID");
                bro.setHermano(t);
                t = bro;
            }
        }
        NodeDec.setSon(t, 0);
        match("TKN_SEMI");
        return NodeDec;
    }

    
    public Nodos listaSentencias() {
        Nodos NodeT = sentencia();
        Nodos NodeP = NodeT;
        while (!(token.getType().equals("TKN_EOF")) && !(token.getType().equals("TKN_ELSE"))
                && !(token.getType().equals("TKN_RKEY")) && !(token.getType().equals("TKN_UNTIL"))
                && !(token.getType().equals("TKN_END")) && !(token.getType().equals("TKN_FI"))) {
            Nodos NodeQ = new Nodos();
            NodeQ = sentencia();
            if (NodeQ != null) {
                if (NodeT == null) {
                    NodeT = NodeP = NodeQ;
                } else {
                    NodeP.setHermano(NodeQ);
                    NodeP = NodeQ;
                }
            }
        }
        return NodeT;
    }

    
    public Nodos sentencia() {
        Nodos NodeT = new Nodos();
        switch (token.getType()) {
            case "TKN_IF": {
                NodeT = sentSeleccion();
                break;
            }
            case "TKN_WHILE": {
                NodeT = sentIteracion();
                break;
            }
            case "TKN_DO": {
                NodeT = sentRepeticion();
                break;
            }
            case "TKN_READ": {
                NodeT = sentRead();
                break;
            }
            case "TKN_WRITE": {
                NodeT = sentWrite();
                break;
            }
            case "TKN_LKEY": {
                NodeT = sentBloque();
                break;
            }
            case "TKN_SEMI":
                match("TKN_SEMI");
                break;
            case "TKN_REAL":
            case "TKN_INTEGER":
            case "TKN_ID": {
                NodeT = sentExpresion();
                break;
            }
            default: {
                //syntaxError("Token inesperado -> " + token.getLexema());
                token = getNextTKN();
                NodeT = null;
            }
        }
        return NodeT;
    }

    // IF-ELSE-FI
    public Nodos sentSeleccion() {
        Nodos NodeT = newStmtNode("IfK");
        match("TKN_IF");
        NodeT.setSon(newStructNode("ConK"), 0);
        NodeT.getSon(0).setSon(expresion(), 0);
        NodeT.setSon(newStructNode("TrueK"), 1);
        NodeT.getSon(1).setSon(listaSentencias(), 0);

        if (token.getType().equals("TKN_ELSE")) {
            match("TKN_ELSE");
            NodeT.setSon(newStructNode("FalseK"), 2);
            NodeT.getSon(2).setSon(listaSentencias(), 0);
        }
        match("TKN_FI");
        return NodeT;
    }

    // WHILE
    public Nodos sentIteracion() {
        Nodos NodeT = newStmtNode("WhileK");
        match("TKN_WHILE");
        match("TKN_LPAREN");
        NodeT.setSon(newStructNode("ConK"), 0);
        NodeT.getSon(0).setSon(expresion(), 0);
        match("TKN_RPAREN");
        //match(TokenType.TKN_LKEY);
        NodeT.setSon(newStructNode("TrueK"), 1);
//        NodeT.getSon(1).setSon(stmt_sequence(), 0);
        NodeT.getSon(1).setSon(sentencia(), 0);
        //match(TokenType.TKN_RKEY);
        return NodeT;
    }

    // DO-UNTIL
    public Nodos sentRepeticion() {
        Nodos NodeT = newStmtNode("DoK");
        match("TKN_DO");
        NodeT.setSon(newStructNode("TrueK"), 0);
        NodeT.getSon(0).setSon(listaSentencias(), 0);
        match("TKN_UNTIL");
        match("TKN_LPAREN");
        NodeT.setSon(newStructNode("ConK"), 1);
        NodeT.getSon(1).setSon(expresion(), 0);
        match("TKN_RPAREN");
        match("TKN_SEMI");
        return NodeT;
    }

    
    public Nodos sentRead() {
        Nodos NodeT = newStmtNode("ReadK");
        match("TKN_READ");
        if (token.getType().equals("TKN_ID")) {
            NodeT.setToken(token);
        }
        match("TKN_ID");
        match("TKN_SEMI");
        return NodeT;
    }

    
    public Nodos sentWrite() {
        Nodos NodeT = newStmtNode("WriteK");
        if (token.getType().equals("TKN_WRITE")) {
            NodeT.setToken(token);
        }
        match("TKN_WRITE");
        NodeT.setSon(expresionSimple(), 0);
        match("TKN_SEMI");
        return NodeT;
    }

     // {-}
    public Nodos sentBloque() {
        match("TKN_LKEY");
        Nodos t = listaSentencias();
        match("TKN_RKEY");
        return t;
    }

    
    public Nodos sentExpresion() {
        Nodos t = expresion();
        match("TKN_SEMI");
        return t;
    }
    
    
    public Nodos expresion() {
        Nodos NodeT = expresionSimple();
        if (token.getType().equals("TKN_EQUAL")) {
            Nodos NodeP = newStmtNode("AssigmK");
            Token tAux = NodeT.getToken();
            tAux.setLexema(NodeT.getToken().getLexema());
            tAux.setType("TKN_ASSIGMENT");
            NodeP.setToken(tAux);
            NodeT = NodeP;
            match("TKN_EQUAL");
            NodeP.setSon(expresion(), 0);
        }
        return NodeT;
    }

    
    public Nodos expresionSimple() {
        Nodos NodeT = expresionOperador();
        if ((token.getType().equals("TKN_COMPAR"))
                || (token.getType().equals("TKN_NOTEQUAL"))
                || (token.getType().equals("TKN_LT"))
                || (token.getType().equals("TKN_LTE"))
                || (token.getType().equals("TKN_GT"))
                || (token.getType().equals("TKN_GTE"))) {
            Nodos NodeP = newExpNode("OpK");
            NodeP.setSon(NodeT, 0);
            NodeP.setToken(token);
            NodeT = NodeP;
            match(token.getType());
            NodeP.setSon(expresionSimple(), 1);
        }
        return NodeT;
    }

    
    public Nodos expresionOperador() {
        Nodos NodeT = termino();
        while ((token.getType().equals("TKN_PLUS")) || (token.getType().equals("TKN_MINUS"))) {
            Nodos NodeP = newExpNode("OpK");
            NodeP.setSon(NodeT, 0);
            NodeP.setToken(token);
            NodeT = NodeP;
            match(token.getType());
            NodeP.setSon(termino(), 1);
        }
        if(token.getType().equals("TKN_PLUSP")){
        //System.out.println("aqir entre"); // FALTA AGREGAR AL ARBOL ###################################
            Nodos asig=newStmtNode("AssigmK");
            Token b=new Token();
            b.setLexema("=");
            b.setType("TKN_ASSIG");
            asig.setToken(NodeT.getToken());
            Nodos NodeP = newExpNode("OpK");
            //TreeNode First =NodeT;
            NodeP.setSon(NodeT, 0);
            token.setLexema("+");
            NodeP.setToken(token);
            NodeT = NodeP;
            NodeP = newExpNode("ConstK");
            Token a=new Token();
            a.setLexema("1");
            a.setValor("1");
            a.setType("TKN_CONSTANT");
            NodeP.setToken(a);
            //match(token.getType());
            NodeT.setSon(NodeP, 1);
            //asig.setSon(First, 0);
            asig.setSon(NodeT, 0);
            NodeT=asig;
            token = getNextTKN();
        }
        if(token.getType().equals("TKN_MINUSP")){
        //System.out.println("aqir entre -"); // FALTA AGREGAR AL ARBOL ###################################
            Nodos asig=newStmtNode("AssigmK");
            Token b=new Token();
            b.setLexema("=");
            b.setType("TKN_ASSIG");
            asig.setToken(NodeT.getToken());
            Nodos NodeP = newExpNode("OpK");
            //TreeNode First =NodeT;
            NodeP.setSon(NodeT, 0);
            token.setLexema("-");
            NodeP.setToken(token);
            NodeT = NodeP;
            NodeP = newExpNode("ConstK");
            Token a=new Token();
            a.setLexema("1");
            a.setValor("1");
            a.setType("TKN_CONSTANT");
            NodeP.setToken(a);
            //match(token.getType());
            NodeT.setSon(NodeP, 1);
            //asig.setSon(First, 0);
            asig.setSon(NodeT, 0);
            NodeT=asig;
            token = getNextTKN();
        }
        return NodeT;
    }

    
    public Nodos termino() {
        Nodos NodeT = factor();
        while ((token.getType().equals("TKN_TIMES")) || (token.getType().equals("TKN_OVER"))) {
            Nodos NodeP = newExpNode("pK");
            NodeP.setSon(NodeT, 0);
            NodeP.setToken(token);
            NodeT = NodeP;
            match(token.getType());
            NodeP.setSon(factor(), 1);
        }
        return NodeT;
    }

    
    public Nodos factor() {
        Nodos NodeT = new Nodos();
        switch (token.getType()) {
            case "TKN_INTEGER":
            case "TKN_REAL":
            //case TKN_FLOAT:
            case "TKN_TEXT": {
                NodeT = newExpNode("ConstK");
                NodeT.setToken(token);
                matchCat(token.getCat());
                break;
            }
            case "TKN_ID": {
                NodeT = newExpNode("IdK");
                NodeT.setToken(token);
                match(token.getType());
                break;
            }
            case "TKN_LPAREN": {
                match("TKN_LPAREN");
                NodeT = expresion();
                match("TKN_RPAREN");
                break;
            }
            case "TKN_PLUS":
            case "TKN_MINUS":
                Token tknAux = new Token();
                tknAux.setLine(token.getLine());
                tknAux.setColumn(token.getColumn() - 1);
                tknAux.setLexema("0");
                tknAux.setType("TKN_INTEGER");

                NodeT = newExpNode("ConstK");
                NodeT.setToken(tknAux);

                Nodos NodeP = newExpNode("OpK");
                NodeP.setSon(NodeT, 0);
                NodeP.setToken(token);
                NodeT = NodeP;
                match(token.getType());
                NodeP.setSon(termino(), 1);
                break;
            default: {
                SyntaxError(" ERROR SINTACTICO Token inesperado -> " + token.getLexema());
                token = getNextTKN();
            }
        }
        return NodeT;
    }

    private Nodos newStmtNode(String Kind) {
        //System.out.println("Kind: " + Kind);
        Nodos Node = new Nodos();
        Node.setHermano(null);
        Node.setTypeNode("StmtK");
        Node.setType(Kind);
        return Node;
    }

    private Nodos newExpNode(String Kind) {
        Nodos node = new Nodos();
        node.setHermano(null);
        node.setTypeNode("ExpK");
        node.setType(Kind);
        return node;
    }

    private Nodos newStructNode(String Kind) {
        Nodos node = new Nodos();
        node.setHermano(null);
        node.setTypeNode("StructK");
        node.setType(Kind);
        return node;
    }
}
