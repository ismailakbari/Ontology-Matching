package structural_similarity;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.util.iterator.*;
import javax.swing.JProgressBar;

public class Structural {
  public Structural() {

  }

  //----------------------------------
  public String path_convert(String filepath){

    filepath = filepath.replace('\\', '/');
    filepath = "file:/" + filepath;
    return filepath;
  }
  //---------------------------------------------
  // return number of named classes of a given model(ontology)
  private int NC_count(OntModel model){

    ExtendedIterator iter = model.listNamedClasses();
    return iter.toList().size();
  }
  //---------------------------------------------
  // Determines whether two classes from a same ontology are neighbor
  private boolean Is_Neighbor(OntClass class1, OntClass class2){

    if (class1.hasSubClass(class2, true) || class1.hasSuperClass(class2, true)||
        class1.hasEquivalentClass(class2)|| class1.isDisjointWith(class2) ||
        class2.hasEquivalentClass(class1)|| class2.isDisjointWith(class1))
      return true;

    ExtendedIterator iter = class1.listDeclaredProperties(true);
    OntProperty pro;
    while(iter.hasNext()){
      pro = (OntProperty)iter.next();
      if(pro.hasRange(class2))
        return true;
    }

    iter = class2.listDeclaredProperties(true);
    while(iter.hasNext()){
      pro = (OntProperty)iter.next();
      if(pro.hasRange(class1))
        return true;
    }
    return false;
  }
  //------------------------------------------------
  // calculates neighbor matrix of a given model(ontology)
  public int[][] N_Matrix(OntModel model){
    int i,j;
    ExtendedIterator iter1 = model.listNamedClasses();
    ExtendedIterator iter2 = model.listNamedClasses();
    OntClass class1, class2;
    int dim = iter1.toList().size();
    iter1 = model.listNamedClasses();

    int[][] matrix = new int[dim][dim];
    for(i=0; i<dim; i++)
      for(j=0; j<dim; j++)
        matrix[i][j] =0;

    for(i=0; i<dim; i++){
      class1 = (OntClass)iter1.next();
      //System.out.print(class1.getLocalName() + '\t');
      for(j=0; j<dim; j++){
        class2 = (OntClass)iter2.next();
        //System.out.print(class2.getLocalName() + ":");
        if(Is_Neighbor(class1, class2))
          matrix[i][j]=1;
        //System.out.println(matrix[i][j]);
      }
      iter2 = model.listNamedClasses();
    }
    return matrix;
  }
  //------------------------------------------------
  // calculates a linked list(3 dim array) which is an array of grids(2 dim arrays),
  // based on the neighbor matrix.
  public int[][][] linked_list(int[][] Neighbor){
    int len = Neighbor.length;
    int[][][] list = new int[len][][];
    int i;
    for(i=0; i< len; i++)
      list[i] = grid(Neighbor, i);
    return list;
  }
  //------------------------------------------------
  // this method generate a grid for ith node of the ontology based on the
  // neighbor matrix
  private int[][] grid(int[][] Neighbor, int row){
    int i,j,k,m,l,p,q;
    int N=0;
    for(i=0; i<Neighbor[row].length; i++)
      if(Neighbor[row][i]==1)
        N++;
    int[][] g = new int[N][];
    k=0;
    m=0;
    p=0;
    for(i=0; i<Neighbor[row].length; i++)
      if(Neighbor[row][i]==1){
        for(j=0; j<Neighbor[i].length; j++)
          if(Neighbor[i][j]==1)
            m++;
        g[k]= new int[m+1];
        g[k][0]=m;
        q=1;
        for(j=0; j<Neighbor[i].length; j++){
          if (Neighbor[i][j] == 1) {
            for (l = 0; l < Neighbor[j].length; l++)
              if (Neighbor[j][l] == 1)
                p++;
            g[k][q] = p;
            q++;
            p=0;
          }
        }
        k++;
        m=0;
      }// end if
    return g;
  }
  //--------------------------------------------------
  // returns similarity matrix between two 3 dim linked lists(ontologies)
  public float[][] LSimilarity(int[][][] list1, int[][][] list2){
    int len1 = list1.length;
    int len2 = list2.length;
    float[][] SMatrix = new float[len1][len2];
    int i,j;
    for(i=0; i<len1; i++)
      for(j=0; j<len2; j++)
        SMatrix[i][j] = 0;
    for(i=0; i<len1; i++)
      for(j=0; j<len2; j++)
        SMatrix[i][j] = GSimilarity(list1[i], list2[j]);
    return SMatrix;
  }
  //--------------------------------------------------
  // calculates similarity between two given nodes based on their grid(a 2 dim array)
  private float GSimilarity(int[][] grid1, int[][] grid2){
    int len1 = grid1.length;
    int len2 = grid2.length;

    if(len1==0 && len2==0)
      return 1.0F;

    int[] temp1 = new int[len1];
    int[] temp2 = new int[len2];
    int i,j;
    for(i=0; i<len1; i++)
      temp1[i] = grid1[i][0];
    for(i=0; i<len2; i++)
      temp2[i] = grid2[i][0];
    float p0 = Set_Sim(temp1, temp2);
    float[] p = new float[len1];
    float max;
    int[] temp3, temp4;
    int k;
    for(i=0; i<len1; i++){
      temp3 = new int[grid1[i].length-1]; // temp3 contains grid1[i] except its first element
      for(k=0; k<temp3.length; k++)
        temp3[k] = grid1[i][k+1];
      p[i]=0;
      max=0;
      for(j=0; j<len2; j++){
        temp4 = new int[grid2[j].length-1];
        for(k=0; k<temp4.length; k++)
          temp4[k] = grid2[j][k+1];
        max = Set_Sim(temp3, temp4);
        if(p[i]<max)
          p[i] = max;
      }
    }
    float prob = 0.0F;
    for(i=0; i<len1; i++)
      prob += p[i];
    prob += p0;
    return prob/(float)(1+len1);
  }
  //--------------------------------------------------
  // calculates similarity between two linear integer array
  public float Set_Sim(int[] set1, int[] set2){
    int len1 = set1.length;
    int len2 = set2.length;
    int i,j;
    int common =0;
    for(i=0; i<len1; i++)
      for(j=0; j<len2; j++)
        if(set1[i]==set2[j]){
          set2[j] = -2; // -2 in set2 shows that its corresponding element also exist in set1
          break;
        }
    for(i=0; i<len2; i++)
      if(set2[i]==-2)
        common++;
    float max = Math.max(len1, len2);
    return (float)common/max;
  }
  //-------------------------------------------------------
  // if two nodes A and A' are similar their neighbors' similarity will be increased by bias
  public float[][] EXT1(float[][] base, int[][] NS, int[][] NT, float threshold, float bias){
    float[][] base2 = new float[base.length][base[0].length];
    int[] srow, trow;
    int i,j,p,m;
    int r,s,r1=0,s1=0;
    float max;
    for(i=0; i<base.length; i++)
      for(j=0; j<base[i].length; j++)
        base2[i][j] = base[i][j];

    for(i=0; i<base.length; i++)
      for(j=0; j<base[i].length; j++)
        if(base[i][j]>= threshold){
          //i's neighbors
          m=0;
          for(p=0; p<NS[i].length; p++)
            if(NS[i][p]==1)
              m++;
          srow = new int[m];
          m=0;
          for(p=0; p<NS[i].length; p++)
            if(NS[i][p]==1)
              srow[m++] = p;
          //j's neighbors
          m=0;
          for(p=0; p<NT[j].length; p++)
            if(NT[j][p]==1)
              m++;
          trow = new int[m];
          m=0;
          for(p=0; p<NT[j].length; p++)
            if(NT[j][p]==1)
              trow[m++] = p;
          //-------
          // Detremining the most similar neighbors of the source and target nodes
          for(r=0; r<srow.length; r++){
            max=0;
            for(s=0; s<trow.length; s++)
              if(base[srow[r]][trow[s]]>max){
                max = base[srow[r]][trow[s]];
                r1 = srow[r];
                s1 = trow[s];
              }
            base2[r1][s1] += bias;
          }
        }
    return base2;
  }
  //---------------------------------------------------------------
  // if two nodes A and A' have n matched neighbors, their similarity will be increased by n*bias
  public float[][] EXT2(float[][] base, int[][] NS, int[][] NT, float threshold, float bias){
    int i, j, p, r, s;
    int m, matched;
    int[] srow, trow;
    float[][] base2 = new float[base.length][base[0].length];
    for(i=0; i<base.length; i++)
      for(j=0; j<base[i].length; j++)
        base2[i][j] = base[i][j];

    // for all source nodes
    for(i=0; i<NS.length; i++){
      //i's neighbors
      m=0;
      for(p=0; p<NS[i].length; p++)
        if(NS[i][p]==1)
          m++;
      srow = new int[m];
      m=0;
      for(p=0; p<NS[i].length; p++)
        if(NS[i][p]==1)
          srow[m++] = p;
      // for all target nodes
      for(j=0; j<NT.length; j++){
        //j's neighbors
          m=0;
          for(p=0; p<NT[j].length; p++)
            if(NT[j][p]==1)
              m++;
          trow = new int[m];
          m=0;
          for(p=0; p<NT[j].length; p++)
            if(NT[j][p]==1)
              trow[m++] = p;
          //-------
          matched = 0;
          for(r=0; r<srow.length; r++)
            for(s=0; s<trow.length; s++)
              if(base[srow[r]][trow[s]]>= threshold)
                matched++;
          base2[i][j] += bias*matched;
      }
    }
    return base2;
  }
  //--------------------------------------------------------
  // sums base similarity matrix with calculsted data type properties similarity matrix
  public float[][] EXT3(float[][] base, float[][] DTM){
    int i,j;
    float[][] base2 = new float[base.length][base[0].length];
    for(i=0; i<base.length; i++)
      for(j=0; j<base[i].length; j++)
        base2[i][j] = base[i][j];

    for(i=0; i<base.length; i++)
      for(j=0; j<base[i].length; j++)
        base2[i][j] = base[i][j]+ DTM[i][j];
    return base2;
  }
  //-----------------------------------------------------
  //if two nodes A nad A' have n common data type properties, then their similarity
  // will be increased by n*bias; return similarity matirx of two ontologies
  //based on their common data type properties
  public float[][] DTM(OntModel model1, OntModel model2, float bias){
    int i,j,common=0;
    ExtendedIterator iter1 = model1.listNamedClasses();
    ExtendedIterator iter2 = model2.listNamedClasses();
    int row = iter1.toList().size();
    int col = iter2.toList().size();
    float[][] DTMatrix = new float[row][col];
    for(i=0; i<DTMatrix.length; i++)
      for(j=0; j<DTMatrix[i].length; j++)
        DTMatrix[i][j]=0;

    iter1 = model1.listNamedClasses();
    iter2 = model2.listNamedClasses();
    OntClass class1,class2;
    for(i=0; i<row; i++){
      class1 =(OntClass) iter1.next();
      for(j=0; j<col; j++){
        class2 = (OntClass)iter2.next();
        common = CommonDTP(class1, class2);
        DTMatrix[i][j] = common*bias;
      }
      iter2 = model2.listNamedClasses();
    }
    return DTMatrix;
  }
  //---------------------------------------------------
  // determine how many data type properties two classes have in common
  private int CommonDTP(OntClass class1, OntClass class2){
    ExtendedIterator iter1 = class1.listDeclaredProperties(true);
    ExtendedIterator iter2 = class2.listDeclaredProperties(true);
    String s1="", s2="";
    OntProperty pro1, pro2;
    int common=0;
    while(iter1.hasNext()){
      pro1 = (OntProperty) iter1.next();
      if(pro1.isDatatypeProperty()){
        s1 = pro1.getLocalName();
        while(iter2.hasNext()){
          pro2 = (OntProperty)iter2.next();
          if(pro2.isDatatypeProperty()){
            s2 = pro2.getLocalName();
            if(s1.toLowerCase().equals(s2.toLowerCase()))
              common++;
          }
        }
      }
      iter2 = class2.listDeclaredProperties(true);
    }
    return common;
  }
  //------------------------------------------------------------
  public float[][] struct_sim(OntModel model1, OntModel model2, JProgressBar jProgressBar1){
    Frame1 frame1 = new Frame1();
    int[][] neighbor1 = N_Matrix(model1);
    jProgressBar1.setValue(25);
    jProgressBar1.update(jProgressBar1.getGraphics());
    int[][] neighbor2 = N_Matrix(model2);

    int i,j;
    jProgressBar1.setValue(28);
    jProgressBar1.update(jProgressBar1.getGraphics());
    int[][][] list1 = linked_list(neighbor1);
    int[][][] list2 = linked_list(neighbor2);

    jProgressBar1.setValue(31);
    jProgressBar1.update(jProgressBar1.getGraphics());
    float[][] base = LSimilarity(list1, list2);
    jProgressBar1.setValue(35);
    jProgressBar1.update(jProgressBar1.getGraphics());
    float [][] ext1 = EXT1(base, neighbor1, neighbor2,
                                Float.valueOf(frame1.jTextField1.getText()),
                                Float.valueOf(frame1.jTextField2.getText()));
    jProgressBar1.setValue(40);
    jProgressBar1.update(jProgressBar1.getGraphics());
    float [][] ext2 = EXT2(ext1, neighbor1, neighbor2,
                                Float.valueOf(frame1.jTextField1.getText()),
                                Float.valueOf(frame1.jTextField2.getText()));
    jProgressBar1.setValue(45);
    jProgressBar1.update(jProgressBar1.getGraphics());
    float [][] DTM = DTM(model1, model2, Float.valueOf(frame1.jTextField2.getText()));
    jProgressBar1.setValue(50);
    jProgressBar1.update(jProgressBar1.getGraphics());
    float [][] ext3 = EXT3(ext2, DTM);
    jProgressBar1.setValue(55);
    jProgressBar1.update(jProgressBar1.getGraphics());
    return ext3;
  }
}//END OF CLASS
