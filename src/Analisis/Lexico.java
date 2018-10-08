
package Analisis;

import Estructura.Token;
import java.io.*;
import java.util.regex.*;
import java.util.*;

class Lexico {

    private String estado;
    private List<String> Error = new ArrayList<String>();
    private List<String> Lexico = new ArrayList<String>();
    private List<Token> Tokens = new ArrayList<Token>();

    Lexico(List<Token> Tokens, List<String> Lexico, List<String> Error) {
        this.Tokens = Tokens;
        this.Lexico = Lexico;
        this.Error = Error;
    }

    public void analizar(File source) {
        int numLin = 0;
        estado = "Inicio";
        Token eof = new Token();
        try {
            String lexema = "";
            String linea = null;
            BufferedReader Archivo = new BufferedReader(new FileReader(source));
            do {
                linea = Archivo.readLine(); // Lee linea por linea
                if (linea != null) {
                    lexema = GetTokens(linea.trim(), ++numLin, lexema);
                }
            } while (linea != null);
            if (lexema.length() > 0) {
                nuevoToken(lexema, numLin, -1);
            }
            Archivo.close();

            eof.setLexema("FIN DE ARCHIVO");
            eof.setType("TKN_EOF");
            Tokens.add(eof);
        } catch (java.io.FileNotFoundException e) {
            Error.add("Error! El archivo " + e + " No Existente");
        } catch (IOException e) {
            Error.add("Error! No se puede leer el archivo");
        }

        //Error.add("Lineas analizadas " + numLin);
    }

    //Funcion Principal obtiene los tokens
    private String GetTokens(String Linea, int numLin, String lexema) {
        int numCol;
        for (numCol = 0; numCol < Linea.length(); numCol++) {
            switch (estado) {
                case "Inicio": {
                    while (Linea.charAt(numCol) == ' ' || Linea.charAt(numCol) == '\t') { // Omite espacios blancos y tabs
                        numCol++;
                    }
                    if (Character.isLetter(Linea.charAt(numCol))) { // Checa si es letra
                        estado = "Identificador";
                        lexema = String.valueOf(Linea.charAt(numCol));
                    } else if (Character.isDigit(Linea.charAt(numCol))) {// Checa si es numero
                        estado = "Numero";
                        lexema = String.valueOf(Linea.charAt(numCol));
                    } else {
                        switch (Linea.charAt(numCol)) {
                            case '(': {
                                estado = "Inicio";
                                lexema = nuevoToken(String.valueOf('('), numLin, (numCol - lexema.length()));
                                break;
                            }
                            case ')': {
                                estado = "Inicio";
                                lexema = nuevoToken(String.valueOf(')'), numLin, (numCol - lexema.length()));
                                break;
                            }
                            case '{': {
                                estado = "Inicio";
                                lexema = nuevoToken(String.valueOf('{'), numLin, (numCol - lexema.length()));
                                break;
                            }
                            case '}': {
                                estado = "Inicio";
                                lexema = nuevoToken(String.valueOf('}'), numLin, (numCol - lexema.length()));
                                break;
                            }
                            case ';': {
                                estado = "Inicio";
                                lexema = nuevoToken(String.valueOf(';'), numLin, (numCol - lexema.length()));
                                break;
                            }
                            case '+': {
                                //state = State.PLUS;
                                estado = "Inicio";
                                //lexema = String.valueOf('+');
                                String mas = Linea.charAt(numCol+1)+"";
                                if(mas.equals("+")){
                                    numCol++;
                                    lexema = nuevoToken(String.valueOf("++"), numLin, (numCol - lexema.length()));
                                    
                                }else{
                                lexema = nuevoToken(String.valueOf('+'), numLin, (numCol - lexema.length()));
                                }
                                break;
                            }
                            case '-': {
                                estado = "Inicio";                              
                                
                                String mas = Linea.charAt(numCol+1)+"";
                                if(mas.equals("-")){
                                    numCol++;
                                    lexema = nuevoToken(String.valueOf("--"), numLin, (numCol - lexema.length()));
                                    
                                }else{
                                lexema = nuevoToken(String.valueOf('-'), numLin, (numCol - lexema.length()));
                                }
                                break;
                            }
                            case '*': {
                                estado = "Inicio";
                                lexema = nuevoToken(String.valueOf('*'), numLin, (numCol - lexema.length()));
                                //lexema = String.valueOf('*');
                                break;
                            }
                            case '=': {
                                estado = "Igual";
                                lexema = String.valueOf('=');
                                break;
                            }
                            case ':': {
                                estado = "Asignacion";
                                lexema = String.valueOf(':');
                                break;
                            }
                            case '<': {
                                estado= "Menor";
                                lexema = String.valueOf('<');
                                break;
                            }
                            case '>': {
                                estado= "Mayor";
                                lexema = String.valueOf('>');
                                break;
                            }
                            case '/': {
                                estado ="Diagonal";
                                //state = State.MCOMMENT;
                                lexema = String.valueOf('/');
                                break;
                            }
                            case '"': {
                                estado ="Texto";
                                lexema = String.valueOf('"');
                                break;
                            }
                            case '!': {
                                estado="Desigualdad";
                                lexema = String.valueOf('!');
                                break;
                            }
                            default: { // en caso de no encontrar token definido guarda error
                                lexema = nuevoToken(String.valueOf(Linea.charAt(numCol)), numLin, (numCol - lexema.length()));
                            }
                        }
                    }
                    break;
                }
                case "Identificador": {
                    if (Character.isLetterOrDigit(Linea.charAt(numCol))) { // busca numeros y letras
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                    } else {
                        numCol--;
                        estado="Inicio";
                        lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
                    }
                    break;
                }
                case "Numero": {
                    if (Character.isDigit(Linea.charAt(numCol)) || (lexema.contains(".") == false && Linea.charAt(numCol) == '.')) {
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                    } else {
                        numCol--;
                        estado="Inicio";
                        lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
                    }
                    break;
                }
                case "Menor": { // operador <
                    if (Linea.charAt(numCol) == '=') { // operador <=
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                        lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
                    } else {
                        numCol--;
                        lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
                    }
                    estado="Inicio";
                    break;
                }
                case "Igual": { // operado igualdad
                    if (Linea.charAt(numCol) == '=') { // operador ==
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                        lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
                    } else {
                        numCol--;
                        lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
                    }
                    estado="Inicio";
                    break;
                }
                case "Desigualdad": { // operador desigual
                    if (Linea.charAt(numCol) == '=') { //operador !=
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                        lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
                    } else {
                        numCol--;
                        lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
                    }
                    estado="Inicio";
                    break;
                }
                case "Mayor":  // operador >
                case "Asignacion": { // operador asignacion
                    if (Linea.charAt(numCol) == '=') {
                        estado="Inicio";
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                        lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
                    } else {
                        numCol--;
                        lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
                    }
                    estado="Inicio";
                    break;
                }
                case "Diagonal": { 
                    if (Linea.charAt(numCol) == '*') { // comentarios
                        estado="Gcomentario";
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                    } else if (Linea.charAt(numCol) == '/') { // comentarios
                        estado="Comentario";
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                    } else {
                        numCol--;
                        estado="Inicio";
                        lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
                    }
                    break;
                }
                case "Gcomentario": {
                    if (Linea.charAt(numCol) != '*') {
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                    } else {
                        estado="Fcomentario";
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                    }
                    break;
                }
                case "Comentario": {
                    lexema = lexema + String.valueOf(Linea.charAt(numCol));
                    break;
                }
                case "Fcomentario": {
                    if (Linea.charAt(numCol) == '/') {
                        estado="Inicio";
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                        lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
                    } else if (Linea.charAt(numCol) == '*') {
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                    } else {
                        estado="Gcomentario";
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                    }
                    break;
                }
                case "Texto": { // cadena entre comillas
                    if (Linea.charAt(numCol) == '"') {
                        estado="Inicio";
                        lexema = lexema + String.valueOf('"');
                        lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
                    } else {
                        lexema = lexema + String.valueOf(Linea.charAt(numCol));
                    }
                }
            }
        }
        if (estado.equals("Gcomentario") || estado.equals("Fcomentario") || estado.equals("Diagonal")) {
            return lexema;
        } else {
            estado="Inicio";
            lexema = nuevoToken(lexema, numLin, (numCol - lexema.length()));
        }
        return lexema;
    }

    private String nuevoToken(String lexema, int numLinea, int numColumna) {
        String tokenType;
        String texto;
        // Checa que lexema enviado este dentro de la gramatica
        if (lexema.length() > 0) {
            if (Pattern.matches("int", lexema)) {
                tokenType = "TKN_INT";
                texto="->Palabra reservada->int";
            } else if (Pattern.matches("if", lexema)) {
                tokenType = "TKN_IF";
                texto="->Palabra reservada->if";
            } else if (Pattern.matches("fi", lexema)) {
                tokenType = "TKN_FI";
                texto="->Palabra reservada->fi";
            } else if (Pattern.matches("then", lexema)) {
                tokenType = "TKN_THEN";
                texto="->Palabra reservada->then";
            } else if (Pattern.matches("else", lexema)) {
                tokenType = "TKN_ELSE";
                texto="->Palabra reservada->else";
            } else if (Pattern.matches("for", lexema)) {
                tokenType = "TKN_FOR";
                texto="->Palabra reservada->for";
            } else if (Pattern.matches("while", lexema)) {
                tokenType = "TKN_WHILE";
                texto="->Palabra reservada->while";
            } else if (Pattern.matches("do", lexema)) {
                tokenType = "TKN_DO";
                texto="->Palabra reservada->do";
            } else if (Pattern.matches("until", lexema)) {
                tokenType = "TKN_UNTIL";
                texto="->Palabra reservada->until";
            } else if (Pattern.matches("read", lexema)) {
                tokenType = "TKN_READ";
                texto="->Palabra reservada->read";
            } else if (Pattern.matches("write", lexema)) {
                tokenType = "TKN_WRITE";
                texto="->Palabra reservada->write";
            } else if (Pattern.matches("float", lexema)) {
                tokenType = "TKN_FLOAT";
                texto="->Palabra reservada->float";
            } else if (Pattern.matches("bool", lexema)) {
                tokenType = "TKN_BOOL";
                texto="->Palabra reservada->boolean";
            } else if (Pattern.matches("program", lexema)) {
                tokenType = "TKN_PROG";
                texto="->Palabra reservada->program";
            } else if (Pattern.matches("[a-zA-Z]([a-zA-Z0-9])*", lexema)) { // ER Identificadores
                tokenType = "TKN_ID";
                texto="->Identificador->Letra seguida de caracteres de letras/numeros";
            } else if (Pattern.matches("\".*\"", lexema)) { // ER Cadena entre "comillas"
                tokenType = "TKN_TEXT";
                texto="->Literal->Caracteres entre comillas";
            } else if (Pattern.matches("([0-9][0-9]*)", lexema)) { // ER numeros enteros
                tokenType = "TKN_INTEGER";  
                texto="->Constante->Entero";
            } else if (Pattern.matches("([0-9][0-9]*)\\.[0-9]+", lexema)) { // ER numeros reales
                tokenType = "TKN_REAL";
                texto="->Constante->Real";
            } else if (Pattern.matches("\\(", lexema)) {
                tokenType = "TKN_LPAREN";
                texto="->Signo Puntuacion->Parentesis que abre";
            } else if (Pattern.matches("\\)", lexema)) {
                tokenType = "TKN_RPAREN";
                texto="->Signo Puntuacion->Parentesis que cierra";
            } else if (Pattern.matches("\\{", lexema)) {
                tokenType = "TKN_LKEY";
                texto="->Signo Puntuacion->Llave que abre";
            } else if (Pattern.matches("\\}", lexema)) {
                tokenType = "TKN_RKEY";
                texto="->Signo Puntuacion->Llave que cierra";
            } else if (Pattern.matches(":=", lexema)) {
                tokenType = "TKN_EQUAL";
                texto="->operador->asignacion";
            } else if (Pattern.matches("==", lexema)) {
                tokenType = "TKN_COMPAR";
                texto="->operador->Comparacion";
            } else if (Pattern.matches("!=", lexema)) {
                tokenType = "TKN_NOTEQUAL";
                texto="->operador->Desigualdad";
            } else if (Pattern.matches("<", lexema)) {
                tokenType = "TKN_LT";
                texto="->operador->Menor que";
            } else if (Pattern.matches("<=", lexema)) {
                tokenType = "TKN_LTE";
                texto="->operador->Menor o Igual";
            } else if (Pattern.matches(">", lexema)) {
                tokenType = "TKN_GT";
                texto="->operador->Mayor que";
            } else if (Pattern.matches(">=", lexema)) {
                tokenType = "TKN_GTE";
                texto="->operador->Mayor o igual";
            } else if (Pattern.matches("\\+", lexema)) {
                tokenType = "TKN_PLUS";
                texto="->operador->Operador Suma";
            } else if (Pattern.matches("\\++", lexema)) {
                tokenType = "TKN_PLUSP";
                texto="->operador->Operador adicion";
            } else if (Pattern.matches("\\-", lexema)) {
                tokenType = "TKN_MINUS"; 
                texto="->operador->Operador Resta";
            } else if (Pattern.matches("\\--", lexema)) {
                tokenType = "TKN_MINUSP";
                texto="->operador->Operador decremento";
            } else if (Pattern.matches("\\*", lexema)) {
                tokenType = "TKN_TIMES";
                texto="->operador->Operador Multiplicacion";
            } else if (Pattern.matches("/", lexema)) {
                tokenType = "TKN_OVER";
                texto="->operador->Operador Division";
            } else if (Pattern.matches(";", lexema)) {
                tokenType = "TKN_SEMI";
                texto="->operador->Punto y coma";
            } else if (Pattern.matches(",", lexema)) {
                tokenType = "TKN_COMA";
                texto="->operador->Coma";
            } else if (Pattern.matches("//.*", lexema)) {
                tokenType = "TKN_COMME";
                texto="->COMENTARIO";
            } else if (Pattern.matches("\\/*.*\\*/", lexema)) { // ER Comentario Multiple
                tokenType = "TKN_MCOMME";
                texto="->COMENTARIO MULTILINEA";
            } else {
                tokenType = "TKN_ERROR";
                texto="->Error";
            }

            // Crea variable tipo Token donde se guarda la posicion y tipo de lexema
            Token token = new Token();
            token.setLine(numLinea);
            token.setColumn(numColumna);
            token.setLexema(lexema);
            token.setType(tokenType);
            Tokens.add(token);
            if (tokenType.equals("TKN_ERROR")) {
                Error.add("  Error: Linea " + numLinea + ", Columna " + (numColumna + 1) + ", Token:   \" " + lexema + " \"");
            } else {
                Lexico.add(" " + token.getLexema() + "     " + texto + " ");
            }

        }
        return "";
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
}
