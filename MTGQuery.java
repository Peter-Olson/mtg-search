
import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Arrays;

/**
   MTGQuery.java
   
   Processes user input and creates the list of cards found after searching
   
   @author Peter Olson
   @version 1/2/22
   @see MTGSearch.java
   @see MTGCard.java
*/
public class MTGQuery {
   
   private File mtgFile;
   private Scanner scanner = null;
   
   private ArrayList<MTGCard> cardList;
   
   private final String MTG_KEYWORD_SEARCH_FILE_LOC = "./mtgKeywordSearchList.txt";
   
   /**
      Create a query object that will store the list of cards found after processing
      and searching the input specifications
      
      @param fileName The name of the file to query. See 'mtgCards.txt'
   */
   public MTGQuery( String fileName ) {
      try {
         mtgFile = new File( fileName );
         scanner = new Scanner( mtgFile );
      } catch( IOException e ) {
         e.printStackTrace();
      }
      
      cardList = new ArrayList<MTGCard>();
      scanner.nextLine(); //skip first line
      int lineNumber = 1;
      
      //Add MTGCards to list
      while( scanner.hasNextLine() ) {
         //set base properties
         String line = scanner.nextLine().trim();
         String[] baseProperties = line.split("\\|");
         
         //set image name
         String imageName = baseProperties[1] + ".jpg";
         
         //check that the properties exist for this card
         if( !scanner.hasNextLine() ) throw new NoSuchElementException("Expected additional line for base properties");
         
         //set properties
         String propertiesLine = scanner.nextLine().trim();
         String[] properties = propertiesLine.split(",");
         
         //set and add MTGCard
         MTGCard card = new MTGCard( baseProperties, imageName, properties );
         cardList.add( card );
         
         //advance card #
         lineNumber++;
      }
      
      //@@DEBUG
      /*
      sortByName( cardList );
      printAllCardsWithProperties();
      */
   }
   
   /**
      Generates the list of cards given the user input.
      
      @param userInput The line entered from the user to be processed
   */
   public void findList( String userInput ) {
      
      //set valid String properties keys from input data
      ArrayList<String> validProperties = translateUserInput( userInput );
      
      //get leftover String values for base property evaluation
      int sizeOfList = validProperties.size();
      String leftOvers = validProperties.get( sizeOfList - 1 );
      validProperties.remove( sizeOfLife - 1 );
      
      //@@DEBUG
      SOPln("\nProperties Found:");
      printList( validProperties );
      
      //get cards that have all properties from query
      
   }
   
   /**
      Processes the user input using language processesing techniques. This algorithm
      uses hashing techniques to narrow down searching to O(nlogn), approximately, similar
      to methods used in Merge/Heap/Quick sort.
      
      This is done in four parts:
      1) Spaces and punctuation is removed from user input. All letters are capitalized
      2) A key is found in the user input, and uses a HashMap<ArrayList<String>> for multi-tiered values
      3) Each set of values is searched against the input. Values that match are removed from the original input
         and their keywordProperties are stored in a list
      4) The list of keywords is returned, and the leftover tokens from the user input are concatenated and added
         to the end of the list
      
      @param userInput The input from the user for finding MTG cards that have the inferred properties
      @return ArrayList<String> The keyword properties to check again the collection of MTG cards
      @see MTGSearch.removeNonAlphanumeric( String str )
      @see consolidateKeyLists( String userInput )
   */
   private ArrayList<String> translateUserInput( String userInput ) {
      //Remove punctuation and make all uppercase
      userInput = MTGSearch.removeNonAlphanumericWhitespace( userInput.trim() ).toUpperCase();
      
      //Remove unneeded filler words
      String[] fillerWords = {" A "," AN "," THE "};
      userInput = removeFillerWords( userInput, fillerWords );
      
      //Create list for resolved keyword tokens
      ArrayList<String> keywords = new ArrayList<String>();
      
      //map for holding key checks and processing values
      HashMap<String, String> compiledDataListMap = null;
      try {
         compiledDataListMap = consolidateKeyLists();
      } catch( KeywordFileFormatException e ) {
         SOPln( e.getMessage() );
      }
      
      //@@DEBUG
      //printMap( compiledDataListMap );
      
      /*Check each key. If the key is in the keyset, grab values and check values against
        the userInput. If found, parse out the keyword and store the value in the keywords list.
        Finally, pop out the phrase found, and concatenate the user String input
        
        Continue this until all keys have been checked, or until the String is empty, or filled
        with whitespace
      */
      for( String key: compiledDataListMap.keySet() ) {
         if( isWhitespace( userInput ) ) break; //break out of key checking if the userInput has popped out all tokens except whitespace
         
         if( userInput.contains( key ) ) {
            String processorWordLine = compiledDataListMap.get( key ); //has values at key
            String[] processorWordList = processorWordLine.split("#"); //split around #
            String[] processorWordSet = new String[ processorWordList.length / 2 ]; //for the processor words
            String[] keyWordSet = new String[ processorWordSet.length ]; //parallel array with above for keywords
            
            //move alternating String sets into their parallel arrays
            for( int i = 0, j = 0, k = 0; i < processorWordList.length; i++ ) {
               if( i % 2 == 0 ) processorWordSet[j++] = processorWordList[i];
               else             keyWordSet[k++] = processorWordList[i];
            }
            
            /*Split processor words for each set and check if userInput contains these tokens
              If they do, add keyword in parallel array to keywords ArrayList. Then pop these
              tokens found out of the original userInput
            */
            int processorSetCount = 0;
            for( int i = 0; i < processorWordSet.length; i++ ) {
               String[] splitProcessorList = processorWordSet[i].split(",");
               for( int j = 0; j < splitProcessorList.length; j++ ) {
                  if( userInput.contains( splitProcessorList[j] ) ) {
                     keywords.add( keyWordSet[ processorSetCount ] );
                     userInput = userInput.replaceAll( splitProcessorList[j], " " );
                     userInput = userInput.trim();
                     break;
                  }
               }
               ++processorSetCount;
            }
         }//end if
      }//end for
      
      //Add leftover tokens to end of list to be processed later
      keywords.add( userInput );
      
      return keywords;
   }
   
   /**
      Consolidate the language processing lines for each given key.
      
      The keyword is added manually after all the processing tokens,
      and flagged with surrounding '#'s. This needs to be done since HashMaps
      cannot have duplicate keys with overwriting values, and since HashMaps
      can also not support three tiers of connectivity (a key that corresponds with
      a value, which in turn is a key for a different value)
     
      I thought about writing a new data structure for this, but it seemed like quite
      the chore, so I went with the jank approach instead
      
      @return HashMap<String, String> The map with the keys and language processing checks, which have the keyword integrated between '#'s
      @see MTG_KEYWORD_SEARCH_FILE_LOC
      @see translateUserInput( String userInput )
   */
   private HashMap<String, String> consolidateKeyLists() throws KeywordFileFormatException {
      File mtgKeywordFile = new File( MTG_KEYWORD_SEARCH_FILE_LOC );
      Scanner fileScanner = null;
      try {
         fileScanner = new Scanner( mtgKeywordFile );
      } catch( IOException e ) {
         e.printStackTrace();
      }
      
      //Create map for holding key checks and processing values
      HashMap<String, String> map = new HashMap<String, String>();
      
      int lineCounter = 0;
      boolean hasFoundStart = false;
      while( fileScanner.hasNextLine() ) {
         //skip to regular lines
         if( !hasFoundStart && !fileScanner.nextLine().equals("@@@") ) continue;
         else hasFoundStart = true;
         
         ++lineCounter;
         String line = fileScanner.nextLine();
         String[] lineSplit = line.split("@");
         if( lineSplit.length != 3 ) throw new KeywordFileFormatException("Expected three @s on line " + lineCounter + " in " + MTG_KEYWORD_SEARCH_FILE_LOC.substring(2, MTG_KEYWORD_SEARCH_FILE_LOC.length() ) +
                                                                           ", but found " + lineSplit.length + " @ symbols for parsing");
         if( !map.containsKey( lineSplit[0] ) ) map.put( lineSplit[0], lineSplit[1] + "#" + lineSplit[2] + "#" );
         else {
            //if map already has this key, get the value, and the append the additional relevant language processing tokens. Then overwrite the data using a put
            String valueAtKey = map.get( lineSplit[0] );
            map.put( lineSplit[0], valueAtKey + lineSplit[1] + "#" + lineSplit[2] + "#" );
         }
      }
      
      return map;
   }
   
   /**
      Remove filler words from user input
      
      @param userInput The input to remove filler words from
      @param fillerWords The list of words that can be removed safely. These words are buffered by spaces on either side
      @return String The userInput without any filler words
      @see translateUserInput( String userInput )
      @see fillerWords[] in the above method
   */
   private String removeFillerWords( String userInput, String[] fillerWords ) {
      for( int i = 0; i < fillerWords.length; i++ )
         userInput = userInput.replaceAll( fillerWords[i], " " );
      
      return userInput;
   }
   
   /**
      Determines if a String is empty or consists of only whitespace
      
      @param str The String to check
      @return boolean True of the String is only whitespace or is empty, false otherwise
      @see translateUserInput( String userInput )
      @see Character.isWhitespace( char ch )
   */
   private boolean isWhitespace( String str ) {
      if( str.isEmpty() ) return true;
      
      char[] set = str.toCharArray();
      
      for( int i = 0; i < set.length; i++ ) {
         if( !Character.isWhitespace( set[i] ) ) return false;
      }
      
      return true;
   }
   
   /**
      Prints the list of cards generated after searching
   */
   public void printList() {
      
   }
   
   /**
      Print ArrayList<String>
      
      @param ArrayList<String> The list to be printed
      @see translateUserInput( String userInput )
   */
   private void printList( ArrayList<String> list ) {
      for( int i = 0; i < list.size(); i++ ) {
         SOPln( list.get(i) );
      }
   }
   
   /**
      Print map
      
      @param map The map to print key and value pairs
   */
   private void printMap( Map<String,String> map ) {
      map.entrySet().forEach(entry -> {
          SOPln( entry.getKey() + " -> " + entry.getValue() );
      });
   }
   
   /**
      Sorts a card list alphabetically by name
      
      @param list The list of MTGCards to sort by name
      @see List.sort( Comparator< ? super E> c )
   */
   public void sortByName( ArrayList<MTGCard> list ) {
      
      list.sort( (o1, o2) -> o1.getName().compareTo( o2.getName() ));
      
   }
   
   /**
      Print all cards in set
      
      These cards are not queried--beware, there are many hundreds of them
      
      @see MTGCard.getName()
   */
   public void printAllCards() {
      int number = 1;
      for( MTGCard card : cardList )
         SOPln( "#" + number++ + ": " + card.getName() );
         
   }
   
   /**
      Prints all cards in the set, including the properties of each card
      
      @see MTGCard.printProperties()
   */
   public void printAllCardsWithProperties() {
      for( MTGCard card : cardList )
         card.printProperties();
   }
   
   /**
      Exception class related to errors with the keyword text file
   */
   private class KeywordFileFormatException extends Exception {
      
      /**
         Throw new exception
         
         @param errorMessage The message to print
      */
      public KeywordFileFormatException( String errorMessage ) {
         super( errorMessage );
      }
      
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