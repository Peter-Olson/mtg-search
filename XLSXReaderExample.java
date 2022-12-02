import java.io.File;  
import java.io.FileInputStream;  
import java.util.Iterator;  
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.xssf.usermodel.XSSFSheet;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook; 

/**
   This was such a pain to get working. Never again.
   
   Here's how to do it again: (lol)
   
   1) Create Java Project
   2) Create package under source folder
   3) Create class file under project
   4) Go to File -> Properties -> Library -> Add External Jar Files -> Then, select all jar files. Make sure to check nested folders if grabbing from a zip
      Close and apply once all are selected.
   5) Go to 
*/
public class XLSXReaderExample {  

   public static void main( String[] args ) {  
      try {  
         File file = new File( "C:\\Users\\Peter Olson\\eclipse-workspace\\MTGSearch\\resources" );   //creating a new file instance  
         FileInputStream fis = new FileInputStream( file );   //obtaining bytes from the file  
         //creating Workbook instance that refers to .xlsx file  
         XSSFWorkbook wb = new XSSFWorkbook( fis );   
         XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object  
         Iterator<Row> itr = sheet.iterator();    //iterating over excel file  
         while( itr.hasNext() ) {  
            Row row = itr.next();  
            Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column  
            while( cellIterator.hasNext() ) {  
               Cell cell = cellIterator.next();  
               switch( cell.getCellType() ) {  
                  case Cell.CELL_TYPE_STRING:    //field that represents string cell type  
                  System.out.print( cell.getStringCellValue() + "\t\t\t" );  
                  break;  
                  case Cell.CELL_TYPE_NUMERIC:    //field that represents number cell type  
                  System.out.print( cell.getNumericCellValue() + "\t\t\t" );  
                  break;  
                  default:
               }  
            }  
            System.out.println("");  
         }  
      } catch( Exception e ) {  
         e.printStackTrace();  
      }  
   }  
}  