package com.ccby.yobee.model.coverages;

/**
 * 阳光保险账户信息
 * 
 * @author zhaohongda
 */
public class YGICAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;// 帐号
	private String pass;// 密码
	private String company_code;// 登录机构代码
	private String belong_department;// 归属部门
	private String belong_person;// 归属业务员
	private String agent_point;// 服务区域
	private String business_src;// 业务来源
	private String staff_code;// 代理人/经纪人/渠道
	private String domestic_vehicle_rate;// 国产车指定专修费率
	private String imported_vehicle_rate;// 进口车指定专修费率

	/**
	 * @param user 帐号
	 * @param pass 密码
	 * @param company_code
	 *            登录机构代码
	 * @param belong_department
	 *            归属部门
	 * @param belong_person
	 *            归属业务员
	 * @param agent_point
	 *            服务区域
	 * @param business_src
	 *            业务来源
	 * @param staff_code
	 *            代理人/经纪人/渠道
	 * @param domestic_vehicle_rate
	 *            国产车指定专修费率
	 * @param imported_vehicle_rate
	 *            进口车指定专修费率
	 */
	public YGICAccount(String user, String pass, String company_code, String belong_department, String belong_person, String agent_point, String business_src, String staff_code,
			String domestic_vehicle_rate, String imported_vehicle_rate) {
		super();
		this.user = user;
		this.pass = pass;
		this.company_code = company_code;
		this.belong_department = belong_department;
		this.belong_person = belong_person;
		this.agent_point = agent_point;
		this.business_src = business_src;
		this.staff_code = staff_code;
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
	 * @return the company_code
	 */
	public String getCompany_code() {
		return company_code;
	}

	/**
	 * @param company_code
	 *            the company_code to set
	 */
	public void setCompany_code(String company_code) {
		this.company_code = company_code;
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
	 * @return the belong_person
	 */
	public String getBelong_person() {
		return belong_person;
	}

	/**
	 * @param belong_person
	 *            the belong_person to set
	 */
	public void setBelong_person(String belong_person) {
		this.belong_person = belong_person;
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
	 * @return the business_src
	 */
	public String getBusiness_src() {
		return business_src;
	}

	/**
	 * @param business_src
	 *            the business_src to set
	 */
	public void setBusiness_src(String business_src) {
		this.business_src = business_src;
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
