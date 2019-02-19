package com.ccby.yobee.model.coverages;

/**
 * 中国太平车险账户信息
 * 
 * @author zhaohongda
 */
public class TAIPINGAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;
	private String pass;
	private String staff_code;// 中介人协议号
	private String agent_no;// 子协议号
	private String agent_point;// 决策单元代码
	private String domestic_vehicle_rate;// 国产车指定专修费率
	private String imported_vehicle_rate;// 进口车指定专修费率

	/**
	 * @param user
	 * @param pass
	 * @param staff_code
	 * @param agent_no
	 * @param agent_point
	 * @param domestic_vehicle_rate
	 * @param imported_vehicle_rate
	 */
	public TAIPINGAccount(String user, String pass, String staff_code, String agent_no, String agent_point, String domestic_vehicle_rate, String imported_vehicle_rate) {
		super();
		this.user = user;
		this.pass = pass;
		this.staff_code = staff_code;
		this.agent_no = agent_no;
		this.agent_point = agent_point;
		this.domestic_vehicle_rate = domestic_vehicle_rate;
		this.imported_vehicle_rate = imported_vehicle_rate;
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
	 * @return the domestic_vehicle_rate
	 */
	public String getDomestic_vehicle_rate() {
		return domestic_vehicle_rate;
	}

	/**
	 * @param domestic_vehicle_rate
	 *            the domestic_vehicle_rate to set
	 */
	public void setDomestic_vehicle_rate(String domestic_vehicle_rate) {
		this.domestic_vehicle_rate = domestic_vehicle_rate;
	}

	/**
	 * @return the imported_vehicle_rate
	 */
	public String getImported_vehicle_rate() {
		return imported_vehicle_rate;
	}

	/**
	 * @param imported_vehicle_rate
	 *            the imported_vehicle_rate to set
	 */
	public void setImported_vehicle_rate(String imported_vehicle_rate) {
		this.imported_vehicle_rate = imported_vehicle_rate;
	}

}
