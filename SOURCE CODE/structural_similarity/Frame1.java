package structural_similarity;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import com.borland.jbcl.layout.XYLayout;
import com.borland.jbcl.layout.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.Dimension;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.*;
import javax.swing.*;
import java.beans.*;
import java.util.Random;

public class Frame1
    extends JFrame {
  JPanel contentPane;
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  XYLayout xYLayout1 = new XYLayout();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JTextField jTextField1 = new JTextField();
  JTextField jTextField2 = new JTextField();
  JButton jButton3 = new JButton();

  JFileChooser filechooser1 = new JFileChooser();
  JFileChooser filechooser2 = new JFileChooser();
  String source_file ="";
  String target_file ="";
  JLabel jLabel5 = new JLabel();
  JProgressBar jProgressBar1 = new JProgressBar();
  //-------------------------------------------------------

  public Frame1() {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(xYLayout1);
    setSize(new Dimension(399, 341));
    setTitle("Matching ontologies");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new Frame1_jMenuFileExit_ActionAdapter(this));
    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new Frame1_jMenuHelpAbout_ActionAdapter(this));
    contentPane.setMinimumSize(new Dimension(500, 700));
    contentPane.setPreferredSize(new Dimension(500, 700));
    contentPane.setToolTipText("");
    jButton1.setText("Source Ontology");
    jButton1.addActionListener(new Frame1_jButton1_actionAdapter(this));
    jButton2.setText("Target Ontology");
    jButton2.addActionListener(new Frame1_jButton2_actionAdapter(this));
    jLabel1.setText("");
    jLabel2.setText("");
    jLabel3.setText("Threshold Value:");
    jLabel4.setText("Bias Value:");
    jTextField1.setText("0.70");
    jTextField2.setText("0.10");
    jButton3.setText("Proceed");
    jButton3.addMouseListener(new Frame1_jButton3_mouseAdapter(this));
    jButton3.addActionListener(new Frame1_jButton3_actionAdapter(this));
    jLabel5.setText("Calculation Time(sec):");
    jMenuBar1.add(jMenuFile);
    jMenuFile.add(jMenuFileExit);
    jMenuBar1.add(jMenuHelp);
    jMenuHelp.add(jMenuHelpAbout);
    contentPane.add(jLabel1, new XYConstraints(18, 51, 366, 27));
    contentPane.add(jLabel3, new XYConstraints(18, 126, 84, 30));
    contentPane.add(jLabel4, new XYConstraints(160, 126, 58, 30));
    contentPane.add(jButton1, new XYConstraints(18, 11, 115, 29));
    contentPane.add(jLabel2, new XYConstraints(18, 82, 366, 23));
    contentPane.add(jButton2, new XYConstraints(185, 11, 115, 29));
    contentPane.add(jButton3, new XYConstraints(272, 170, 112, 33));
    contentPane.add(jLabel5, new XYConstraints(16, 210, 179, 30));
    contentPane.add(jProgressBar1, new XYConstraints(16, 173, 221, 30));
    contentPane.add(jTextField1, new XYConstraints(101, 126, 43, 27));
    contentPane.add(jTextField2, new XYConstraints(225, 126, 47, 27));
    setJMenuBar(jMenuBar1);

    filechooser1 = new JFileChooser(filechooser1.getCurrentDirectory());
    filechooser2 = new JFileChooser(filechooser2.getCurrentDirectory());
  }

  /**
   * File | Exit action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
    System.exit(0);
  }

  /**
   * Help | About action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuHelpAbout_actionPerformed(ActionEvent actionEvent) {
    Frame1_AboutBox dlg = new Frame1_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation( (frmSize.width - dlgSize.width) / 2 + loc.x,
                    (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.setVisible(true);
  }
//--------------------------------------------------------------------
  public void jButton1_actionPerformed(ActionEvent e) {
    jLabel5.setText("Calculation Time(sec): ");
    filechooser1.setMultiSelectionEnabled(false);
    filechooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
    int result = filechooser1.showOpenDialog( this );
    if ( result == JFileChooser.CANCEL_OPTION )
      System.exit( 1 );
    source_file = filechooser1.getSelectedFile().getAbsolutePath(); // get selected file
    // display error if invalid
    if ( source_file.equals( "" ) )
     {
       JOptionPane.showMessageDialog( this, "Invalid File Name",
       "Invalid File Name", JOptionPane.ERROR_MESSAGE );
        System.exit( 1 );
     } // end if
     jLabel1.setText(source_file);

  }
//---------------------------------------------------------------
  public void jButton2_actionPerformed(ActionEvent e) {
    jLabel5.setText("Calculation Time(sec): ");
    filechooser2.setMultiSelectionEnabled(false);
    filechooser2.setFileSelectionMode(JFileChooser.FILES_ONLY);
    int result = filechooser2.showOpenDialog( this );
    if ( result == JFileChooser.CANCEL_OPTION )
      System.exit( 1 );
    target_file = filechooser2.getSelectedFile().getAbsolutePath(); // get selected file
    // display error if invalid
    if ( target_file.equals( "" ) )
     {
       JOptionPane.showMessageDialog( this, "Invalid File Name",
       "Invalid File Name", JOptionPane.ERROR_MESSAGE );
        System.exit( 1 );
     } // end if
     jLabel2.setText(target_file);

  }
  //=====================//
  // UPDATING THE FRAME  //
  //=====================//
  private void update(){

    jButton1.update(jButton1.getGraphics());
    jButton2.update(jButton2.getGraphics());
    jButton3.update(jButton3.getGraphics());
    jLabel5.update(jLabel5.getGraphics());
    jProgressBar1.update(jProgressBar1.getGraphics());
  }
//-------------------------------------------------------------------------
  public void jButton3_actionPerformed(ActionEvent e) {

    jButton1.setEnabled(false);
    jButton2.setEnabled(false);
    jButton3.setEnabled(false);
    jProgressBar1.setValue(0);
    jProgressBar1.setVisible(true);


    OntModel model1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    OntModel model2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

    long exec_time1 = System.nanoTime();


    //jLabel5.setText("Calculation Time(sec): " + String.format("%.3f", time));
    //jLabel6.setText("READING ONTOLOGIES!!");
    jProgressBar1.setValue(3);

    update();
    //------------------------------------------------------------//
    // CALCULATING STRUCTURAL SIMILARITY BETWEEN MODEL1 AND MODEL2//
    //------------------------------------------------------------//
       Structural init = new Structural();
       jProgressBar1.setValue(5);
       jProgressBar1.update(jProgressBar1.getGraphics());
       source_file = init.path_convert(filechooser1.getSelectedFile().getAbsolutePath());
       target_file = init.path_convert(filechooser2.getSelectedFile().getAbsolutePath());
       model1.read(source_file, "RDF/XML");
       jProgressBar1.setValue(9);
       jProgressBar1.update(jProgressBar1.getGraphics());
       model2.read(target_file, "RDF/XML");

       jProgressBar1.setValue(13);
       jProgressBar1.update(jProgressBar1.getGraphics());

       float [][] cstruct = init.struct_sim(model1, model2, jProgressBar1);
       int i,j;
       jProgressBar1.setValue(65);
       jProgressBar1.update(jProgressBar1.getGraphics());

       //update();
       //---------------------------------------------------------//
       // CALCULATING LEXICAL SIMILARITY BETWEEN MODEL1 AND MODEL2//
       //---------------------------------------------------------//
       Lexical lex = new Lexical();
       float[][] clex = lex.class_sim(model1, model2);
       jProgressBar1.setValue(70);
       jProgressBar1.update(jProgressBar1.getGraphics());
       float[][] oplex = lex.op_sim(model1, model2);
       jProgressBar1.setValue(75);
       jProgressBar1.update(jProgressBar1.getGraphics());
       float[][] dplex = lex.dp_sim(model1, model2);
       jProgressBar1.setValue(80);
       jProgressBar1.update(jProgressBar1.getGraphics());
       /*
       exec_time2 = System.nanoTime()-exec_time1;
       time = exec_time2/(float)Math.pow(10, 9);
       jLabel5.setText("Calculation Time(sec): " + String.format("%.3f", time));
       //jLabel6.setText("LEXICAL SIMILARITY CALCULATED!!");
       //update();
       */
      jProgressBar1.setValue(85);
      jProgressBar1.update(jProgressBar1.getGraphics());
       //update();


       //---------------------------------------------------------//
       // CALCULATING OVERALL SIMILARITY BETWEEN MODEL1 AND MODEL2//
       //---------------------------------------------------------//
       overall over = new overall();
       float[][] c_over = over.class_sim(cstruct, clex);
       float[][] op_over = over.op_sim(oplex, c_over, model1, model2,
                                     Float.valueOf(jTextField1.getText()),
                                     Float.valueOf(jTextField2.getText()));
       float[][] dp_over = over.dp_sim(dplex, c_over, model1, model2,
                                     Float.valueOf(jTextField1.getText()),
                                     Float.valueOf(jTextField2.getText()));


       jProgressBar1.setValue(90);
       jProgressBar1.update(jProgressBar1.getGraphics());
       //update();

       int[] itersize = new int[6];
       ExtendedIterator cnames1 = model1.listNamedClasses();
       itersize[0] = cnames1.toList().size();
       cnames1 = model1.listNamedClasses();

       ExtendedIterator cnames2 = model2.listNamedClasses();
       itersize[1] = cnames2.toList().size();
       cnames2 = model2.listNamedClasses();

       ExtendedIterator opnames1 = model1.listObjectProperties();
       itersize[2] = opnames1.toList().size();
       opnames1 = model1.listObjectProperties();

       ExtendedIterator opnames2 = model2.listObjectProperties();
       itersize[3] = opnames2.toList().size();
       opnames2 = model2.listObjectProperties();

       ExtendedIterator dpnames1 = model1.listDatatypeProperties();
       itersize[4] = dpnames1.toList().size();
       dpnames1 = model1.listDatatypeProperties();
       //System.out.println("dp1:" + itersize[4]);

       ExtendedIterator dpnames2 = model2.listDatatypeProperties();
       itersize[5] = dpnames2.toList().size();
       dpnames2 = model2.listDatatypeProperties();
       //System.out.println("dp2:" + itersize[5]);
       excel ex = new excel();
       ex.output(c_over, op_over, dp_over, cnames1, cnames2, opnames1,
               opnames2, dpnames1, dpnames2, itersize, Float.valueOf(jTextField1.getText()));


       jProgressBar1.setValue(100);
       jProgressBar1.update(jProgressBar1.getGraphics());
       long exec_time2 = System.nanoTime()-exec_time1;
       float time = exec_time2/(float)Math.pow(10, 9);
       jLabel5.setText("Calculation Time(sec): " + String.format("%.3f", time));

       jButton1.setEnabled(true);
       jButton2.setEnabled(true);
       jButton3.setEnabled(true);
       update();


    }
}

class Frame1_jButton3_actionAdapter
    implements ActionListener {
  private Frame1 adaptee;
  Frame1_jButton3_actionAdapter(Frame1 adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton3_actionPerformed(e);
  }
}

class Frame1_jButton3_mouseAdapter
    extends MouseAdapter {
  private Frame1 adaptee;
  Frame1_jButton3_mouseAdapter(Frame1 adaptee) {
    this.adaptee = adaptee;
  }

}

class Frame1_jButton2_actionAdapter
    implements ActionListener {
  private Frame1 adaptee;
  Frame1_jButton2_actionAdapter(Frame1 adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton2_actionPerformed(e);
  }
}

class Frame1_jButton1_actionAdapter
    implements ActionListener {
  private Frame1 adaptee;
  Frame1_jButton1_actionAdapter(Frame1 adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

class Frame1_jMenuFileExit_ActionAdapter
    implements ActionListener {
  Frame1 adaptee;

  Frame1_jMenuFileExit_ActionAdapter(Frame1 adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.jMenuFileExit_actionPerformed(actionEvent);
  }
}

class Frame1_jMenuHelpAbout_ActionAdapter
    implements ActionListener {
  Frame1 adaptee;

  Frame1_jMenuHelpAbout_ActionAdapter(Frame1 adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.jMenuHelpAbout_actionPerformed(actionEvent);
  }
}
