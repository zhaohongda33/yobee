package com.ccby.yobee.model.coverages;

/**
 * 天安车险账户信息
 * 
 * @author zhaohongda
 */
public class TAICAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;
	private String pass;
	private String belong_department;// 业务归属
	private String operator_no;// 中介人协议号

	/**
	 * @param user
	 * @param pass
	 * @param belong_department
	 * @param operator_no
	 */
	public TAICAccount(String user, String pass, String belong_department, String operator_no) {
		super();
		this.user = user;
		this.pass = pass;
		this.belong_department = belong_department;
		this.operator_no = operator_no;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @param pass
	 *            the pass to set
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * @return the belong_department
	 */
	public String getBelong_department() {
		return belong_department;
	}

	/**
	 * @param belong_department
	 *            the belong_department to set
	 */
	public void setBelong_department(String belong_department) {
		this.belong_department = belong_department;
	}

	/**
	 * @return the operator_no
	 */
	public String getOperator_no() {
		return operator_no;
	}

	/**
	 * @param operator_no
	 *            the operator_no to set
	 */
	public void setOperator_no(String operator_no) {
		this.operator_no = operator_no;
	}
}
