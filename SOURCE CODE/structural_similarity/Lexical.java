package structural_similarity;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.*;


/**
 * <p>Title: Ontology Matching Tool</p>
 *
 * <p>Description: A Combined Lexical and Structural Ontology Matching Tool</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author Ismail Akbari
 * @version 1.0
 */
public class Lexical {
  public Lexical() {
  }

  //==================================
  // lexical similarity among named classes of source and target ontologies
  // based using Edit_Distance class
  //===================================
  public float[][] class_sim(OntModel model1, OntModel model2){

    ExtendedIterator iter1 = model1.listNamedClasses();
    ExtendedIterator iter2 = model2.listNamedClasses();
    int i,j;
    int row = iter1.toList().size();
    int col = iter2.toList().size();
    float[][] cm = new float[row][col];
    for(i=0; i<cm.length; i++)
      for(j=0; j<cm[i].length; j++)
        cm[i][j]=0.0F;

    iter1 = model1.listNamedClasses();
    iter2 = model2.listNamedClasses();
    OntClass class1,class2;
    String s="", t="";
    Edit_Distance ed = new Edit_Distance();
    //float sim =0.0F;
    for(i=0; i<row; i++){
      class1 =(OntClass) iter1.next();
      s= class1.getLocalName();
      for(j=0; j<col; j++){
        class2 = (OntClass)iter2.next();
        t = class2.getLocalName();
        cm[i][j] = ed.similarity(s,t);
      }
      iter2 = model2.listNamedClasses();
    }
    return cm;
  }
  //==================================
  // lexical similarity among object properties of source and target ontologies
  // based using Edit_Distance class
  public float[][] op_sim(OntModel model1, OntModel model2){

    ExtendedIterator iter1 = model1.listObjectProperties();
    ExtendedIterator iter2 = model2.listObjectProperties();
    int i,j;
    int row = iter1.toList().size();
    int col = iter2.toList().size();
    float[][] op = new float[row][col];
    for(i=0; i<op.length; i++)
      for(j=0; j<op[i].length; j++)
        op[i][j]=0.0F;

    iter1 = model1.listObjectProperties();
    iter2 = model2.listObjectProperties();
    ObjectProperty prop1, prop2;
    String s="", t="";
    Edit_Distance ed = new Edit_Distance();

    for(i=0; i<row; i++){
      prop1 =(ObjectProperty) iter1.next();
      s= prop1.getLocalName();
      for(j=0; j<col; j++){
        prop2 = (ObjectProperty)iter2.next();
        t = prop2.getLocalName();
        op[i][j] = ed.similarity(s,t);
      }
      iter2 = model2.listObjectProperties();
    }
    return op;
  }
  //==================================
    // lexical similarity among data properties of source and target ontologies
    // based using Edit_Distance class
    public float[][] dp_sim(OntModel model1, OntModel model2){

      ExtendedIterator iter1 = model1.listDatatypeProperties();
      ExtendedIterator iter2 = model2.listDatatypeProperties();
      int i,j;
      int row = iter1.toList().size();
      int col = iter2.toList().size();
      float[][] dp = new float[row][col];
      for(i=0; i<dp.length; i++)
        for(j=0; j<dp[i].length; j++)
          dp[i][j]=0.0F;

      iter1 = model1.listDatatypeProperties();
      iter2 = model2.listDatatypeProperties();
      DatatypeProperty data1,data2;
      String s="", t="";
      Edit_Distance ed = new Edit_Distance();
      //float sim =0.0F;
      for(i=0; i<row; i++){
        data1 =(DatatypeProperty) iter1.next();
        s= data1.getLocalName();
        for(j=0; j<col; j++){
          data2 = (DatatypeProperty)iter2.next();
          t = data2.getLocalName();
          dp[i][j] = ed.similarity(s,t);
        }
        iter2 = model2.listDatatypeProperties();
      }
      return dp;
  }
}
