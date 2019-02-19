package com.ccby.yobee.model.coverages;

/**
 *中华联合账户信息
 * 
 * @author zhaohongda
 */
public class CICPAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;
	private String pass;
	private String belong_person;// 中华联合必填字段，归属人
	private String belong_department;// 中华联合必填字段，归属部门
	private String business_src;// 中华联合必填字段，业务来源
	private String operator_no;// 中华联合必填字段，中介代码
	private String agent_point;// 中华联合必填字段，服务代码
	private String belong_org;// 承保机构
	private String domestic_vehicle_rate;// 国产车指定专修费率
	private String imported_vehicle_rate;// 进口车指定专修费率
	private String agent_zcjg;// 仲裁机构
	private String agent_zycl;// 争议处理

	/**
	 * @param user
	 * @param pass
	 * @param belong_person 中华联合必填字段，归属人
	 * @param belong_department 中华联合必填字段，归属部门
	 * @param business_src 中华联合必填字段，业务来源
	 * @param operator_no 中华联合必填字段，中介代码
	 * @param agent_point 中华联合必填字段，服务代码
	 * @param belong_org 承保机构
	 * @param domestic_vehicle_rate 国产车指定专修费率
	 * @param imported_vehicle_rate 进口车指定专修费率
	 * @param agent_zcjg 仲裁机构
	 * @param agent_zycl 争议处理
	 */
	public CICPAccount(String user, String pass, String belong_person, String belong_department, String business_src, String operator_no, String agent_point, String belong_org, String domestic_vehicle_rate, String imported_vehicle_rate, String agent_zcjg, String agent_zycl) {
		super();
		this.user = user;
		this.pass = pass;
		this.belong_person = belong_person;
		this.belong_department = belong_department;
		this.business_src = business_src;
		this.operator_no = operator_no;
		this.agent_point = agent_point;
		this.belong_org = belong_org;
		this.imported_vehicle_rate = imported_vehicle_rate;
		this.domestic_vehicle_rate = domestic_vehicle_rate;
		this.agent_zcjg = agent_zcjg;
		this.agent_zycl = agent_zycl;
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
	 * @return the domestic_vehicle_rate
	 */
	public String getDomestic_vehicle_rate() {
		return domestic_vehicle_rate;
	}

	/**
	 * @param domestic_vehicle_rate the domestic_vehicle_rate to set
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
	 * @param imported_vehicle_rate the imported_vehicle_rate to set
	 */
	public void setImported_vehicle_rate(String imported_vehicle_rate) {
		this.imported_vehicle_rate = imported_vehicle_rate;
	}

	/**
	 * @return the agent_zcjg
	 */
	public String getAgent_zcjg() {
		return agent_zcjg;
	}

	/**
	 * @param agent_zcjg the agent_zcjg to set
	 */
	public void setAgent_zcjg(String agent_zcjg) {
		this.agent_zcjg = agent_zcjg;
	}

	/**
	 * @return the agent_zycl
	 */
	public String getAgent_zycl() {
		return agent_zycl;
	}

	/**
	 * @param agent_zycl the agent_zycl to set
	 */
	public void setAgent_zycl(String agent_zycl) {
		this.agent_zycl = agent_zycl;
	}

}
