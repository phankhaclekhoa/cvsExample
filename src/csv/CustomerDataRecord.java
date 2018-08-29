package csv;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerDataRecord {
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String companyName;
	private String customerNumber;
	private String notes;
	private int salutation;
	private String street;
	private String postcode;
	private String city;
	private String country;
	
	private int addressType;
	// customer type
	private int type;
}
