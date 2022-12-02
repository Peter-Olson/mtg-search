
import java.util.HashSet;
import java.util.Arrays;

/**
   MTGCard.java
   
   Stores the property information for a MTG card
   
   @author Peter Olson
   @version 1/2/22
   @see MTGQuery.java
   @see MTGSearch.java
   @see mtgCards.txt
*/
public class MTGCard {
   
   private final int BASE_PROPERTIES_SIZE = 16;
   private int propertiesSize;
   private final int VARIABLE_COST = -1;
   
   /* @@@@@@@@@@@@@@@@@@@@@@@@@@@ FIELD PROPERTIES @@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
   /* @ */                                                                  /* @ */
   /* @ */   //Line number                                                  /* @ */
   /* @ */   private String LINE_NUMBER;                                    /* @ */
   /* @ */                                                                  /* @ */
   /* @ */   //image name                                                   /* @ */
   /* @ */   private final String IMAGE_NAME;                               /* @ */
   /* @ */                                                                  /* @ */
   /* @ */   //Base Properties                                              /* @ */
   /* @ */   private String   NAME, CARD_TYPE, SUBTYPE, MANA_COST, RARITY,  /* @ */
   /* @ */                    CARD_NUMBER, SET_NAME, ARTIST, YEAR, POWER,   /* @ */
   /* @ */                    TOUGHNESS;                                    /* @ */
   /* @ */   private String[] SUBTEXT_CHARS;                                /* @ */
   /* @ */   private String   EQUIP_COST, LOYALTY, QUANTITY;                /* @ */
   /* @ */                                                                  /* @ */
   /* @ */   //Properties                                                   /* @ */
   /* @ */   private HashSet<String> baseSet;                               /* @ */
   /* @ */   private HashSet<String> propertiesSet;                         /* @ */
   /* @ */                                                                  /* @ */
   /* @ */   //Translated Properties                                        /* @ */
   /* @ */   private String   COLOR_TYPE; //R,U,G,W,B, or multiple, RGW, eg /* @ */
   /* @ */   private int      TOTAL_COST; //Added mana costs together       /* @ */
   /* @ */                                                                  /* @ */
   /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
   
   /**
      Sets the fields / properties of the MTG card, as well as the image name.
      
      The 'baseList' array properties are qualified by name and are parsed into
      their corresponding data types. The 'properties' array properties are
      stored in a HashSet for quick check access
      
      @param baseList The list of base properties, which include: Name, Card Type,
             Subtype, Mana Cost, Rarity, Card Number, Set Name, Artist, Date,
             Power, Toughness, Subtext Chars, Equip Cost, Loyalty, and Quantity
      @param imageName The name of the image file associated with this card
      @param properties The list of additional properties attributes to this card.
             The total number of properties is too great to mention here, but can be
             found in the mtgWordList.xlsx file, or by viewing via running the 'help'
             function through the menu for this program, by looking in the 'all'
             category
      @see mtgWordList.xlsx
      @see mtgCards.txt
      @see MTGQuery.java
      @see MTGSearch.java
      @see setBaseProperties( String[] baseList )
      @see IMAGE_NAME
      @see setProperties( String[] properties )
   */
   public MTGCard( String[] baseList, String imageName, String[] properties ) {
      
      try{
         setBaseProperties( baseList );
         setProperties( properties );
      } catch( MTGCardPropertyException e ) {
         SOPln( e.getMessage() );
      }
      
      IMAGE_NAME = imageName;
      
   }
   
   /**
      Sets the base properties of this mtg card, including:
      
      Name, Card Type, Subtype, Mana Cost, Rarity,
      Card Number, Set Name, Artist, Year, Power, Toughness,
      Subtext Chars, Equip Cost, Loyalty, and Quantity
      
      @param baseList The list of properties to parse and set to this object
      @see MTGCard( String[] baseList, String imageName, String[] properties )
   */
   private void setBaseProperties( String[] baseList ) throws MTGCardPropertyException {
      
      //Set line number beforehand in case there is an error found
      LINE_NUMBER   = baseList[0];
      
      if( baseList.length != BASE_PROPERTIES_SIZE )
         throw new MTGCardPropertyException("Line #" + LINE_NUMBER + ": Expected total base properties: " + BASE_PROPERTIES_SIZE + ", Found: " + baseList.length );
      
      NAME          = baseList[1];
      CARD_TYPE     = baseList[2];
      SUBTYPE       = baseList[3];
      MANA_COST     = baseList[4];
      RARITY        = baseList[5];
      CARD_NUMBER   = baseList[6];
      SET_NAME      = baseList[7];
      ARTIST        = baseList[8];
      YEAR          = baseList[9];
      POWER         = baseList[10];
      TOUGHNESS     = baseList[11];
      SUBTEXT_CHARS = baseList[12].toUpperCase().split("@");
      EQUIP_COST    = baseList[13];
      LOYALTY       = baseList[14];
      QUANTITY      = baseList[15];
      
      //Set COLOR_TYPE and COLOR_COST
      setColorType( MANA_COST );
      setColorCost( MANA_COST );
      
      /* Set HashSet for base properties */
      final int TOTAL_STRING_BASE_PROPERTIES_EXCLUDING_SUBLISTS = 5; //don't want to double-count
      String[] nameList = MTGSearch.removeNonLettersWhitespace( NAME ).toUpperCase().split(" ");
      String[] typeList = CARD_TYPE.toUpperCase().split(" ");
      String[] subtypeList = SUBTYPE.toUpperCase().split(" ");
      String[] artistList = MTGSearch.removeNonLettersWhitespace( ARTIST ).toUpperCase().split(" ");
      baseSet = new HashSet<String>( TOTAL_STRING_BASE_PROPERTIES_EXCLUDING_SUBLISTS + nameList.length + typeList.length + subtypeList.length + artistList.length + SUBTEXT_CHARS.length );
      String[][] lists = { nameList, typeList, subtypeList, artistList, SUBTEXT_CHARS };
      
      //Add sublists
      for( int i = 0; i < lists.length; i++ ) {
         for( int j = 0; j < lists[i].length; j++ ) {
            baseSet.add( lists[i][j] );
         }
      }
      
      //Add single-item base properties
      baseSet.add( RARITY.toUpperCase()   );
      baseSet.add( SET_NAME.toUpperCase() );
      baseSet.add( YEAR.toUpperCase()     );
      
      //Note that the base HashSet does not have MANA_COST, CARD_NUMBER, POWER, TOUGHNESS, EQUIP_COST, LOYALTY, or QUANTITY for hash look-up
      
      /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
   }
   
   /**
      Sets the main color type of this card
      
      @param MANA_COST The mana cost of this card
      @see setBaseProperties( String[] baseList )
   */
   private void setColorType( String MANA_COST ) {
      if(      MANA_COST.contains("R") ) COLOR_TYPE = "RED";
      else if( MANA_COST.contains("U") ) COLOR_TYPE = "BLUE";
      else if( MANA_COST.contains("G") ) COLOR_TYPE = "GREEN";
      else if( MANA_COST.contains("W") ) COLOR_TYPE = "WHITE";
      else if( MANA_COST.contains("B") ) COLOR_TYPE = "BLACK";
      else                               COLOR_TYPE = "COLORLESS";
   }
   
   /**
      Sets the total cost (in mana) of this card
      
      @param MANA_COST The mana cost of this card
      @see setBaseProperties( String[] baseList )
   */
   private void setColorCost( String MANA_COST ) {
      MANA_COST = MANA_COST.replaceAll("[^0-9X]+", " ");
      TOTAL_COST = 0;
      
      if( MANA_COST.contains("X") ) {
         TOTAL_COST = VARIABLE_COST;
      } else {
         String[] splitCost = MANA_COST.split(" ");
         for( int i = 0; i < splitCost.length; i++ ) {
            TOTAL_COST += Integer.parseInt( splitCost[i] );
         }
      }
   }
   
   /**
      Sets the properties of this mtg card.
      
      The properties of any given card are widely varied, and have no particular
      maximum in their quantity. The list of these properties can be found within the
      mtgWordList.xlsx and mtgWordList.txt lists, which can be seen in detail within
      the options menu when running the MTGSearch.java program
      
      The properties within this list are populated to a HashSet for optimal look-up
      performance
      
      @param properties The properties of this card. Each card typically varies in
                        their total properties
      @see HashSet.add( Element e )
   */
   private void setProperties( String[] properties ) {
      propertiesSize = properties.length;
      propertiesSet = new HashSet<String>( propertiesSize );
      
      for( int i = 0; i < propertiesSize; i++ )
         propertiesSet.add( properties[i] );
      
   }
   
   /**
      Print base properties and properties of card
      
      @see Arrays.toString( Object[] list )
   */
   public void printProperties() {
      SOPln( "#" + LINE_NUMBER + ": "                 + NAME        );
      SOPln( "\t\tCard Type:   "                      + CARD_TYPE   );
      SOPln( "\t\tSubtype:     "                      + SUBTYPE     );
      SOPln( "\t\tMana Cost:   "                      + MANA_COST   );
      SOPln( "\t\tRarity:      "                      + RARITY      );
      SOPln( "\t\tCard Number: "                      + CARD_NUMBER );
      SOPln( "\t\tSet Name:    "                      + SET_NAME    );
      SOPln( "\t\tArtist:      "                      + ARTIST      );
      SOPln( "\t\tYear:        "                      + YEAR        );
      SOPln( "\t\tPower:       "                      + POWER       );
      SOPln( "\t\tToughness:   "                      + TOUGHNESS   );
      SOPln( "\t\tSubtext Important Characters: "     + Arrays.toString( SUBTEXT_CHARS ) );
      SOPln( "\t\tEquip Cost:  "                      + EQUIP_COST  );
      SOPln( "\t\tLoyalty:     "                      + LOYALTY     );
      SOPln( "\t\tQuantity:    "                      + QUANTITY    );
      SOPln( "\t\tProperties:  "                      + propertiesSet + "\n"  );
      
   }
   
   /**
      Determines whether the MTGCard has the given property
      
      @param property The String property to check against this card's properties
      @return boolean True if this card has the property, false otherwise
      @see HashSet.contains( Object o )
   */
   public boolean hasProperty( String property ) {
      if( propertiesSet.contains( property ) )
         return true;
         
      return false;
   }
   
   /**
      Adds a property to this card
      
      @param The property to add
      @return boolean True if this card did not have this property, false if
                      it did have this property
      @see HashSet.add( Element e )
   */
   public boolean addProperty( String property ) {
      return propertiesSet.add( property );
   }
   
   /**
      Returns the total number of properties that this MTGCard contains
      
      @return int The total number of properties of this MTGCard
      @see HashSet.size()
   */
   public int totalProperties() {
      return propertiesSet.size();
   }
   
   /**
      Get the HashSet of base properties for this MTGCard
      
      @return HashSet<String> The list of base properties
   */
   public HashSet<String> getBaseProperties() {
      return baseSet;
   }
   
   /**
      Get the HashSet of properties for this MTGCard
      
      @return HashSet<String> The list of properties
   */
   public HashSet<String> getProperties() {
      return propertiesSet;
   }
   
   /**
      Gets the image name of this mtg card
      
      @return String The image name associated with this mtg card
   */
   public String getImageName() {
      return IMAGE_NAME;
   }
   
   /**
      Gets the name of this mtg card
      
      @return String The name associated with this mtg card
   */
   public String getName() {
      return NAME;
   }
   
   /**
      Gets the card type of this mtg card
      
      @return String The card type associated with this mtg card
   */
   public String getCardType() {
      return CARD_TYPE;
   }
   
   /**
      Gets the subtype of this mtg card
      
      @return String The subtype associated with this mtg card
   */
   public String getSubtype() {
      return SUBTYPE;
   }
   
   /**
      Gets the mana cost of this mtg card
      
      @return String The mana cost associated with this mtg card
   */
   public String getManaCost() {
      return MANA_COST;
   }
   
   /**
      Gets the rarity of this mtg card
      
      @return String The rarity associated with this mtg card
   */
   public String getRarity() {
      return RARITY;
   }
   
   /**
      Gets the card number of this mtg card
      
      @return String The card number associated with this mtg card
   */
   public String getCardNumber() {
      return CARD_NUMBER;
   }
   
   /**
      Gets the set name of this mtg card
      
      @return String The set name associated with this mtg card
   */
   public String getSetName() {
      return SET_NAME;
   }
   
   /**
      Gets the artist of this mtg card
      
      @return String The artist associated with this mtg card
   */
   public String getArtist() {
      return ARTIST;
   }
   
   /**
      Gets the date of this mtg card
      
      @return String The date associated with this mtg card
   */
   public String getYear() {
      return YEAR;
   }
   
   /**
      Gets the power of this mtg card
      
      @return String The power associated with this mtg card
   */
   public String getPower() {
      return POWER;
   }
   
   /**
      Gets the toughness of this mtg card
      
      @return String The toughness associated with this mtg card
   */
   public String getToughness() {
      return TOUGHNESS;
   }
   
   /**
      Gets the subtext characters of this mtg card
      
      @return String[] The subtext characters associated with this mtg card
   */
   public String[] getSubtextChars() {
      return SUBTEXT_CHARS;
   }
   
   /**
      Gets the equip cost of this mtg card
      
      @return String The equip cost associated with this mtg card
   */
   public String getEquipCost() {
      return EQUIP_COST;
   }
   
   /**
      Gets the loyalty of this mtg card
      
      @return String The loyalty associated with this mtg card
   */
   public String getLoyalty() {
      return LOYALTY;
   }
   
   /**
      Gets the quantity of this mtg card
      
      @return String The quantity associated with this mtg card
   */
   public String getQuantity() {
      return QUANTITY;
   }

   /**
      Exception class related to mtg card properties
   */
   private class MTGCardPropertyException extends Exception {
      
      /**
         Throw new exception
         
         @param errorMessage The message to print
      */
      public MTGCardPropertyException( String errorMessage ) {
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