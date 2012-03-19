package gash.indexing.inverted;

import gash.indexing.Document;
import gash.indexing.KeyWord;
import gash.indexing.stopwords.StopWords;
import gash.indexing.stopwords.StopWordsFile;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import morphia.entities.DocumentEntity;
import morphia.entities.InvertIndexEntity;
import morphia.entities.KeyWordEntity;


import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class Registry {
	public static final String sStopWords = "stop.words.file";
	public static final String sDataStorage = "data.storage";
	public static final String sIndexStorage = "index.storage";

	public enum Match {
		Or, And, Proximity
	}

	// TODO abstract/isolate storage. oh yeah and persist it too
	private HashMap<String, ArrayList<Document>> index = new HashMap<String, ArrayList<Document>>();

	private Loader loader;
	private Properties conf;
	Datastore datastore;

	public Registry(Properties conf) {
		this.conf = conf;
		setup();
		Mongo mongo;
		try {
			mongo = new Mongo("localhost");
	        Morphia morphia = new Morphia();

	        morphia.mapPackage("morphia.entities");
	        datastore = morphia.createDatastore(mongo, "bookstore");
	        datastore.ensureIndexes();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * find all documents containing these key words - union.
	 * 
	 * TODO allow for union and intersection
	 * 
	 * @param keywords
	 * @return
	 */
	public List<DocumentEntity> query(List<String> keywords) {
		if (keywords == null || keywords.size() == 0)
			return null;

		// TODO break down the keywords into separate words for searching (e.g.,
		// 'san jose' => 'san' 'jose')
		Mongo mongo;
		Datastore datastore = null;
		try {
			mongo = new Mongo("localhost");
	        Morphia morphia = new Morphia();

	        morphia.mapPackage("morphia.entities");
	        datastore = morphia.createDatastore(mongo, "bookstore");
	        datastore.ensureIndexes();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<DocumentEntity> r = new ArrayList<DocumentEntity>();
		for (String w : keywords) {
			//ArrayList<Document> sub = index.get(w);
			Query query = datastore.createQuery(InvertIndexEntity.class).filter("word = ",w);
			InvertIndexEntity iie = (InvertIndexEntity) query.get();
		//		System.out.println(iie.getDocuments().size());
			if (iie != null) {
				for (DocumentEntity d : iie.getDocuments()) {
					if (!r.contains(d))
						r.addAll(iie.getDocuments());
				}
			}
		}

		return r;
	}

	
	
	
	
	
	public void register(File f) {
		if (f == null)
			return;

		// TODO prevent double registration
		//here instead of getting from loader, get from mongo asList
		loader.load(f);
		Mongo mongo;
		Datastore datastore = null;
		try {
			mongo = new Mongo("localhost");
	        Morphia morphia = new Morphia();

	        morphia.mapPackage("morphia.entities");
	        datastore = morphia.createDatastore(mongo, "bookstore");
	        datastore.ensureIndexes();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Query q = datastore.createQuery(DocumentEntity.class);
		Iterable<DocumentEntity> allDocs = q.fetch();

		for (DocumentEntity d : allDocs) {
			for (KeyWordEntity kw : d.keywords()) {
				/*ArrayList<Document> list = index.get(kw.word);
				if (list == null) {
					list = new ArrayList<Document>();
					index.put(kw.word, list);*/
			        
				Query query = datastore.createQuery(InvertIndexEntity.class).filter("word = ",kw.word);
				InvertIndexEntity iie = (InvertIndexEntity) query.get();
				if (iie == null)
				{
					iie = new InvertIndexEntity();
					iie.setWord(kw.word);
				}
				iie.getDocuments().add(d);
				datastore.save(iie);
			//	list.add(d);
			}
		}
	}

	private void setup() {
		try {
			File idir = new File(conf.getProperty(sIndexStorage));

			File swf = new File(conf.getProperty(sStopWords));
			StopWords swords = new StopWordsFile(swf);
			loader = new Loader(swords);
		} catch (Exception e) {
			throw new RuntimeException("Failed to setup registry.", e);
		}
	}
}
