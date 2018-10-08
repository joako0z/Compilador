
package Archivo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.swing.JOptionPane;


public class Archivo {

    public int numLineas;

    public Archivo() {
        File f = new File("Arch");
        if (!f.exists()) {
            f.mkdir();
        }
    }

    public void guardar(List<String> lineas, String nombre) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(nombre, false));
            for (String str : lineas) {
                out.println(str);
            }
            out.flush();
            out.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void guardar(String texto, String nombre) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(nombre, false));
            String[] lineas = texto.split("\n");
            for(String linea : lineas){
                out.println(linea);
            }
            out.flush();
            out.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String abrir(File archivo) {
        FileReader fichero;
        String lineas = "";
        numLineas = 1;
        try {
            fichero = new FileReader(archivo);
            String linea;
            BufferedReader leerLinea = new BufferedReader(fichero);
            while ((linea = leerLinea.readLine()) != null) {
                lineas += (linea + "\n");
                //editor.setText(editor.getText() + linea + "\n");
                numLineas++;
            }
            leerLinea.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex2) {
            JOptionPane.showMessageDialog(null, ex2.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
        //editor.lineas = contLin;
        return lineas;
    }
}
