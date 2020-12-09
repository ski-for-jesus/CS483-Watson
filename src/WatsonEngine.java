import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.intervals.IntervalIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import edu.stanford.nlp.simple.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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
    public Directory index;

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
    	index = FSDirectory.open(Paths.get("/Users/tedseipp/Desktop/CSC483Watson/CS483-Watson/indexes"));
    	// FSDirectory finalIndex = FSDirectory.open(Paths.get("path here");
    	IndexWriter writer = new IndexWriter(index, config);
    	File wikiDir = new File(inputFilePath);
    	for(File wiki : wikiDir.listFiles()) {
    		System.out.println("Current wiki is " + wiki.getName() + "\n");
    		File wikiDoc = new File(wiki.getPath());
    		try (Scanner input = new Scanner(wikiDoc)) {
    			while(input.hasNextLine()) {
    				String currLine = input.nextLine();
    				// process titles first
    				 if(currLine.startsWith("[[")) {
    					 if(currLine.startsWith("[[File") || currLine.startsWith("[[Media")
    							 || currLine.startsWith("[[Image")) {
    						 // skip titles that aren't really titles
    						 continue;
    					 }
    					documentAdder(writer, currLine, input, true);
    				} else {
    					documentAdder(writer, currLine, input, false);
    				}
    			}
    			input.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	writer.close();
    	IndexReader reader = DirectoryReader.open(index);
        final Fields fields = MultiFields.getFields(reader);
        final Iterator iterator = (Iterator) fields.iterator();

        java.util.Iterator<String> iterator2 = (java.util.Iterator<String>) iterator;
		while(iterator2.hasNext()) {
            final String field = iterator2.next();
            final Terms terms = MultiFields.getTerms(reader, field);
            final TermsEnum it = terms.iterator();
            BytesRef term = it.next();
            while (term != null) {
                System.out.println(term.utf8ToString());
                term = it.next();
            }
        }
        indexExists = true;
    }
    
    public static void documentAdder(IndexWriter writer, String currLine, Scanner input, Boolean title) throws IOException {
    	Document currDoc = new Document();
    	if(title) {
    		currDoc.add(new StringField("title", currLine, Field.Store.YES));
    	}
    	// add documents to index
    	writer.addDocument(currDoc);
    }

    public static void main(String[] args ) {
        try {
        	// Directory to store wiki is hard coded
            String wikiDir = "src/files/";
            WatsonEngine objWatsonEngine = new WatsonEngine(wikiDir);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
