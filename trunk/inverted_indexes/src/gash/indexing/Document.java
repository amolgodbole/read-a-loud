package gash.indexing;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Document {
	private String name;
	private String location; // file,url
	private String description;
	private Date date;
	private long numWords;

	// may want to count the words to know the strength of a word within a
	// document. Also, separation (distance between words) can give us other
	// metrics on the keywords
	public HashMap<String, KeyWord> keywords = new HashMap<String, KeyWord>();

	public HashMap<String, KeyWord> docIDs = new HashMap<String, KeyWord>();

	public Document(File f) {
		if (f == null)
			return;

		location = f.getAbsolutePath();
		name = f.getName();
		date = new Date(f.lastModified());
	}

	/**
	 * collect keywords by frequency
	 * 
	 * TODO use a euclidean distance for weighting word importance => clustering
	 * 
	 * @param w
	 */
	public void addKeyword(String w, int position) {
		if (w == null || w.trim().length() == 0)
			return;
		else {
			KeyWord kw = keywords.get(w.trim().toLowerCase());
			if (kw == null) {
				kw = new KeyWord(w.trim().toLowerCase(), position);
				keywords.put(kw.word, kw);
			} else
				kw.add(position);
		}

	}

	/**
	 * Examines if the two documents share keywords and returns a intersection
	 * value (0-1.0) uses a weighted jaccard index by including the frequency of
	 * word occurrences
	 * 
	 * @param doc
	 * @return
	 */
	public double similarity(Document doc) {
		if (keywords == null || doc.keywords == null)
			return 0.0;

		int total = 0, count = 0;
		if (keywords.size() > doc.keywords.size()) {
			for (KeyWord kw : keywords.values()) {
				total += kw.position.size();
				if (doc.keywords.containsKey(kw.word))
					count += kw.position.size();
			}
		} else {
			for (KeyWord kw : doc.keywords.values()) {
				total += kw.position.size();
				if (keywords.containsKey(kw.word))
					count += kw.position.size();
			}
		}

		// jaccard weighted index
		double r = (double) count / (double) total;

		// rounding
		int ri = (int) (r * 10000.0f);
		r = ((double) ri) / 10000.0f;
		return r;
	}

	/**
	 * ordered list of keywords by frequency of occurrence
	 * 
	 * @return
	 */
	public List<KeyWord> keywords() {
		List<KeyWord> list = new ArrayList<KeyWord>(keywords.values());
		Collections.sort(list, new Comparator<KeyWord>() {

			@Override
			public int compare(KeyWord w1, KeyWord w2) {
				if (w1.position.size() == w2.position.size())
					return 0;
				else if (w1.position.size() < w2.position.size())
					return 1;
				else
					return -1;
			}
		});

		return list;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ");
		sb.append(name);
		sb.append("\nFile: ");
		sb.append(location);
		sb.append("\nNum words: ");
		sb.append(numWords);
		sb.append("\nKeywords:\n");

		DecimalFormat fmt = new DecimalFormat("#.##");

		for (KeyWord kw : keywords()) {
			sb.append(kw.word);
			sb.append(" (n = ");
			sb.append(kw.position.size());
			sb.append(", pos = [");

			double ave = 0.0f;
			for (Integer p : kw.position) {
				ave += p;
				sb.append(p);
				sb.append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("], ");

			ave /= kw.position.size();
			sb.append("mean = ");
			sb.append(fmt.format(ave));
			sb.append(", stdev = ");
			sb.append(fmt.format(kw.stdDev()));
			sb.append(")\n");

		}
		return sb.toString();
	}

	public String csvHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append("name,location,numWords\n");
		sb.append("\"");
		sb.append(name);
		sb.append("\",\"");
		sb.append(location);
		sb.append("\",");
		sb.append(numWords);
		sb.append("\n");

		return sb.toString();
	}

	public String csvData() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n");
		sb.append("word, freq, pos (mean), pos (stdev)\n");

		DecimalFormat fmt = new DecimalFormat("#.##");

		for (KeyWord kw : keywords()) {
			double ave = 0.0f;
			for (Integer p : kw.position) {
				ave += p;
			}
			ave /= kw.position.size();

			sb.append("\"");
			sb.append(kw.word);
			sb.append("\",");
			sb.append(kw.position.size());
			sb.append(",");
			sb.append(fmt.format(ave));
			sb.append(",");
			sb.append(fmt.format(kw.stdDev()));
			sb.append("\n");

		}
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getNumWords() {
		return numWords;
	}

	public void setNumWords(long numWords) {
		this.numWords = numWords;
	}
}
