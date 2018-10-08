package interfaz;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledEditorKit;

public class Entorno extends JTextPane {

    private File archivo;
    private String nombre;
    public int lineas = 1;

    public Entorno(File archivo){
        if (archivo == null) {
          
            this.archivo = null;
            this.nombre = "nuevo";
        } else {
            
            this.archivo = archivo;
            this.nombre = archivo.getName();
        }
        setEditorStyle(this);

        this.setFont(new Font("Consolas", Font.PLAIN, 12));
        this.setAutoscrolls(true);

        //this.putClientProperty(PlainDocument.tabSizeAttribute, 4);
    }

    public File getArchivo() {
        return archivo;
    }

    public void setArchivo(File archivo) {
        this.archivo = archivo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public void setEditorStyle(JTextPane txtPane) {
        EditorKit editorKit = new StyledEditorKit() {

            @Override
            public Document createDefaultDocument() {
                return new Estilo();
                //return new SyntaxDoc_Dark();
            }
        };
        txtPane.setEditorKitForContentType("text/java", editorKit);
        txtPane.setContentType("text/java");
    }
}
