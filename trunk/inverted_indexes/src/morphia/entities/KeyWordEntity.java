package morphia.entities;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity("Keyword")
public class KeyWordEntity {
	 @Id
	 ObjectId id;
	 
	public String word;
	public List<Integer> position = new ArrayList<Integer>();
	
	public KeyWordEntity()
	{
		
	}
	
	public KeyWordEntity(String word, int position) {
		this.word = word;
		add(position);
	}

	public void add(int position) {
		this.position.add(position);
	}
	
	
	
	public double stdDev() {
		// shouldn't happen
		if (position.size() == 0)
			return 0;

		double sd = 0.0f;

		double ave = 0.0;
		for (Integer n : position)
			ave += n;
		ave /= position.size();

		for (Integer n : position)
			sd += Math.pow((ave - n), 2);
		sd /= position.size();

		return Math.sqrt(sd);
	}

}
