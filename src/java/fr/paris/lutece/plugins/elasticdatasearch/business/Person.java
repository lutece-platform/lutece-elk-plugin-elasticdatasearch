package fr.paris.lutece.plugins.elasticdatasearch.business;

public class Person {
	
	String strName;
    String strFirstName;
    String strRegistrationNumber;

    public Person(String nom, String prenom, String matricule) {
        this.strName = nom;
        this.strFirstName = prenom;
        this.strRegistrationNumber = matricule;
    }

    @Override
    public String toString() {
        return "{ nom='" + strName + "', prenom='" + strFirstName + "', matricule='" + strRegistrationNumber + "' }";
    }

	public String getName() {
		return strName;
	}

	public void setName(String strName) {
		this.strName = strName;
	}

	public String getFirstName() {
		return strFirstName;
	}

	public void setFirstName(String strFirstName) {
		this.strFirstName = strFirstName;
	}

	public String getRegistrationNumber() {
		return strRegistrationNumber;
	}

	public void setRegistrationNumber(String strRegistrationNumber) {
		this.strRegistrationNumber = strRegistrationNumber;
	}

    
    
}
