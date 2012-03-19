package gash.indexing.inverted;

import gash.indexing.Document;
import gash.indexing.stopwords.StopWords;
import gash.indexing.stopwords.StopWordsFile;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import morphia.entities.DocumentEntity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoaderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testLoadDocs() throws Exception {
		File swf = new File("resources/stopwords.txt");
		StopWords swords = new StopWordsFile(swf);
		File dir = new File("testdata/docs");
		Loader ldr = new Loader(swords);
		List<DocumentEntity> docs = ldr.load(dir);
		Assert.assertEquals("mismatch in number of files", 4, docs.size());

		for (DocumentEntity d : docs)
			System.out.println(d + "\n");
	}

	@Test
	public void testLoadLyricsOne() throws Exception {
		File swf = new File("resources/stopwords-long.txt");
		StopWords swords = new StopWordsFile(swf);
		File dir = new File("testdata/lyrics/taylor-swift.txt");
		Loader ldr = new Loader(swords);
		List<DocumentEntity> docs = ldr.load(dir);
		Assert.assertEquals("mismatch in number of files", 1, docs.size());

		for (DocumentEntity d : docs)
			System.out.println(d + "\n");
	}

	@Test
	public void testLoadLyricsAll() throws Exception {
		File swf = new File("resources/stopwords-long.txt");
		StopWords swords = new StopWordsFile(swf);
		File dir = new File("testdata/lyrics");
		Loader ldr = new Loader(swords);
		List<DocumentEntity> docs = ldr.load(dir);

		// Ultimate Answer to the Ultimate Question of Life, the Universe, and
		// Everything
		Assert.assertEquals("mismatch in number of files", 42, docs.size());

		for (DocumentEntity d : docs)
			System.out.println(d + "\n");
	}

	@Test
	public void testLyricCompare() throws Exception {
		File swf = new File("resources/stopwords-long.txt");
		StopWords swords = new StopWordsFile(swf);
		File dir = new File("testdata/lyrics");
		Loader ldr = new Loader(swords);
		List<DocumentEntity> docs = ldr.load(dir);

		for (int n = 0, N = docs.size(); n < N; n++) {
			DocumentEntity d = docs.get(n);
			for (DocumentEntity d2 : docs) {
				double rank = d.similarity(d2);
				if (d != d2 && rank > 0.3)
					System.out.println(d.getName() + " to " + d2.getName() + " matches " + rank);
			}
		}
	}

	@Test
	public void testLoadDocsAll() throws Exception {
		File swf = new File("resources/stopwords-long.txt");
		StopWords swords = new StopWordsFile(swf);
		File dir = new File("testdata/docs");
		Loader ldr = new Loader(swords);
		List<DocumentEntity> docs = ldr.load(dir);

		Assert.assertEquals("mismatch in number of files", 5, docs.size());

		for (DocumentEntity d : docs)
			System.out.println(d + "\n");
	}

	@Test
	public void testLoadDocsAllCSV() throws Exception {
		File swf = new File("resources/stopwords-long.txt");
		StopWords swords = new StopWordsFile(swf);
		File dir = new File("testdata/docs");
		Loader ldr = new Loader(swords);
		List<DocumentEntity> docs = ldr.load(dir);

		Assert.assertEquals("mismatch in number of files", 5, docs.size());

		for (DocumentEntity d : docs) {
			System.out.println(d.csvHeader());
			System.out.println(d.csvData());
			System.out.println("\n\n\n\n");
		}

	}
}
