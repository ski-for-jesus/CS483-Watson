import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/* Ted Seipp
 * CSC 483
 * 6 September 2020
 * Professor Surdeanu
 */

public class QueryEngine {
    boolean indexExists=false;
    String inputFilePath ="";

    public QueryEngine(String inputFile) throws IOException{
        inputFilePath =inputFile;
        buildIndex();
    }

    @SuppressWarnings("deprecation")
	private void buildIndex() throws IOException {
        //Get file from resources folder
    	// Lines of object declaration taken from Lucene in 5 minutes example
    	StandardAnalyzer analyzer = new StandardAnalyzer();
    	IndexWriterConfig config = new IndexWriterConfig(analyzer);
    	Directory index = new RAMDirectory();
    	IndexWriter writer = new IndexWriter(index, config);
    	// remember to close writer
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(inputFilePath).getFile());

        try (Scanner inputScanner = new Scanner(file)) {
            while (inputScanner.hasNextLine()) {
                //System.out.println(inputScanner.nextLine());
            	String currLine = inputScanner.nextLine();
            	documentAdder(writer, currLine);
            }
            inputScanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        indexExists = true;
    }
    
    public static void documentAdder(IndexWriter writer, String currLine) throws IOException {
    	System.out.println(currLine);
    	Document doc1 = new Document();
    	Document doc2 = new Document();
    	Document doc3 = new Document();
    	Document doc4 = new Document();
    	// instead of splitting whole line, do index of first white space, text to left becomes doc id
    	// text to right becomes tokens
    	String[] tokenArray = currLine.split(" ");
    	String docID = tokenArray[0].substring(3);
    	System.out.println(docID);
    	// index document id numbers as string fields, we dont want to tokenize them
    	// then once docid is known add all of the tokens/words in the array to the document
    	// overkill, hardcoded for 4 documents
    	if(docID.equals("1")) {
    		doc1.add(new StringField("docID", docID, Field.Store.YES));
    		for(int i = 1; i < tokenArray.length; i++) {
    			// can pass the entire remaining string to lucene
    			// we want single text field for the string of tokens
    			doc1.add(new TextField("token", tokenArray[i], Field.Store.YES));
    		}
    	} else if (docID.equals("2")) {
    		doc2.add(new StringField("docID", docID, Field.Store.YES));
    		for(int i = 1; i < tokenArray.length; i++) {
    			doc2.add(new TextField("token", tokenArray[i], Field.Store.YES));
    		}
    	} else if (docID.equals("3")) {
    		doc3.add(new StringField("docID", docID, Field.Store.YES));
    		for(int i = 1; i < tokenArray.length; i++) {
    			doc3.add(new TextField("token", tokenArray[i], Field.Store.YES));
    		}
    	} else {
    		doc4.add(new StringField("docID", docID, Field.Store.YES));
    		for(int i = 1; i < tokenArray.length; i++) {
    			doc4.add(new TextField("token", tokenArray[i], Field.Store.YES));
    		}
    	}
    	// add documents to index
    	writer.addDocument(doc1);
    	writer.addDocument(doc2);
    	writer.addDocument(doc3);
    	writer.addDocument(doc4);
    }

    public static void main(String[] args ) {
        try {
            String fileName = "input.txt";
            String[] query13a = {"information", "retrieval"};
            QueryEngine objQueryEngine = new QueryEngine(fileName);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
