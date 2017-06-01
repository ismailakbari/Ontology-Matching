package structural_similarity;

import com.hp.hpl.jena.ontology.*;
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
public class overall {
  public overall() {
  }
 //===================================================
 // combine structural and lexical matrices of classes
  public float[][] class_sim(float[][] struct, float[][] lexical){

    if((struct.length!= lexical.length)||(struct[0].length!= lexical[0].length))
      return null;
    int i,j;
    float sum1=0;
    float sum2=0;

    // determining that the 2 ontologies are more structurally similar or more lexically
    int flag =2;
    for(i=0;i<struct.length; i++)
      for(j=0;j<struct[i].length; j++)
        sum1 += struct[i][j];
    for(i=0;i<lexical.length; i++)
      for(j=0;j<lexical[i].length; j++)
        sum2 += lexical[i][j];
    if(sum1>sum2)
      flag=1;
    float sim[][] = new float[struct.length][struct[0].length];
    if(flag==1)
      for(i=0;i<sim.length; i++)
      for(j=0;j<sim[i].length; j++)
        sim[i][j] = (0.6F * struct[i][j])+ (0.4F * lexical[i][j]);
    else
      for(i=0;i<sim.length; i++)
      for(j=0;j<sim[i].length; j++)
        sim[i][j] = (0.4F * struct[i][j])+ (0.6F * lexical[i][j]);
    return sim;
  }

  //==================================================
  // if 2 object properties have matched domains and/or ranges then their similarity will be increased by the bias value
  public float[][] op_sim(float[][] op, float[][] cm, OntModel model1, OntModel model2, float threshold, float bias){


    int i,j;
    int row = op.length;
    int col = op[0].length;
    float[][] new_op = new float[row][col];
    for(i=0; i<new_op.length; i++)
      for(j=0; j<new_op[i].length; j++)
        new_op[i][j]=op[i][j];

    ExtendedIterator iter1 = model1.listObjectProperties();
    ExtendedIterator iter2 = model2.listObjectProperties();
    ObjectProperty prop1,prop2;
    String domain1="";
    String domain2="";
    int c1=-1, c2=-1;

    for(i=0; i<row; i++){
      prop1 =(ObjectProperty) iter1.next();
      domain1 = domain_name(prop1);
      c1 = class_index(model1, domain1);
      for(j=0; j<col; j++){
        prop2 = (ObjectProperty) iter2.next();
        domain2 = domain_name(prop2);
        c2 = class_index(model2, domain2);
        if(c1>=0 && c1 <= cm.length && c2>=0 && c2<=cm[0].length)
          if(cm[c1][c2]>= threshold)
            new_op[i][j] += bias;
      }
      iter2 = model2.listObjectProperties();
    }

    iter1 = model1.listObjectProperties();
    iter2 = model2.listObjectProperties();
    c1=-1; c2=-1;
    String range1="", range2="";

    for(i=0; i<row; i++){
      prop1 =(ObjectProperty) iter1.next();
      range1 = range_name(prop1);
      c1 = class_index(model1, range1);
      for(j=0; j<col; j++){
        prop2 = (ObjectProperty) iter2.next();
        range2 = range_name(prop2);
        c2 = class_index(model2, range2);
        if(c1>=0 && c1 <= cm.length && c2>=0 && c2<=cm[0].length)
          if(cm[c1][c2]>= threshold)
            new_op[i][j] += bias;
      }
      iter2 = model2.listObjectProperties();
    }
    return new_op;
  }

  //==================================================
  private int class_index(OntModel model, String name){
    ExtendedIterator iter = model.listNamedClasses();
    int index=-1;
    boolean flag = false;
    OntClass class2;
    while(iter.hasNext() && ! flag){
      class2 =(OntClass) iter.next();
      index++;
      if(class2.getLocalName().toLowerCase().matches(name.toLowerCase()))
        flag = true;
    }
    if (flag)
      return index;
    return -1;
  }
  //==================================================
  // THIS METHOD JUST RETURNS ONE OF THE OBJECT PROPERTY DOMAINS.
  // IT NEEEDS IMPROVEMENT
  private String domain_name(OntProperty op){
    ExtendedIterator iter = op.listDeclaringClasses(true);
    OntClass class1;
    while(iter.hasNext()){
      class1 = (OntClass) iter.next();
      if(class1.getLocalName()!= null && class1.getLocalName()!= "")
        return class1.getLocalName();
    }
    return "";
  }
  //==================================================
  // THIS METHOD JUST RETURNS ONE OF THE OBJECT PROPERTY RANGE.
  // IT NEEEDS IMPROVEMENT
  private String range_name(ObjectProperty op){
    ExtendedIterator iter = op.listRange();
    OntClass class1;
    //String range="";
    //iter.n
    while(iter.hasNext()){
      class1 = (OntClass) iter.next();
      if(class1.getLocalName()!= null && class1.getLocalName()!= "")
        return class1.getLocalName();
    }
    return "";
  }
  //==================================================
  // THIS METHOD JUST RETURNS ONE OF THE DATA TYPE PROPERTY RANGE.
  // IT NEEEDS IMPROVEMENT
  private String range_name(DatatypeProperty dp){
    ExtendedIterator iter = dp.listRange();
    OntResource res1;
    while(iter.hasNext()){
      res1 = (OntResource) iter.next();
      if(res1.getLocalName()!= null && res1.getLocalName()!= "")
      return res1.getLocalName();
    }
    return "";
  }

  //==================================================
    // if 2 data properties have matched domains and/or same ranges then their similarity will be increased by the bias value
    public float[][] dp_sim(float[][] dp, float[][] cm, OntModel model1, OntModel model2, float threshold, float bias){


      int i,j;
      int row = dp.length;
      int col = dp[0].length;
      float[][] new_dp = new float[row][col];
      for(i=0; i<new_dp.length; i++)
        for(j=0; j<new_dp[i].length; j++)
          new_dp[i][j]=dp[i][j];

      ExtendedIterator iter1 = model1.listDatatypeProperties();
      ExtendedIterator iter2 = model2.listDatatypeProperties();
      DatatypeProperty data1,data2;
      OntClass class1, class2;
      String domain1="", domain2="";
      int c1=-1, c2=-1;

      for(i=0; i<row; i++){
        data1 =(DatatypeProperty) iter1.next();
        domain1 = domain_name(data1);
        c1 = class_index(model1, domain1);
        for(j=0; j<col; j++){
          data2 = (DatatypeProperty) iter2.next();
          domain2 = domain_name(data2);
          c2 = class_index(model2, domain2);
          if(c1>=0 && c1 <= cm.length && c2>=0 && c2<=cm[0].length)
            if(cm[c1][c2]>= threshold)
              new_dp[i][j] += bias;
        }
        iter2 = model2.listDatatypeProperties();
      }

      iter1 = model1.listDatatypeProperties();
      iter2 = model2.listDatatypeProperties();
      String s1="", s2="";

      for(i=0; i<row; i++){
        data1 =(DatatypeProperty) iter1.next();
        //s1 = data1.getRange().getLocalName();
        s1 = range_name(data1);
        for(j=0; j<col; j++){
          data2 =(DatatypeProperty) iter2.next();
          //s2 = data2.getRange().getLocalName();
          s2 = range_name(data2);
          if(s1.toLowerCase().matches(s2.toLowerCase()))
            new_dp[i][j] += bias;
        }
        iter2 = model2.listDatatypeProperties();
      }
      return new_dp;
    }

}
