package structural_similarity;

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
public class Edit_Distance {
  public Edit_Distance() {
  }
  //=====================================
  // determines index of non-letter characters in a given string and puts them in an interger array
  private int[] Delimiter_Places(String s){

    int i,j;
    int count=0;
    for(i=0; i<s.length(); i++)
      if(! (Character.isLetter(s.charAt(i)) || Character.isDigit(s.charAt(i))) )
        count++;
    int[] places = new int[count];
    j=0;
    for(i=0; i<s.length(); i++)
      if(! (Character.isLetter(s.charAt(i)) || Character.isDigit(s.charAt(i))) )
        places[j++] = i;
    return places;
  }

  //====================================
  // converts each string to a bag of words, considering non-letter characters as delimiter
 private String[] Initial_Bag(String s){

   int[] places = Delimiter_Places(s);
   String[] bag = new String[places.length+1];
   int i;
   for(i=0; i<bag.length; i++)
     bag[i]="";
   int b=0;
   int begin=0, end=0;
   //String temp = "";

   for(i=0; i<places.length; i++){
     end = places[i];
     bag[b] = s.substring(begin, end);
     b++;
     begin = end+1;
   }
   bag[b] = s.substring(begin);
   return bag;
 }
 //====================================

 private String[] Bag_Of_Words(String s){

   String[] init_bag = Initial_Bag(s);

   ////////////////////////////////////////////////
   // removing empty strings from the initial bag.//
   ////////////////////////////////////////////////
   int count =0;
   int i;
   for(i=0; i<init_bag.length; i++)
     if(! init_bag[i].matches(""))
       count++;
   String[] bag = new String[count];
   int j=0;
   for(i=0; i<init_bag.length; i++)
     if(! init_bag[i].matches(""))
       bag[j++] = init_bag[i];
   return bag;
 }

 //================================================
 // calculates lexical similarity between strings s and t based on their bag of words
 public float similarity(String s, String t){

   String[] bag_s = Bag_Of_Words(s);
   String[] bag_t = Bag_Of_Words(t);
   int i,j;
/*
   System.out.println("--------");
   for(i=0; i<bag_s.length; i++)
     System.out.println(bag_s[i]);
   for(j=0; j<bag_t.length; j++)
     System.out.println(bag_t[j]);
   System.out.println("--------");
*/

   for(i=0; i<bag_s.length; i++)
     for(j=0; j<bag_t.length; j++)
       if(bag_s[i].toLowerCase().matches(bag_t[j].toLowerCase())){
         bag_s[i] = "";
         bag_t[j] = "";
         break;
       }
   String temp1= "", temp2="";
   for(i=0; i<bag_s.length; i++)
     temp1 = temp1.concat(bag_s[i]);
   for(j=0; j<bag_t.length; j++)
     temp2 = temp2.concat(bag_t[j]);
//   System.out.println(temp1);
//   System.out.println(temp2);

   Levenshtein LD = new Levenshtein();
   int distance = LD.LDistance(temp1, temp2);
//   System.out.println("distance(s,t):" + distance);
   float sim = 1- (float)distance / Math.max(s.length(), t.length());
//   System.out.println("similarity(s,t):" + sim);
   return sim;
 }

}
