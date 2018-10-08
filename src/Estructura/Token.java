package Estructura;

public class Token {

    private String type;
    String valor;
    String cat;
    private String lexema;
    private int linea, columna;
    private String nombre;
    
    public Token() {
        lexema = "";
    }

    public void setLexema(String lex) {
        this.lexema = lex;
        this.nombre=lex;
        if (lex.equals("<") || lex.equals(">") || lex.equals("<=") || lex.equals(">=") || lex.equals("==") || lex.equals("<>")) {
        } else {
            if (isNumberDouble(lex)) {
                this.valor = lex;
            } else {
                this.valor = "0";
            }
        }
    }

    public boolean isNumberDouble(String cadena) {
        try {
            Double.parseDouble(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public String getLexema() {
        return lexema;
    }

    public void setValor(String valor){
        this.valor=valor;
    }

    public String getValor(){
        return valor;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        setCat();
    }

    public int getLine() {
        return linea;
    }

    public void setLine(int lin) {
        this.linea = lin;
    }

    public int getColumn() {
        return columna;
    }

    public void setColumn(int col) {
        this.columna = col;
    }

    public String getCat() {
        return cat;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCat() {
        switch (getType()) {
            case "TKN_ERROR": {
                this.cat = "TKN_ERROR";
                break;
            }

            case "TKN_TEXT":
            case "TKN_INTEGER":
            case "TKN_FLOAT": {
                this.cat = "TKN_CONSTANT";
                break;
            }

            //case TKN_STRING:
            case "TKN_BEGIN":
            case "TKN_END":
            case "TKN_INT":
            case "TKN_IF":
            case "TKN_THEN":
            case "TKN_ELSE":
            case "TKN_FOR":
            case "TKN_WHILE":
            case "TKN_UNTIL":
            case "TKN_DO":
            case "TKN_EQUAL":
            case "TKN_NOTEQUAL":
            case "TKN_LT":
            case "TKN_LTE":
            case "TKN_GT":
            case "TKN_COMPAR":
            case "TKN_GTE": {
                this.cat = "TKN_RELATIONAL";
                break;
            }

            case "TKN_PLUS":
            case "TKN_MINUS":
            case "TKN_TIMES":
            case "TKN_OVER": {
                this.cat = "TKN_ARITHMETIC";
                break;
            }

            case "TKN_COMME":
            case "TKN_MCOMME": {
                this.cat = "TKN_COMMENT";
                break;
            }

            case "TKN_LPAREN":
            case "TKN_RPAREN":
            case "TKN_LKEY":
            case "TKN_RKEY": {
                this.cat = "TKN_GROUP";
                break;
            }

            case "TKN_ASSIGMENT": {
                this.cat = "TKN_ASSIG";
                break;
            }

            case "TKN_SEMI": {
                this.cat = "TKN_ENDLINE";
                break;
            }

            case "TKN_EOF": {
                this.cat = "TKN_EOF";
                break;
            }

            default: {
                this.cat = "TKN_ID";
            }
        }
    }
}
