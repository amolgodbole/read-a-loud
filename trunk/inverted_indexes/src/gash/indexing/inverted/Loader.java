package gash.indexing.inverted;

//import StationData;
import gash.indexing.Document;
import gash.indexing.stopwords.StopWords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.hibernate.Session;

import readaloud.models.User;
import readaloud.util.HibernateUtil;


import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.hibernate.entities.BookEntity;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import morphia.entities.DocumentEntity;

public class Loader {
	private StopWords ignore;
	private List<File> list = new ArrayList<File>();
	Class bookEntityClassObject = BookEntity.class;
	BookEntity bookEntity = new BookEntity();
	

	public Loader(StopWords stopWords) throws Exception {
		ignore = stopWords;
	}

	public List<DocumentEntity> load(File f) {
		if (f == null)
			return null;

		if (f.isFile())
			list.add(f);
		else {
			discoverFiles(f);
		}

		System.out.println("---> " + list);
		gatherData();
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
		List<DocumentEntity> allDocs = q.asList();
		return allDocs;
	}

	public List<File> files() {
		return list;
	}

	private void gatherData() {
		// TODO this should be ran in parallel
	//	ArrayList<Document> r = new ArrayList<Document>(list.size());
		Mongo mongo;
		Datastore datastore = null;
		try {
			mongo = new Mongo("localhost");
	        Morphia morphia = new Morphia();

	        morphia.map(DocumentEntity.class);
	        datastore = morphia.createDatastore(mongo, "bookstore");
	        datastore.ensureIndexes();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (File f : list) {
			BufferedReader rdr = null;
			DocumentEntity d = new DocumentEntity(f);
			
			try {
				rdr = new BufferedReader(new FileReader(f));
				String raw = rdr.readLine();
				int relPosition = 0;
				long numWords = 0;
				int lineNum = 0;
				while (raw != null) {
					if (lineNum < 25)
					{
						if(raw.startsWith("Title:")||raw.startsWith("Author:")||raw.startsWith("Release Date:")||raw.startsWith("Language:"))
						{
							String[] splitValues = raw.trim().split("[:\\[]");
							String key = (String) splitValues[0];
							String value = (String) splitValues[1].trim();
							System.out.println(raw);
							System.out.println(key);
							System.out.println(value);
							/*Method fileDataObjMeth = null, statDataObjMeth = null;
							statDataObjMeth = bookEntityClassObject.getMethod("set"+(key.trim()), new Class[]{String.class});
							statDataObjMeth.invoke(bookEntity, value);*/
							
							if(key.equalsIgnoreCase("Title"))
							{
								bookEntity.setTitle(value);
							}
							if(key.equalsIgnoreCase("Author"))
							{
								bookEntity.setAuthor(value);
							}
							if(key.equalsIgnoreCase("Release Date"))
							{
								bookEntity.setReleaseDate(value);
							}
							if(key.equalsIgnoreCase("Language"))
							{
								bookEntity.setLanguage(value);
							}
						}
						
						/*Session session = HibernateUtil.getSessionFactory().getCurrentSession();
						try {
							session.beginTransaction();
							session.save(bookEntity);
							session.getTransaction().commit();
						} catch (RuntimeException e) {
							session.getTransaction().rollback();
							throw e;
						}*/
						lineNum++;
			
					}
					String[] parts = raw.trim().split("[\\s,\\.:;\\-#~\\(\\)\\?\\!\\&\\*\\\"\\/\\'\\`\\$]");

					numWords += parts.length;
					
					for (String p : parts) {
						if (!ignore.contains(p))
							d.addKeyword(p, relPosition);
						
						// location (word position) in document allows use to
						// calculate strength by relative location and frequency
						relPosition++;
					}

					raw = rdr.readLine();
				}
				
				d.setNumWords(numWords);
				//r.add(d);
				datastore.save(d);
				
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				try {
					session.beginTransaction();
					bookEntity.setFilePath(f.getAbsolutePath());
					session.save(bookEntity);
					session.getTransaction().commit();
				} catch (RuntimeException e) {
					session.getTransaction().rollback();
					throw e;
				}
				
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (rdr != null)
						rdr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//return r;
	}

	/**
	 * depth search
	 * 
	 * @param dir
	 */
	private void discoverFiles(File dir) {
		if (dir == null || dir.isFile())
			return;

		File[] dirs = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.isFile())
					list.add(f);
				else if (f.getName().startsWith("."))
					; // ignore
				else if (f.isDirectory()) {
					discoverFiles(f);
				}

				return false;
			}
		});
	}
}
