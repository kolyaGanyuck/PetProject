package customloginapplication.dto;

public class UserDto {
	
	
	private String username;
	private String password;
	private String fullname;
	private String phoneNumber;
	private String city;
	private boolean active;
	private String role;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public UserDto() {
	
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public UserDto(String username, String password, String fullname, String phoneNumber, String city, boolean active) {
		this.username = username;
		this.password = password;
		this.fullname = fullname;
		this.phoneNumber = phoneNumber;
		this.city = city;
		this.active = active;
	}

	public UserDto(String username, String password, String fullname) {
		
		this.username = username;
		this.password = password;
		this.fullname = fullname;

	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getFullname() {
		return fullname;
	}


	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	
	
	
	

}
