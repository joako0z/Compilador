
package Analisis;

import Estructura.Token;
import Estructura.Nodos;
import Archivo.*;
import Archivo.Archivo.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class TablaDeSimbolos {

    private HashMap hashmap;
    private Nodos semanticTree;
    private Archivo archivo;
    private List<String> logSymTab;
    private int count = 0;

    public TablaDeSimbolos() {
        archivo = new Archivo();
        hashmap = new HashMap();
        logSymTab = new ArrayList<String>();
    }

    public TablaDeSimbolos(Nodos semanticTree) {
        archivo = new Archivo();
        hashmap = new HashMap();
        logSymTab = new ArrayList<String>();
        this.semanticTree = semanticTree;
        buildSymTab(this.semanticTree);
        //printSymTab();
    }

    // Estructura de tabla (tipo variable,locacion en Memoria, lineas ,valor Actual)
    public class Row {

        private String tknType;
        private int location;
        private List<Integer> lines = new ArrayList<Integer>();
        private String val;

        public Row(String type, int loc, List<Integer> line) {
            tknType = type;
            location = loc;
            lines = line;
            val = "0";
        }
    }

    public void setError(String msg) {
        logSymTab.add(msg);
    }

    public Nodos getSymTab() {
        return this.semanticTree;
    }

    // cadena con Errores obtenidos durante la construccion de la tabla
    public String getSymTabLog() {
        String str = "";
        if (logSymTab.size() > 0) {
            str += " \n";
            for (String s : logSymTab) {
                str += s + "\n";
            }
        } else {
            str += " \n";
        }
        return str;
    }

    // Recorre arbol
    public void buildSymTab(Nodos node) {
        if (node != null) {
            buildSymTab(node.getSon(0));
            buildSymTab(node.getSon(1));
            buildSymTab(node.getSon(2));
            checknode(node);
            //TreeSin(node.getSon(3), Hijo, 3);
            buildSymTab(node.getHermano());
        }
    }

    // Verifica que el nodo que llega sea identificador
    private void checknode(Nodos node) {
        Token token = node.getToken();
        if (token != null) {
            if (token.getType().equals("TKN_ID")) {
                st_insert(token, node.getExpType());
            }
        }
    }

    // Regresa la locacion de memoria de la variable
    public int st_lookup(String nombre) {
        if (hashmap.containsKey(nombre)) {
            Row row = (Row) hashmap.get(nombre);
            return row.location;
        } else {
            return -1;
        }

    }

    // Agregar Variable
    public void st_insert(Token token, String type) {
        if (token != null) {
            String newKey = token.getLexema();
            //**checa si esta en la tabla
            if (hashmap.containsKey(newKey)) {
                //System.out.println("\n ->Elemento Existente");
                Row row = (Row) hashmap.get(newKey);
                //**checa token que llega sea del mismo tipo
                if (type != null && row.tknType != null) {
                    if (!type.equals(row.tknType)) {
                        setError("ERROR en Linea: " + token.getLine() + " Columna: " + (token.getColumn() + 1) + " -> Variable: '" + token.getLexema() + "' duplicada");
                    } else {
                        row.lines.add(token.getLine());
                    }
                }
            } else {
                //System.out.println("\n ->Nuevo Elemento Agregado");
                if ((type != null) && (!type.equals("Void"))) {
                    List<Integer> line = new ArrayList<Integer>();
                    line.add(token.getLine());
                    Row newRow = new Row(type, count++, line);
                    hashmap.put(newKey, newRow);
                }
            }
        }
    }

    // Agregar declaracion
    public void st_insertDec(Token token, String type) {
        if (token != null) {
            String newKey = token.getLexema();
            // checa si ya esta declarada
            if (hashmap.containsKey(newKey)) {
                setError("ERROR en Linea: " + token.getLine() + " Columna: " + (token.getColumn() + 1) + " -> Variable: '" + token.getLexema() + "' duplicada");
            } else {
                //System.out.println("\n ->Nuevo Elemento Agregado");
                if ((type != null) && (!type.equals("Void"))) {
                    List<Integer> line = new ArrayList<Integer>();
                    line.add(token.getLine());
                    Row newRow = new Row(type, count++, line);
                    hashmap.put(newKey, newRow);
                }
            }
        }
    }

    // Verifica si la tabla ya contiene la variable y regresa el tipo
    public String st_contains(Token tkn) {
        Row row = (Row) hashmap.get(tkn.getLexema());
        if (row != null) {
            return row.tknType;
        }
        if (tkn.getType().equals("TKN_ID") || tkn.getType().equals("TKN_ASSIGMENT")) {
            setError("ERROR en la tabla de simbolos Linea: " + tkn.getLine() + " Columna: " + (tkn.getColumn() + 1) + " -> Variable: '" + tkn.getLexema() + "' NO Declarada");
        }
        return "Void";
    }

    // Va guardando el valor actual de la variable
    public void st_setVal(Token tkn, String val) {
        Row row = (Row) hashmap.get(tkn.getLexema());
        if (row != null) {
            row.val = val;
        }
    }

    // Obtiene el Valor Actual
    public String st_getVal(Token tkn) {
        Row row = (Row) hashmap.get(tkn.getLexema());
        if (row != null) {
            return row.val;
        }
        return "ERROR";
    }

    // Cadena que contiene la tabla de Simbolos
    public String printSymTab() {
        String str = "Tabla   de   Simbolos--->\n"
                + "  TIPO\tVARIABLE\tLOCALIZACION\tLINEA\n";
                
        Set keySet = hashmap.keySet();
        for (Object key : keySet) {
            Row row = (Row) hashmap.get(key);
            str += getType(row.tknType);
            str += key.toString() + "\t";
            str += row.location + "\t\t";
            for (Integer lines : row.lines) {
                str += lines + " ";
            }
            str += "\n";
        }
        str += " ";
        if (logSymTab.size() > 0) {
            str += " ";
            for (String s : logSymTab) {
                str += s + "\n";
            }
        } else {
            str += " ";
        }
        return str;
    }

    // Metodo aux para verificar si es int, float o bool
    public String getType(String type) {
        if (type != null) {
            switch (type) {
                case "Integer":
                    return " int\t";
                case "Real":
                    return " float\t";
                case "Boolean":
                    return " bool\t";
                default:
                    return " ERROR\t";
            }
        }
        return " ERROR\t";
    }

}
