package Analisis;

import Estructura.Token;
import Estructura.Nodos;
import Archivo.Archivo;
import java.io.File;
import java.util.*;
import javax.swing.tree.DefaultTreeModel;

public class Analisis<T> {

    private File source; // Archivo fuente a analizar
    private List<Token> Tokens;
    private List<String> Errores;
    private List<String> Lexico;
    private Resultados res;
    private boolean error;
    private Nodos SyntaxTree = new Nodos();
    private Nodos SemanticTree = new Nodos();
    private Archivo archivo = new Archivo();
    String symTab, symTabLog;
    private String codigoIntermedio;

    public Analisis(File source) {
        this.source = source;
        this.Errores = new ArrayList<String>();
        this.Lexico = new ArrayList<String>();
        this.Tokens = new ArrayList<Token>();
    }

    public void comenzar() {
        this.Errores.clear();
        this.Lexico.clear();
        this.Tokens.clear();
        analisisLexico();

        if (res.Error_Lexico() == true) {
            res.Display_Lexico();
            analisisSintactico();

            if (error == false) {
                analisisSemantico();
                codigoIntermedio();
            }
        }

    }

    private void analisisLexico() {
        Errores.add(" ");
        Lexico AL = new Lexico(Tokens, Lexico, Errores);
        AL.analizar(source);
        Lexico = AL.getLexico();
        Errores = AL.getErrores();
        Tokens = AL.getTokens();
        res = new Resultados(Tokens, Lexico, Errores);
       // archivo.guardar(Lexico, "LOGS/+Tokens.log");
    }

    private void analisisSintactico() {
        res.Remove_Tokens();
        Errores = res.getErrores();
        Tokens = res.getTokens();
        Errores.add(" ");
        Sintactico Sin = new Sintactico(Tokens, Errores);
        SyntaxTree = Sin.parse();
        Errores = Sin.getErrores();
        error = Sin.getError();
    }

    private void analisisSemantico() {
        res.Remove_Tokens();
        Errores = res.getErrores();
        Tokens = res.getTokens();
        Errores.add(" ");

        Semantico Sem = new Semantico(Errores);
        SemanticTree = Sem.iniciaSemantico(SyntaxTree);
        Errores = Sem.getErrores();
        error = Sem.getError();
        symTab = Sem.getSymTab();
        symTabLog = Sem.getSymTabLog();

        Errores.add(symTabLog);

        //archivo.guardar(Errores, "LOGS/+Errores.log");
    }

    public void codigoIntermedio(){
        if(error==false)
        {
            CodGen cgenerator = new CodGen(SyntaxTree);
            cgenerator.codeGen(SyntaxTree);
            codigoIntermedio = "";
            for(String linea : cgenerator.codigogenerado){
                codigoIntermedio = codigoIntermedio + linea;
            }
            archivo.guardar(codigoIntermedio, "tm\\CodGen.tm");
        }
    }

    // Muestra errores obtenidos en Todo el Analisis
    public String getErrores() {
        return res.Display_Errores();
    }

    // Aqui se demuestra los resultados del analisis lexico
    public String getLexico() {
        return res.Display_Lexico();
    }

    // Arbol Sintactico
    public DefaultTreeModel getTreeSin() {
        return res.Display_Sintactico(SyntaxTree);
    }

    //Obtiene la tabla de Simbolos
    public String getSymTab() {
        return symTab;
    }

    //Obtiene Errores obtenidos en tabla de Simbolos
    public String getSymTabLog() {
        return symTabLog;
    }

    // Arbol Semantico (Arbol Sintactico con Anotaciones)
    public DefaultTreeModel getTreeSem() {
        return res.Display_Semantico(SemanticTree);
    }

    public String getCodigoIntermedio(){
        return codigoIntermedio;
    }
}
