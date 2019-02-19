package com.ccby.yobee.model.coverages;

/**
 * 太平洋（新）账户信息
 * 
 * @author zhaohongda
 */
public class CPICNAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;
	private String pass;
	private String agent_type;// 太平洋（新）必填字段，终端
	private String staff_code;// 太平洋（新）必填字段，代理人

	/**
	 * @param user
	 * @param pass
	 * @param agent_type
	 * @param staff_code
	 */
	public CPICNAccount(String user, String pass, String agent_type, String staff_code) {
		super();
		this.user = user;
		this.pass = pass;
		this.agent_type = agent_type;
		this.staff_code = staff_code;
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
	 * @return the agent_type
	 */
	public String getAgent_type() {
		return agent_type;
	}

	/**
	 * @param agent_type
	 *            the agent_type to set
	 */
	public void setAgent_type(String agent_type) {
		this.agent_type = agent_type;
	}

	/**
	 * @return the staff_code
	 */
	public String getStaff_code() {
		return staff_code;
	}

	/**
	 * @param staff_code
	 *            the staff_code to set
	 */
	public void setStaff_code(String staff_code) {
		this.staff_code = staff_code;
	}

}
