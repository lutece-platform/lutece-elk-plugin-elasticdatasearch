package fr.paris.lutece.plugins.elasticdatasearch.business;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FormResponseElastic {
	
	private HashMap<String,String> mapAttributes = new HashMap<>();
	private String strFormName;
	private String strStatus;
	private LocalDate dateForm;
	
	
	public HashMap<String, String> getMapAttributes() {
		return mapAttributes;
	}
	public void setMapAttributes(HashMap<String, String> mapAttributes) {
		this.mapAttributes = mapAttributes;
	}
	public String getFormName() {
		return strFormName;
	}
	public void setFormName(String strFormName) {
		this.strFormName = strFormName;
	}
	public String getStatus() {
		return strStatus;
	}
	public void setStatus(String strStatus) {
		this.strStatus = strStatus;
	}
	public LocalDate getDateForm() {
		return dateForm;
	}
	public void setDateForm(LocalDate dateForm) {
		this.dateForm = dateForm;
	}
	
	public static Comparator<FormResponseElastic> sortByAttributeKey(String key) {
	    return Comparator.comparing(
	        fr -> fr.getMapAttributes().getOrDefault(key, ""),
	        Comparator.nullsLast(String::compareToIgnoreCase)
	    );
	}
	
	public static List<FormResponseElastic> filterByAttribute(
	        List<FormResponseElastic> list,
	        String key,
	        String value) {

	    return list.stream()
	        .filter(fr -> value.equals(fr.getMapAttributes().get(key)))
	        .collect(Collectors.toList());
	}
	
}
