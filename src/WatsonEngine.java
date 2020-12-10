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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.intervals.IntervalIterator;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import edu.stanford.nlp.simple.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

/* Ted Seipp
 * CSC 483
 * 9 December 2020
 * Watson Project
 * Professor Surdeanu
 */

/* Watson is represented as an engine class based off of the skeleton
 * code from HW3. When created there are parameters for the index,
 * the file path and a boolean representing if the index has been created.
 */
public class WatsonEngine {
    boolean indexExists = false;
    String inputFilePath = "";
    public static Directory index;
    public static StandardAnalyzer analyzer;
    public static ArrayList<String> results = new ArrayList<String>();

    // Constructor
    public WatsonEngine(String inputFile) throws IOException{
        inputFilePath = inputFile;
        buildIndex();
    }

    /* Main work is done within this method, 
     * all Lucene object stubs are created, and the index is set up
     * then each line of the wiki article is read in and sent to be 
     * added to the index based on title criteria.
     */
	private void buildIndex() throws IOException {
    	// Explicit declaration used because of multiple
    	// Document Classes
    	analyzer = new StandardAnalyzer();
    	IndexWriterConfig config = new IndexWriterConfig(analyzer);
    	index = FSDirectory.open(Paths.get("/Users/tedseipp/Desktop/CSC483Watson/CS483-Watson/indexes"));
    	IndexWriter writer = new IndexWriter(index, config);
    	// start with fresh indexes every time
    	writer.deleteAll();
    	File wikiDir = new File(inputFilePath);
    	for(File wiki : wikiDir.listFiles()) {
    		File wikiDoc = new File(wiki.getPath());
    		try (Scanner input = new Scanner(wikiDoc)) {
    			while(input.hasNextLine()) {
    				String currLine = input.nextLine();
    				// process titles first
    				 if(title(currLine)) {
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
        indexExists = true;
    }
    
	/* This method is used to add the tokens read in
	 * from the scanned txt file to the index created at
	 * construction time.
	 */
    public void documentAdder(IndexWriter writer, String currLine, Scanner input, boolean title) throws IOException {
    	Document currDoc = new Document();
    	String rawData = "";
    	if(title) {
    		currLine = currLine.replace("[", "");
    		currLine = currLine.replace("]", "");
    		currDoc.add(new StringField("title", currLine, Field.Store.YES));
    		//skip new line after title
    		input.nextLine();
    		currLine = input.nextLine();
    		// until the next title attribute all information to the title
    		// in the same document
    		while(!title(currLine) && input.hasNextLine()) {
    			List<String> lemmatizedTokens;
    			edu.stanford.nlp.simple.Document sDoc = new edu.stanford.nlp.simple.Document(currLine);
    			for(Sentence sent : sDoc.sentences()) {
    				lemmatizedTokens = sent.lemmas();
    				for(int i = 0; i < lemmatizedTokens.size(); i++) {
    					rawData += lemmatizedTokens.get(i) + " ";
    				}
    			}
				currLine = input.nextLine();
			}
    	}
    	currDoc.add(new TextField("raw data", rawData, Field.Store.YES));
    	// add documents to index
    	writer.addDocument(currDoc);
    }
    
    /* This method is used to create lemmatized questions 
     * and answers for the query parser
     */
    public static void buildQuestionIndex(String file) throws ParseException, IOException {
    	File questions = new File(file);
    	Scanner input = new Scanner(questions);
    	while(input.hasNextLine()) {
    		String finalQuestion = "";
    		String finalAnswer = "";
    		// skip category
    		input.nextLine();
    		String question = input.nextLine();
    		if(question.isEmpty()) {
    			continue;
    		}
    		Sentence qSent = new Sentence(question);
    		List<String> questionLemmas = qSent.lemmas();
    		for(String token : questionLemmas) {
    			finalQuestion += token + " ";
    		}
    		String answer = input.nextLine();
    		if(answer.isEmpty()) {
    			continue;
    		}
    		Sentence aSent = new Sentence(answer);
    		List<String> answerLemmas = qSent.lemmas();
    		for(String token : answerLemmas) {
    			finalAnswer += token + " ";
    		}
    		runQuery(finalQuestion, finalAnswer);
    	}
    }
    
    /* method to compute results from lemmatized queries,
     * examples taken from lucene website on how to use different
     * objects
     */
    public static void runQuery(String question, String answer) throws ParseException, IOException {
    	QueryParser qP = new QueryParser("raw data", analyzer);
    	IndexReader r = DirectoryReader.open(index);
    	IndexSearcher s = new IndexSearcher(r);
    	Query q = qP.parse(question);
    	// 100 hardcoded questions
    	TopDocs trebeksHeroes = s.search(q, 100);
    	ScoreDoc[] hits = trebeksHeroes.scoreDocs;
    	for(int i = 0; i < hits.length; i++) {
			Document doc = s.doc(hits[i].doc);
			if(doc.get("title").equals(answer)) {
				results.add(doc.get("title"));
			} 
    	}
    }
    
    public boolean title(String scannedLine) {
    	if(scannedLine.startsWith("[[")) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public static void main(String[] args ) {
        try {
        	// Directory to store wiki is hard coded
            String wikiDir = "src/files/";
            String questionsFile = "questions.txt";
            WatsonEngine objWatsonEngine = new WatsonEngine(wikiDir);
            buildQuestionIndex(questionsFile);
            System.out.println(Arrays.toString(results.toArray()));
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
