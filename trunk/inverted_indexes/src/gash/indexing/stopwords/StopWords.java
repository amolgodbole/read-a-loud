package gash.indexing.stopwords;

import java.io.File;

public interface StopWords {

	boolean contains(String word);

	void init(File swf) throws Exception;

}