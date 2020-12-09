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
    boolean indexExists = false;
    String inputFilePath = "";

    public WatsonEngine(String inputFile) throws IOException{
        inputFilePath = inputFile;
        buildIndex();
    }

    @SuppressWarnings("deprecation")
	private void buildIndex() throws IOException {
    	// Explicit declaration used because of multiple
    	// Document Classes
    	edu.stanford.nlp.simple.Document doc;
    	StandardAnalyzer analyzer = new StandardAnalyzer();
    	IndexWriterConfig config = new IndexWriterConfig(analyzer);
    	Directory index = new RAMDirectory();
    	// FSDirectory finalIndex = FSDirectory.open(Paths.get("path here");
    	IndexWriter writer = new IndexWriter(index, config);
    	File wikiDir = new File(inputFilePath);
    	for(File wiki : wikiDir.listFiles()) {
    		System.out.println("Current wiki is " + wiki.getName() + "\n");
    		File wikiDoc = new File(wiki.getPath());
    		try (Scanner input = new Scanner(wikiDoc)) {
    			while(input.hasNextLine()) {
    				String currLine = input.nextLine();
    				if(currLine.startsWith("[[")) {
    					System.out.println(currLine);
    				}
    			}
    				//System.out.println(input.nextLine());
    				//doc = new edu.stanford.nlp.simple.Document(input.nextLine());
                	//for(Sentence sent : doc.sentences()) {
                		//System.out.println(sent.words());
                		//System.out.println(sent.lemmas());
    				//documentAdder(writer, currLine);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            String wikiDir = "src/files/";
            WatsonEngine objWatsonEngine = new WatsonEngine(wikiDir);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
