package utils;

public enum Roles {
	
	VISITOR("visitor"), REG_CUSTOMER("reg_customer"), MANAGER("manager"), EMPLOYEE("employee"), NONE("none");
	
	private String role;
	
	private Roles(String role) {
		this.role = role;
	}
	
	public String toString() {
		return this.role;
	}
	
	public static Roles getRoleByString(String role) {
		if(role.equals("visitor")) {
			return VISITOR;
		} else if(role.equals("reg_customer")) {
			return REG_CUSTOMER;
		}  else if(role.equals("manager")) {
			return MANAGER;
		}  else if(role.equals("employee")) {
			return EMPLOYEE;
		}  else {
			return NONE;
		}
	}
	
}
