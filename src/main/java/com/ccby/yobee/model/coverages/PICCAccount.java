package com.ccby.yobee.model.coverages;

/**
 * 人保账户信息
 * 
 * @author zhaohongda
 */
public class PICCAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;
	private String pass;
	private String belong_department;// 归属部门
	private String belong_person;// 归属人
	private String operator_no;// 经办人工号
	private String business_src;// 业务来源
	private String agent_point;// 渠道代码
	private String staff_code;// 验车人
	private String tax_code;// 税务机关代码
	private String belong_org;// 出单机构
	private String tax_reg_no;// 税务登记证号
	

	/**
	 * @param user
	 * @param pass
	 * @param belong_department 归属部门
	 * @param belong_person 归属人
	 * @param operator_no 经办人工号
	 * @param business_src 业务来源
	 * @param agent_point 渠道代码
	 * @param staff_code 验车人
	 * @param tax_code 税务机关代码
	 * @param belong_org 出单机构
	 * @param tax_reg_no 税务登记证号
	 */
	public PICCAccount(String user, String pass, String belong_department, String belong_person, String operator_no, String business_src, String agent_point, String staff_code, String tax_code, String belong_org, String tax_reg_no) {
		super();
		this.user = user;
		this.pass = pass;
		this.belong_department = belong_department;
		this.belong_person = belong_person;
		this.operator_no = operator_no;
		this.business_src = business_src;
		this.agent_point = agent_point;
		this.staff_code = staff_code;
		this.tax_code = tax_code;
		this.belong_org = belong_org;
		this.tax_reg_no = tax_reg_no;
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
	 * @return the tax_code
	 */
	public String getTax_code() {
		return tax_code;
	}

	/**
	 * @param tax_code
	 *            the tax_code to set
	 */
	public void setTax_code(String tax_code) {
		this.tax_code = tax_code;
	}

	/**
	 * @return the belong_org
	 */
	public String getBelong_org() {
		return belong_org;
	}

	/**
	 * @param belong_org the belong_org to set
	 */
	public void setBelong_org(String belong_org) {
		this.belong_org = belong_org;
	}

	/**
	 * @return the tax_reg_no
	 */
	public String getTax_reg_no() {
		return tax_reg_no;
	}

	/**
	 * @param tax_reg_no the tax_reg_no to set
	 */
	public void setTax_reg_no(String tax_reg_no) {
		this.tax_reg_no = tax_reg_no;
	}

}
