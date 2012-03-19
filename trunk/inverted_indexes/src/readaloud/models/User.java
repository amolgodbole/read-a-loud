package readaloud.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * In this hibernate example we have the person owning the contact relationship.
 * This is a traditional OO type of organization.
 * 
 * @author gash
 * 
 */
@Entity
@Table(name = "Reader")
public class User {

	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "userId", updatable = false, nullable = false)
	private String id;
	
	private String firstName;
	private String lastName;
	private String password;
	private Date created;


	@Version
	private int version;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return id;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}



	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(id=");
		sb.append(id);
		sb.append(") ");
		sb.append(firstName);
		sb.append(" ");
		sb.append(lastName);
		return sb.toString();
	}
}
