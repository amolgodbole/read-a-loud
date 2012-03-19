package morphia.entities;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;
import com.google.code.morphia.utils.IndexDirection;

@Entity("invertIndex")
public class InvertIndexEntity {
	 @Id
	 ObjectId id;

	 @Indexed(value = IndexDirection.ASC, name = "bandName", unique = true)
	 String word;
	 
	 @Reference
	 List<DocumentEntity> documents = new ArrayList<DocumentEntity>();

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public List<DocumentEntity> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentEntity> documents) {
		this.documents = documents;
	}
	 
}
