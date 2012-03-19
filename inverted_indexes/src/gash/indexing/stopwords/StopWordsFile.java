package gash.indexing.stopwords;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;

/**
 * Words which have no place in this term-concept mapping are those which
 * describe no concepts. The particle words of grammar, the, of, and ..., known
 * in IR as stopwords, fall into this category. Stopwords can be useful for
 * retrieval but only in searching for phrases (see
 * http://snowball.tartarus.org/texts/introduction.html)
 * 
 * Sources:
 * <ul>
 * <li>http://www.ranks.nl/resources/stopwords.html
 * <li>http://www.lextek.com/manuals/onix/stopwords1.html
 * </ul>
 * 
 * @author gash1
 * 
 */
public final class StopWordsFile implements StopWords {
	private HashSet<String> list = new HashSet<String>();

	public StopWordsFile(File src) throws Exception {
		init(src);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gash.indexing.inverted.StopWords#ignore(java.lang.String)
	 */
	@Override
	public boolean contains(String word) {
		if (word == null || word.trim().length() < 2)
			return true;
		else if (list != null)
			return list.contains(word.trim().toLowerCase());
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gash.indexing.inverted.StopWords#init(java.io.File)
	 */
	@Override
	public void init(File swf) throws Exception {
		if (swf == null)
			return;

		list.clear();

		if (!swf.exists())
			throw new RuntimeException("File not found: " + swf.getAbsolutePath());

		BufferedReader rdr = null;
		try {
			rdr = new BufferedReader(new FileReader(swf));
			String raw = rdr.readLine();
			while (raw != null) {
				if (!raw.startsWith("#"))
					list.add(raw.trim().toLowerCase());
				raw = rdr.readLine();
			}
		} finally {
			rdr.close();
		}
	}
}
