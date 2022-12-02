
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
   MTGSearch.java
   
   Produces a search query based on the mtgCards.txt card list
   
   @author Peter Olson
   @version 12/20/21
   @see mtgCards.txt
   @see mtgWordList.xlsx
*/
public class MTGSearch {

   private static final String TEXT_FILE = "./mtgCards.txt";
   private static final String WORD_FILE = "./mtgWordList.txt";
   
   private static Scanner scanner = new Scanner( System.in );
   
   /**
      Handle which program to run
      
      @param args Not used
      @see searchFromUser()
   */
   public static void main( String[] args ) {
      searchFromUser();
   }
   
   /**
      Produces a query of mtg cards from the mtg list
      
      @see MTGQuery.java
      @see printHelp()
      @see MTGQuery.findList( String userInput )
      @see MTGQuery.printList()
   */
   private static void searchFromUser() {
      boolean isHelping = false;
      MTGQuery query = null;
      String line = "";
      do {
         SOPln("\nWhat properties are you searching for?\nEnter 'help' for a list of suggestions.");
         line = scanner.nextLine();
         
         if( line.equals("help") || line.equals("Help") ) {
            printHelp();
            isHelping = true;
         } else if( line.equals("all") || line.equals("All") ) {
            printAllProperties();
            isHelping = true;
         } else {
            query = new MTGQuery( TEXT_FILE );
            isHelping = false;
         }
      
      } while( isHelping );
      
      query.findList( line );
      
      query.printList();
   }
   
   /**
      Prints helpful suggestions for creating an MTGQuery list
      
      @see searchFromUser()
   */
   private static void printHelp() {
      SOPln("MTGSearch processes descriptions to find cards within the " + TEXT_FILE + " list" +
            " that meet the criteria input.\n\nConsider the following suggestions for generating lists" +
            ", or enter 'all' to see the exact criteria inputs for exact searching:" +
            "\n\n\t1) Specify card type, such as 'creature', 'sorcery', 'instant', etc" +
            "\n\t2) Specify color type, either being red, blue, green, white, black," + "\n\t    colorless, or no color" +
            "\n\t3) Specify abilities required, including keyword properties, " + "\n\t   such as 'trample', 'flying', etc" +
            "\n\t4) Specify any qualities you want excluded, by saying 'excludes'\n\t   followed by the desired" +
            " properties to be excluded" +
            "\n\t5) Queries can also search by subtype, mana value, rarity,\n\t   set, artist, print year, " +
            "power, toughness, etc");
   }
   
   /**
      Get all the properties as a 2D array of Strings, sorted by category
      
      @return String[][] The sorted list of properties
      @see printAllProperties()
   */
   public static String[][] getAllProperties() {
      return printAllProperties();
   }
   
   /**
      Prints all the properties from the Excel file mtgWordList.xlsx
      
      @return String[][] The list sorted by column of properties
      @see searchFromUser()
      @see removeNonAlphanumericWhitespaceAndAnd( String str )
      @see mtgWordList.xlsx
      @fix mtgWordList.txt
   */
   private static String[][] printAllProperties() {
      File wordList = new File( WORD_FILE );
      Scanner scanner = null;
      try {
         scanner = new Scanner( wordList );
      } catch( IOException e ) {
         e.printStackTrace();
      }
      
      ArrayList<String[]> unsortedList = new ArrayList<String[]>();
      
      int maxLength = 0;
      int lineNumber = 0;
      
      //Gather all lines
      while( scanner.hasNextLine() ) {
         
         /* replace all special characters, leaving letters, numbers, and spaces behind
            Then split by white space characters */
         String line = scanner.nextLine();
         
         //skip empty lines
         if( line.length() <= 1 ) continue;
         
         line = removeNonAlphanumericWhitespaceAndAnd( line.replace("\0", "") );
         
         String[] lineList = line.split("\\s");
         lineList = substituteEmpties( lineList );
         
         if( lineNumber == 0 ) maxLength = lineList.length;
         
         String[] fullList = addEmpties( lineList, maxLength );
         
         unsortedList.add( fullList );
         
         //@@DEBUG: printArray( fullList );
         
         lineNumber++;
      }
      
      String[][] sortedList = sortByColumn( unsortedList );
      printArray( sortedList );
      
      return sortedList;
   }
   
   /**
      Adds "empty" tokens to each list to extend the length of the list to the maxium length
      
      @param list The original list
      @param maxLength The total length to extend the original list to
      @return String[] The new list, now with empties extending to the max length in total tokens
   */
   private static String[] addEmpties( String[] list, int maxLength ) {
      String[] newList = new String[ maxLength ];
      int originalLength = list.length;
      for( int i = 0; i < maxLength; i++ ) {
         if( i < originalLength )
            newList[i] = list[i];
         else
            newList[i] = "empty";
      }
      
      return newList;
   }
   
   /**
      Substitude empty Strings with the word "empty"
      
      @param list The list to make substitutions
      @return String[] The list with all empty Strings replaced with the Words "empty"
      @see printAllProperties()
   */
   private static String[] substituteEmpties( String[] list ) {
      for( int i = 0; i < list.length; i++ ) {
         if( list[i].equals("") )
            list[i] = "empty";
      }
      return list;
   }
   
   /**
      Translates array in row format that has column patterned assortment into
      an array that is sorted in rows patterned by their assortment
      
      @param list The list to sort
      @return String[] The new list, which is now sorted by properties organized in rows
      @see printAllProperties()
   */
   private static String[][] sortByColumn( ArrayList<String[]> list ) {
      final int MAX_LENGTH = list.get(0).length;
      final int TOTAL_ROWS = list.size();
      String[][] listByColumn = new String[ MAX_LENGTH ][ TOTAL_ROWS ];
      
      for( int col = 0; col < MAX_LENGTH; col++ ) {
         for( int row = 0; row < list.size(); row++ ) {
            if( !list.get( row )[ col ].equals("empty") )
               listByColumn[ col ][ row ] = list.get( row )[ col ];
            else
               break;
         }
      }
      
      return listByColumn;
   }
   
   /**
      Print String array
      
      @param list The list of Strings to print
      @see printAllProperties()
   */
   private static void printArray( String[] list ) {
      if( list.length <= 0 || list[0].equals("") )
         return;
      
      String line = "( ";
      for( int i = 0; i < list.length; i++ ) {
         if( i != list.length - 1 && list[i] != null )
            line += list[i] + ", ";
         else if( i == list.length - 1 && list[i] != null )
            line += list[i] + " )";
         else if( i == list.length - 1 && list[i] == null )
            line += " )";
      }
      
      if( line.endsWith(",  )") ) line = line.replace(",  )", " )");
      
      SOPln( line );
   }
   
   /**
      Print String[][] array
      
      @param list The 2D String list to print
      @see printAllProperties()
      @see printArray( String[] )
      
   */
   private static void printArray( String[][] list ) {
      for( int i = 0; i < list.length; i++ )
         printArray( list[i] );
   }
   
   /**
      Print int array
      
      @param list The list of Strings to print
      @see printAllProperties()
   */
   private static void printArray( int[] list ) {
         
      SOP("( ");
      for( int i = 0; i < list.length; i++ ) {
         if( i != list.length - 1 )
            SOP( list[i] + ", " );
         else
            SOP( list[i] + " )" );
      }
      SOPln();
   }
   
   /**
      Remove all characters that are not letters
      
      @param str The String to edit
      @return String The String without nonletter characters
   */
   public static String removeNonLetters( String str ) {
      return str.replaceAll("[^a-zA-Z]+", "");
   }
   
   /**
      Remove all characters that are not numbers
      
      @param str The String to edit
      @return String The String without nonnumber characters
   */
   public static String removeNonNumbers( String str ) {
      return str.replaceAll("[^0-9]+", "");
   }
   
   /**
      Remove all characters that are not letters or numbers
      
      @param str The String to edit
      @return String The String without non-alphanumeric characters
   */
   public static String removeNonAlphanumeric( String str ) {
      return str.replaceAll("[^a-zA-Z0-9]+", "");
   }
   
   /**
      Remove all characters that are not letters or numbers or whitespace
      
      @param str The String to edit
      @return String The String without nonletters, nonnumbers, or non-whitespace characters,
                     or non-ampersands
   */
   public static String removeNonAlphanumericWhitespace( String str ) {
      return str.replaceAll("[^a-zA-Z0-9\\s]+", "");
   }
   
   /**
      Remove all characters that are not letters or numbers or whitespace
      
      @param str The String to edit
      @return String The String without nonletters, nonnumbers, or non-whitespace characters,
                     or non-ampersands
   */
   public static String removeNonAlphanumericWhitespaceAndAnd( String str ) {
      return str.replaceAll("[^a-zA-Z0-9\\s&]+", "");
   }
   
   /**
      Remove all characters that are not letters or not whitespace
      
      @param str The String to edit
      @return String The String without nonletter, non-whitespace characters
   */
   public static String removeNonLettersWhitespace( String str ) {
      return str.replaceAll("[^a-zA-Z\\s]+", "");
   }
   
   /**
      Remove all characters that are not numbers or not whitespace
      
      @param str The String to edit
      @return String The String without non-numeric, non-whitespace characters
   */
   public static String removeNonNumbersWhitespace( String str ) {
      return str.replaceAll("[^0-9\\s]+", "");
   }
   
   /**
      Faster method override
      
      @param str The String to print
   */
   private static void SOPln( String str ) {
      System.out.println( str );
   }
   
   /**
      Faster method override
   */
   private static void SOPln() {
      System.out.println();
   }
   
   /**
      Faster method override
      
      @param str The String to print
   */
   private static void SOP( String str ) {
      System.out.print( str );
   }
}