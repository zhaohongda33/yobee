package com.ccby.yobee.model.coverages;

/**
 * 国寿财账户信息
 * 
 * @author zhaohongda
 */
public class GPICAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;
	private String pass;
	private String company_code;// 国寿财必填字段，归属机构
	private String tax_reg_no;// 国寿财必填字段，税务登记证号
	private String channel;// 国寿财必填字段，销售渠道
	private String belong_department;// 国寿财必填字段，归属部门
	private String belong_person;// 国寿财必填字段，业务员/产险专员
	private String business_src;// 国寿财必填字段， 业务来源
	private String agent_point;// 国寿财必填字段， 代理人/经纪人/寿险机构

	/**
	 * @param user
	 * @param pass
	 * @param company_code
	 * @param tax_reg_no
	 * @param channel
	 * @param belong_department
	 * @param belong_person
	 * @param business_src
	 * @param agent_point
	 */
	public GPICAccount(String user, String pass, String company_code, String tax_reg_no, String channel, String belong_department, String belong_person, String business_src, String agent_point) {
		super();
		this.user = user;
		this.pass = pass;
		this.company_code = company_code;
		this.tax_reg_no = tax_reg_no;
		this.channel = channel;
		this.belong_department = belong_department;
		this.belong_person = belong_person;
		this.business_src = business_src;
		this.agent_point = agent_point;
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
	 * @return the tax_reg_no
	 */
	public String getTax_reg_no() {
		return tax_reg_no;
	}

	/**
	 * @param tax_reg_no
	 *            the tax_reg_no to set
	 */
	public void setTax_reg_no(String tax_reg_no) {
		this.tax_reg_no = tax_reg_no;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel
	 *            the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
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

}
