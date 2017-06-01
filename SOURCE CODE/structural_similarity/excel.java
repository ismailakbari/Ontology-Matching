package structural_similarity;

import java.io.IOException;
import java.io.File;
import java.util.Date;

import jxl.write.*;
import jxl.*;

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
public class excel {
  public excel() {
  }

  //==============================================
  // RETURNS LOCAL NAMES IN THE GIVEN ITERATOR
  // TYPE : 1 ---->  CLASS
  // TYPE : 2 ---->  OBJECT PROPERTIES
  // TYPE : 3 ---->  DATA PROPERTIES
  //==============================================
  private String[] getnames(ExtendedIterator iter, int size, int type){

    String[] names = new String[size];
    int i=0;
    switch (type){
      case 1:
        OntClass class1;
        for(i=0; i< size; i++){
          class1 = (OntClass) iter.next();
          if(class1.getLocalName()!=null)
            names[i] = class1.getLocalName();
          else
            names[i] = "";
        }
        break;
      case 2:
        ObjectProperty op;
        for(i=0; i<size; i++){
          op = (ObjectProperty) iter.next();
          if(op.getLocalName()!=null)
            names[i] = op.getLocalName();
          else
            names[i] = "";
        }
        break;
      case 3:
        DatatypeProperty dp;
        for(i=0; i<size; i++){
          dp = (DatatypeProperty) iter.next();
          if(dp.getLocalName()!=null)
            names[i] = dp.getLocalName();
          else
            names[i] = "";
        }
        break;

    }// end switch
    return names;
  }

  //=================================================//
  // SENDS OUTPUT ARRAYS TO THE "C:\OUTPUT.XLS" FILE //
  //=================================================//
  public boolean output(float[][] nodes, float[][] op, float[][] dp,
                        ExtendedIterator cnames1, ExtendedIterator cnames2,
                        ExtendedIterator opnames1, ExtendedIterator opnames2,
                        ExtendedIterator dpnames1, ExtendedIterator dpnames2, int[] itersize, float threshold){

    int row1,col1, row2,col2,row3,col3;
    row1 = nodes.length;
    col1 = nodes[0].length;
    row2 = op.length;
    col2 = op[0].length;
    row3 = dp.length;
    col3 = dp[0].length;
    String[] cn1 =  new String[row1];
    String[] cn2 =  new String[col1];
    String[] opn1 =  new String[row2];
    String[] opn2 =  new String[col2];
    String[] dpn1 =  new String[row3];
    String[] dpn2 =  new String[col3];
    cn1 =  getnames(cnames1, itersize[0], 1);
    cn2 =  getnames(cnames2, itersize[1], 1);
    opn1 = getnames(opnames1, itersize[2], 2);
    opn2 = getnames(opnames2, itersize[3], 2);
    dpn1 = getnames(dpnames1, itersize[4], 3);
    dpn2 = getnames(dpnames2, itersize[5], 3);


    try {

      WritableWorkbook workbook = Workbook.createWorkbook(new File("c:\\output.xls"));
      WritableSheet activesheet = workbook.createSheet("Results", 0);

      WritableFont wf = new WritableFont(WritableFont.TAHOMA, 14, WritableFont.BOLD);
      WritableCellFormat wcf1 = new WritableCellFormat(wf);
      WritableFont wf2 = new WritableFont(WritableFont.TAHOMA, 12, WritableFont.BOLD);
      WritableCellFormat wcf2 = new WritableCellFormat(wf2);
      //NumberFormat nf = new NumberFormat("#.###");
      //WritableCellFormat wcf2 = new WritableCellFormat(nf);

      //=======================
      // WRITING CLASS MATIRX//
      //=====================//

      jxl.write.Label label1 = new jxl.write.Label((col1/2)+1, 0, "CLASS SIMILARITY", wcf1);
      activesheet.addCell(label1);
      int i, j;
      for(i=0; i<row1; i++){
        jxl.write.Label name1 = new jxl.write.Label(0, i+3, cn1[i]);
        activesheet.addCell(name1);
      }
      for(i=0; i<col1; i++){
        jxl.write.Label name2 = new jxl.write.Label(i+3, 2, cn2[i]);
        activesheet.addCell(name2);
      }

      for (i = 0; i <nodes.length; i++)
        for(j=0; j<nodes[0].length; j++){
            if(nodes[i][j]>= threshold){
              jxl.write.Number number = new jxl.write.Number(j + 3, i + 3,
                  nodes[i][j], wcf2);
              activesheet.addCell(number);
            }
            else{
              jxl.write.Number number = new jxl.write.Number(j + 3, i + 3,
                  nodes[i][j]);
              activesheet.addCell(number);
            }
      }

      //=======================
      // WRITING OBJECT PROPERTIES MATIRX//
      //=====================//
      jxl.write.Label label2 = new jxl.write.Label( (col1 + 3+5) + (col2 / 2) + 1, 0,
                                                 "OBJECT PROPERTY SIMILARITY", wcf1);
      activesheet.addCell(label2);
      for (i = 0; i < row2; i++) {
        jxl.write.Label name3 = new jxl.write.Label(col1 + 3+5, i + 3, opn1[i]);
        activesheet.addCell(name3);
      }
      for (i = 0; i < col2; i++) {
        jxl.write.Label name4 = new jxl.write.Label(col1 + 3+5+i+ 3, 2, opn2[i]);
        activesheet.addCell(name4);
      }

      for (i = 0; i < op.length; i++)
        for (j = 0; j < op[0].length; j++) {
          if(op[i][j]>=threshold){
            jxl.write.Number number = new jxl.write.Number(col1 + 3 + 5 + j + 3,
                i + 3, op[i][j], wcf2);
            activesheet.addCell(number);
          }
          else{
            jxl.write.Number number = new jxl.write.Number(col1 + 3 + 5 + j + 3,
                i + 3, op[i][j]);
            activesheet.addCell(number);

          }
        }
      //=======================
      // WRITING DATA PROPERTIES MATIRX//
      //=====================//
      jxl.write.Label label3 = new jxl.write.Label( (col1 + 3+5)+(col2 + 3+5) + (col3 / 2) + 1, 0,
                                                   "DATA PROPERTY SIMILARITY", wcf1);
      activesheet.addCell(label3);
      for (i = 0; i < row3; i++) {
        jxl.write.Label name5 = new jxl.write.Label((col1 + 3+5)+(col2 + 3+5), i + 3, dpn1[i]);
        activesheet.addCell(name5);
      }
      for (i = 0; i < col3; i++) {
        jxl.write.Label name6 = new jxl.write.Label((col1 + 3+5)+(col2 + 3+5)+i + 3, 2, dpn2[i]);
        activesheet.addCell(name6);
      }

      for (i = 0; i < dp.length; i++)
        for (j = 0; j < dp[0].length; j++) {
          if(dp[i][j]>= threshold){
            jxl.write.Number number = new jxl.write.Number( (col1 + 3 + 5) +
                (col2 + 3 + 5) + j + 3, i + 3, dp[i][j], wcf2);
            activesheet.addCell(number);
          }
          else{
            jxl.write.Number number = new jxl.write.Number( (col1 + 3 + 5) +
                (col2 + 3 + 5) + j + 3, i + 3, dp[i][j]);
            activesheet.addCell(number);

          }
        }
      //---------
      workbook.write();
      workbook.close();
    }
    catch (IOException ioe) {
      System.out.println(ioe.getMessage());
    }
    catch (WriteException we) {
      System.out.println(we.getMessage());
    }
    catch (JXLException je) {
      System.out.println(je.getMessage());
    }

    return true;
  }
}
