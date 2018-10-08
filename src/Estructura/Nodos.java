package Estructura;

public class Nodos {

    private Nodos[] hijo = new Nodos[3];
    private Nodos hermano;
    private Token token;
    private String nodeType;
    private Object type;
    private String valor;
    private String typeValue;
    private String exptype;

    public String getExpType() {//
        return exptype;
    }

    public void setExpType(String et) {//
        this.exptype = et;
    }

    public Nodos getSon(int Pos) {
        return hijo[Pos];
    }

    public void setSon(Nodos Son, int Pos) {
        this.hijo[Pos] = Son;
    }

    public Nodos getHermano() {
        return hermano;
    }

    public void setHermano(Nodos hermano) {
        this.hermano = hermano;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setTypeNode(String nodeType) {
        this.nodeType = nodeType;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }
}
