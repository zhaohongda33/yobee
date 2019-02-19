package com.ccby.yobee.model.coverages;

/**
 * 中煤保险账户信息
 * 
 * @author zhaohongda
 */
public class ZMBXAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;
	private String pass;
//	private String proxy_id;// 代理人ID
	private String belong_org;// 业务归属机构
	private String short_name;// 账号简称
	private String agent_point;// 网点代码
	private String staff_code;// 业务员
	private String belong_department;// 保单归属地（地市）
	private String agent_no;// 保单归属地（区县）

	/**
	 * @param user
	 * @param pass
	 * @param proxy_id 代理人ID
	 * @param belong_org 业务归属机构
	 * @param short_name 账号简称
	 * @param agent_point 网点代码
	 * @param staff_code 业务员
	 * @param belong_department 保单归属地（地市）
	 * @param agent_no 保单归属地（区县）
	 */
	public ZMBXAccount(String user, String pass, String proxy_id, String belong_org, String short_name, String agent_point, String staff_code, String belong_department, String agent_no) {
		super();
		this.user = user;
		this.pass = pass;
//		this.proxy_id = proxy_id;
		this.belong_org = belong_org;
		this.short_name = short_name;
		this.agent_point = agent_point;
		this.staff_code = staff_code;
		this.belong_department = belong_department;
		this.agent_no = agent_no;
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

//	/**
//	 * @return the proxy_id
//	 */
//	public String getProxy_id() {
//		return proxy_id;
//	}
//
//	/**
//	 * @param proxy_id
//	 *            the proxy_id to set
//	 */
//	public void setProxy_id(String proxy_id) {
//		this.proxy_id = proxy_id;
//	}

	/**
	 * @return the belong_org
	 */
	public String getBelong_org() {
		return belong_org;
	}

	/**
	 * @param belong_org
	 *            the belong_org to set
	 */
	public void setBelong_org(String belong_org) {
		this.belong_org = belong_org;
	}

	/**
	 * @return the short_name
	 */
	public String getShort_name() {
		return short_name;
	}

	/**
	 * @param short_name
	 *            the short_name to set
	 */
	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}

	/**
	 * @return the agent_point
	 */
	public String getAgent_point() {
		return agent_point;
	}

	/**
	 * @param agent_point
	 *            the agent_point to set
	 */
	public void setAgent_point(String agent_point) {
		this.agent_point = agent_point;
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
	 * @return the agent_no
	 */
	public String getAgent_no() {
		return agent_no;
	}

	/**
	 * @param agent_no
	 *            the agent_no to set
	 */
	public void setAgent_no(String agent_no) {
		this.agent_no = agent_no;
	}

}
