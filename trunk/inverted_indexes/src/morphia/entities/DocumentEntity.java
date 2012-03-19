package morphia.entities;



import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.utils.IndexDirection;

@Entity("Document")
public class DocumentEntity {
	 @Id
	 ObjectId id;
	 
	private String name;
	
	@Indexed(name = "fileUrl", unique = true)
	private String location; // file,url
	
	private String description;
	private Date date;
	private long numWords;
	
	@Embedded
	private HashMap<String, KeyWordEntity> keywords = new HashMap<String, KeyWordEntity>();
	
	
	
	
	



	public ObjectId getId() {
		return id;
	}




	public void setId(ObjectId id) {
		this.id = id;
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




	public Date getDate() {
		return date;
	}




	public void setDate(Date date) {
		this.date = date;
	}




	public long getNumWords() {
		return numWords;
	}




	public void setNumWords(long numWords) {
		this.numWords = numWords;
	}




	public HashMap<String, KeyWordEntity> getKeywords() {
		return keywords;
	}




	public void setKeywords(HashMap<String, KeyWordEntity> keywords) {
		this.keywords = keywords;
	}




	public DocumentEntity(File f) {
		if (f == null)
			return;

		location = f.getAbsolutePath();
		name = f.getName();
		date = new Date(f.lastModified());
	}
	
	public DocumentEntity()
	{
		
	}
	
	
	
	
	public void addKeyword(String w, int position) {
		if (w == null || w.trim().length() == 0)
			return;
		else {
			KeyWordEntity kw = keywords.get(w.trim().toLowerCase());
			if (kw == null) {
				kw = new KeyWordEntity(w.trim().toLowerCase(), position);
				keywords.put(kw.word, kw);
			} else
				kw.add(position);
		}

	}
	
	
	
	
	public double similarity(DocumentEntity d2) {
		if (keywords == null || d2.keywords == null)
			return 0.0;

		int total = 0, count = 0;
		if (keywords.size() > d2.keywords.size()) {
			for (KeyWordEntity kw : keywords.values()) {
				total += kw.position.size();
				if (d2.keywords.containsKey(kw.word))
					count += kw.position.size();
			}
		} else {
			for (KeyWordEntity kw : d2.keywords.values()) {
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
	
	
	
	
	
	public List<KeyWordEntity> keywords() {
		List<KeyWordEntity> list = new ArrayList<KeyWordEntity>(keywords.values());
		Collections.sort(list, new Comparator<KeyWordEntity>() {

			@Override
			public int compare(KeyWordEntity w1, KeyWordEntity w2) {
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

		for (KeyWordEntity kw : keywords()) {
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

}
