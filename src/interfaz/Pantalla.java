package interfaz;

import Analisis.Analisis;
import Archivo.Archivo;
import static interfaz.Pantalla.area;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Joaquín Zavala
 */
public class Pantalla extends javax.swing.JFrame {

//<editor-fold defaultstate="collapsed" desc="VARIABLES">
    public static Entorno area;
    public Archivo archivo;
    public DefaultMutableTreeNode RootSin = new DefaultMutableTreeNode("Arbol Sintactico");
    public DefaultTreeModel TreeSin = new DefaultTreeModel(RootSin);
    public JTree SyntaxTree = new JTree(TreeSin);
    public DefaultMutableTreeNode RootSem = new DefaultMutableTreeNode("Arbol Semantico");
    public DefaultTreeModel TreeSem = new DefaultTreeModel(RootSin);
    public JTree SemanticTree = new JTree(TreeSin);

    //--------------------------------------------------------
    public static int fila;
    public static String path;
    final JTextPane numLineas = new JTextPane();

    public int lineas;

    //</editor-fold>
//<editor-fold defaultstate="collapsed" desc="CONSTRUCTOR">
    public Pantalla() {
        initComponents();

        archivo = new Archivo();
        newTab(null);
        /*
         this.area.addCaretListener(new CaretListener() {
         public void caretUpdate(CaretEvent e) {
         System.out.println("row=" + getRow(e.getDot(), (JTextComponent) e.getSource()));
         System.out.println("col=" + getColumn(e.getDot(), (JTextComponent) e.getSource()));

         }

         });
         */
        Image icono = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Joako0z\\Documents\\NetBeansProjects\\joako0zcomp\\src\\iconos_imagenes\\Icono_XLive.png");
        this.setIconImage(icono);

    }

    //</editor-fold>
//<editor-fold defaultstate="collapsed" desc="METODOS">
    //<editor-fold defaultstate="collapsed" desc="FILA Y COLUMNA">
    public int getRow(int pos, JTextComponent editor) {
        int rn = (pos == 0) ? 1 : 0;
        try {
            int offs = pos;
            while (offs > 0) {
                offs = Utilities.getRowStart(editor, offs) - 1;
                rn++;
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        this.jLabel1.setText("fila:" + Integer.toString(rn));

        return rn;

    }

    public int getColumn(int pos, JTextComponent editor) {
        int retorno;
        try {
            retorno = pos - Utilities.getRowStart(editor, pos) + 1;
            this.jLabel2.setText("columna:" + Integer.toString(retorno));
            return pos - Utilities.getRowStart(editor, pos) + 1;
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return -1;

    }

    //</editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="NEWTAB">
    public void newTab(File nomArchivo) {
        area = new Entorno(nomArchivo);
        area.addKeyListener(new editorKeyPressed());
        area.addCaretListener(new LineaColumna());

//        JScrollPane scrollPane = new JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(area);
        if (area.getArchivo() != null) {
            //mostrarArchivo(editor.getArchivo());
            area.setText(archivo.abrir(area.getArchivo()));
            area.lineas = archivo.numLineas;
        }
        tpEditor.addTab(area.getNombre(), scrollPane);

        //**Agrega numero de lineas
        final JEditorPane numLineas = new JTextPane();
        numLineas.setBackground(new Color(225, 225, 225));
        numLineas.setEditable(false);
        numLineas.setEnabled(false);
        numLineas.setSelectionColor(new Color(225, 225, 225));
        numLineas.setFont(new Font("Consolas", Font.PLAIN, 12));
        //panel.add(lineas, BorderLayout.WEST);
        scrollPane.setRowHeaderView(numLineas);
        String strLin = "";
        for (int i = 0; i < area.lineas; i++) {
            strLin += " " + (i + 1) + " \n";
        }
        numLineas.setText(strLin);

        ButtonTabComponent btc = new ButtonTabComponent(tpEditor, null);
        tpEditor.setTabComponentAt(tpEditor.getTabCount() - 1, btc);

        tpEditor.setSelectedIndex(tpEditor.getTabCount() - 1);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ABRIR">

    public void abrir() {
        JFileChooser fch = new JFileChooser(System.getProperty("user.dir"));
        //fch.setMultiSelectionEnabled(true);
        if (fch.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            newTab(fch.getSelectedFile());
        }
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="GUARDAR">

    public void guardar() {

        if (tpEditor.getTabCount() != 0) {
            JScrollPane jsp = (JScrollPane) tpEditor.getSelectedComponent();
            //editor = (Editor) jsp.getViewport().getComponent(0);
            JPanel p = (JPanel) jsp.getViewport().getComponent(0);
            area = (Entorno) p.getComponent(0);
            if (area.getArchivo() == null) {
                nuevoArchivo();
            } else {
                String[] strs = area.getText().split("\\n");
                archivo.guardar(Arrays.asList(strs), area.getArchivo().toString());
            }
        }
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NUEVOARCHIVO">

    public void nuevoArchivo() {
        if (tpEditor.getTabCount() != 0) {
            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String[] strs = area.getText().split("\\n");
                archivo.guardar(Arrays.asList(strs), fc.getSelectedFile().toString());
                area.setArchivo(fc.getSelectedFile());
                area.setName(area.getArchivo().getName());

                tpEditor.setTitleAt(tpEditor.getSelectedIndex(), area.getArchivo().getName() + ".txt");
                tpEditor.updateUI();
            }
        }
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="COMPILAR">

    public void compilar() {

        if (tpEditor.getTabCount() != 0) {
            JScrollPane jsp = (JScrollPane) tpEditor.getSelectedComponent();
            //editor = (Editor) jsp.getViewport().getComponent(0);
            JPanel p = (JPanel) jsp.getViewport().getComponent(0);
            area = (Entorno) p.getComponent(0);
            guardar();
            limpiar();

            Analisis analisis = new Analisis(area.getArchivo());
            analisis.comenzar();

            this.txtLex.setText(analisis.getLexico());
            if (analisis.getErrores().contains("Error:")) {
                this.txtLex.setText(this.txtLex.getText() + "\n\n Hay Errores");
                this.txtErrores_Lex.setText(analisis.getErrores());
            } else {
                this.txtLex.setText(this.txtLex.getText() + " ");
            }

            //Sintactico
            TreeSin = analisis.getTreeSin();
            SyntaxTree.setModel(TreeSin);
            TreePath pathSin = new TreePath(SyntaxTree.getModel().getRoot());
            JScrollPane barrasSintactico = new JScrollPane(SyntaxTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            panelSintactico.setLayout(new BorderLayout());
            panelSintactico.add(barrasSintactico);

            SyntaxTree.updateUI();

            //semantico
            TreeSem = analisis.getTreeSem();
            SemanticTree.setModel(TreeSem);
            TreePath pathSem = new TreePath(SemanticTree.getModel().getRoot());
            JScrollPane barrasSemantico = new JScrollPane(SemanticTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            panelSemantico.setLayout(new BorderLayout());
            panelSemantico.add(barrasSemantico);

            SemanticTree.updateUI();

            txtCodigoIntermedio.setText(analisis.getCodigoIntermedio());

            if (!txtCodigoIntermedio.getText().equals("")) {

                String command = "cmd /C start tm/TM.exe tm/CodGen.tm";
                try {
                    Runtime.getRuntime().exec(command);
                } catch (IOException ex) {
                    System.out.println("errorsaso");
                }
            }
            this.txtErrores_Lex.setText(analisis.getErrores());

        }

    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LIMPIAR">
    public void limpiar() {
        this.txtLex.setText("");
        this.txtErrores_Lex.setText("");
        panelSintactico.removeAll();
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FORMATO">
    public void formato() {
        String codigo;
        if (tpEditor.getTabCount() != 0) {
            JScrollPane jsp = (JScrollPane) tpEditor.getSelectedComponent();
            //editor = (Editor) jsp.getViewport().getComponent(0);
            JPanel p = (JPanel) jsp.getViewport().getComponent(0);
            area = (Entorno) p.getComponent(0);

            codigo = area.getText();

            String textaux = "";
            for (int x = 0; x < codigo.length(); x++) {

                if (!Character.isWhitespace(codigo.charAt(x))) {
                    textaux = textaux + codigo.charAt(x);
                } else {
                    if (codigo.charAt(x) == ' ') {
                        if (!Character.isWhitespace(codigo.charAt(x + 1))) {
                            textaux = textaux + codigo.charAt(x);
                        }
                    }
                }

            }
            
            System.out.println(textaux);

            for (int x = 0; x < textaux.length(); x++) {
                if (textaux.charAt(x) == ';' || textaux.charAt(x) == '{' || textaux.charAt(x) == '}') {

                        textaux = Pantalla.insert(codigo, "\n", x+1);
                    
                }

            }

            int contador = 0;
            for (int x = 0; x < textaux.length(); x++) {
                if (textaux.charAt(x) == '{') {
                    contador++;
                }

                if (contador != 0 && textaux.charAt(x) == '\n') {
                    for (int w = 0; w <= contador; w++) {
                        textaux = Pantalla.insert(textaux, "\t", x + 1);

                    }
                }

                if (textaux.charAt(x) == '}') {
                    contador--;
                }

            }

            area.setText(textaux);

        }

        /*
         //enter despues de punto y llave
         for (int x = 0; x < codigo.length(); x++) {
         if (codigo.charAt(x) == ';' || codigo.charAt(x) == '{' || codigo.charAt(x) == '}') {
         if (codigo.charAt(x + 1) != '\n') {
         //System.out.println("enter");
         codigo = Pantalla.insert(codigo, "\n", x + 1);
         }
         }

         }

         int contador = 0;
         for (int x = 0; x < codigo.length(); x++) {
         if (codigo.charAt(x) == '{') {
         contador++;
         int aux = x;
         }

         if (contador != 0 && codigo.charAt(x) == '\n') {
         for (int w = 0; w <= contador; w++) {
         codigo = Pantalla.insert(codigo, "\t", x + 1);

         }
         }

         if (codigo.charAt(x) == '}') {
         contador--;
         }

         }

         area.setText(codigo);

         }*/
    }
    // </editor-fold>

    //</editor-fold>
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        tpEditor = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLex = new javax.swing.JTextPane();
        panelSintactico = new javax.swing.JPanel();
        panelSemantico = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtCodigoIntermedio = new javax.swing.JTextPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtErrores_Lex = new javax.swing.JTextPane();
        jToolBar1 = new javax.swing.JToolBar();
        Btn_Nuevo = new javax.swing.JButton();
        Btn_Cerrar = new javax.swing.JButton();
        Btn_Guardar = new javax.swing.JButton();
        Btn_GuardarComo = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton4 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tpEditor.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 468, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tpEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 248, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tpEditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(jPanel1);

        jScrollPane1.setViewportView(txtLex);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 523, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Lexico", jPanel3);

        javax.swing.GroupLayout panelSintacticoLayout = new javax.swing.GroupLayout(panelSintactico);
        panelSintactico.setLayout(panelSintacticoLayout);
        panelSintacticoLayout.setHorizontalGroup(
            panelSintacticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 523, Short.MAX_VALUE)
        );
        panelSintacticoLayout.setVerticalGroup(
            panelSintacticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Sintactico", panelSintactico);

        javax.swing.GroupLayout panelSemanticoLayout = new javax.swing.GroupLayout(panelSemantico);
        panelSemantico.setLayout(panelSemanticoLayout);
        panelSemanticoLayout.setHorizontalGroup(
            panelSemanticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 523, Short.MAX_VALUE)
        );
        panelSemanticoLayout.setVerticalGroup(
            panelSemanticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Semantico", panelSemantico);

        jScrollPane2.setViewportView(txtCodigoIntermedio);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 523, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Codigo Intermedio", jPanel8);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 528, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPane2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 248, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.Alignment.TRAILING))
        );

        jSplitPane2.setRightComponent(jPanel2);

        jLabel1.setText("fila:");

        jLabel2.setText("columna:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(jLabel2))
        );

        jScrollPane3.setViewportView(txtErrores_Lex);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 998, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 998, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 132, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Errores", jPanel7);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPane3))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 160, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
        );

        jToolBar1.setRollover(true);

        Btn_Nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos_imagenes/Empty-Document-New-32.png"))); // NOI18N
        Btn_Nuevo.setFocusable(false);
        Btn_Nuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Btn_Nuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Btn_Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_NuevoActionPerformed(evt);
            }
        });
        jToolBar1.add(Btn_Nuevo);

        Btn_Cerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos_imagenes/Open-File-32.png"))); // NOI18N
        Btn_Cerrar.setFocusable(false);
        Btn_Cerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Btn_Cerrar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Btn_Cerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_CerrarActionPerformed(evt);
            }
        });
        jToolBar1.add(Btn_Cerrar);

        Btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos_imagenes/Disquette-32.png"))); // NOI18N
        Btn_Guardar.setFocusable(false);
        Btn_Guardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Btn_Guardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_GuardarActionPerformed(evt);
            }
        });
        jToolBar1.add(Btn_Guardar);

        Btn_GuardarComo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos_imagenes/Actions-document-save-all-icon.png"))); // NOI18N
        Btn_GuardarComo.setFocusable(false);
        Btn_GuardarComo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Btn_GuardarComo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Btn_GuardarComo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_GuardarComoActionPerformed(evt);
            }
        });
        jToolBar1.add(Btn_GuardarComo);
        jToolBar1.add(jSeparator1);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos_imagenes/openterm-32.png"))); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);

        jMenu1.setText("Archivo");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Abrir");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Guardar");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem3.setText("Formato");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        abrir();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed

    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void Btn_NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_NuevoActionPerformed
        newTab(null);


    }//GEN-LAST:event_Btn_NuevoActionPerformed

    private void Btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_GuardarActionPerformed
        guardar();
    }//GEN-LAST:event_Btn_GuardarActionPerformed

    private void Btn_GuardarComoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_GuardarComoActionPerformed
        nuevoArchivo();
    }//GEN-LAST:event_Btn_GuardarComoActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        compilar();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void Btn_CerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_CerrarActionPerformed
        // TODO add your handling code here:
        abrir();
    }//GEN-LAST:event_Btn_CerrarActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        formato();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Pantalla.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Pantalla.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Pantalla.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Pantalla.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Pantalla().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Btn_Cerrar;
    private javax.swing.JButton Btn_Guardar;
    private javax.swing.JButton Btn_GuardarComo;
    private javax.swing.JButton Btn_Nuevo;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel panelSemantico;
    private javax.swing.JPanel panelSintactico;
    private javax.swing.JTabbedPane tpEditor;
    private javax.swing.JTextPane txtCodigoIntermedio;
    private javax.swing.JTextPane txtErrores_Lex;
    private javax.swing.JTextPane txtLex;
    // End of variables declaration//GEN-END:variables

    private class editorKeyPressed implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void keyPressed(KeyEvent e) {

            JScrollPane jsp = (JScrollPane) tpEditor.getSelectedComponent();
            JEditorPane lineas = (JEditorPane) jsp.getRowHeader().getComponent(0);
            JPanel p = (JPanel) jsp.getViewport().getComponent(0);
            area = (Entorno) p.getComponent(0);

            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    ++area.lineas;
                    break;

                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DELETE:
                    int end = area.viewToModel(new Point(
                            jsp.getViewport().getViewPosition().x + area.getWidth(),
                            jsp.getViewport().getViewPosition().y + area.getHeight()));
                    Document doc = area.getDocument();
                    int endline = doc.getDefaultRootElement().getElementIndex(end);
                    area.lineas = endline;
                    break;
                default:
                    return;

            }

            String strLin = "";
            for (int i = 0; i < area.lineas; i++) {
                strLin += " " + (i + 1) + " \n";
            }
            lineas.setText(strLin);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private class LineaColumna implements CaretListener {

        @Override
        public void caretUpdate(CaretEvent e) {
            System.out.println("row=" + getRow(e.getDot(), (JTextComponent) e.getSource()));
            System.out.println("col=" + getColumn(e.getDot(), (JTextComponent) e.getSource()));

        }

    }

    public static String insert(String bag, String marble, int index) {
        String bagBegin = bag.substring(0, index);
        String bagEnd = bag.substring(index);
        return bagBegin + marble + bagEnd;
    }

}
