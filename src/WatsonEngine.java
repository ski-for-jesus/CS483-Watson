import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import edu.stanford.nlp.simple.*;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/* Ted Seipp
 * CSC 483
 * 9 December 2020
 * Watson Project
 * Professor Surdeanu
 */

public class WatsonEngine {
    boolean indexExists=false;
    String inputFilePath ="";

    public WatsonEngine(String inputFile) throws IOException{
        inputFilePath =inputFile;
        buildIndex();
    }

    @SuppressWarnings("deprecation")
	private void buildIndex() throws IOException {
    	// Lines of object declaration taken from Lucene in 5 minutes example
    	edu.stanford.nlp.simple.Document doc;
    	StandardAnalyzer analyzer = new StandardAnalyzer();
    	IndexWriterConfig config = new IndexWriterConfig(analyzer);
    	Directory index = new RAMDirectory();
    	IndexWriter writer = new IndexWriter(index, config);
    	File file = new File(inputFilePath);
        try (Scanner inputScanner = new Scanner(file)) {
            while (inputScanner.hasNextLine()) {
            	// CoreDocument used from Stanford NLP to avoid overlap with 
            	// Lucene Document Class
            	doc = new edu.stanford.nlp.simple.Document(inputScanner.nextLine());
            	for(Sentence sent : doc.sentences()) {
            		System.out.println(sent.words());
            		System.out.println(sent.lemmas());
            	}
            	//documentAdder(writer, currLine);*/

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
            String fileName = "questions.txt";
            String[] query13a = {"information", "retrieval"};
            WatsonEngine objWatsonEngine = new WatsonEngine(fileName);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

   private  List<ResultClass> returnDummyResults(int maxNoOfDocs) {

        List<ResultClass> doc_score_list = new ArrayList<ResultClass>();
            for (int i = 0; i < maxNoOfDocs; ++i) {
                Document doc = new Document();
                doc.add(new TextField("title", "", Field.Store.YES));
                doc.add(new StringField("docid", "Doc"+Integer.toString(i+1), Field.Store.YES));
                ResultClass objResultClass= new ResultClass();
                objResultClass.DocName =doc;
                doc_score_list.add(objResultClass);
            }

        return doc_score_list;
    }

}
