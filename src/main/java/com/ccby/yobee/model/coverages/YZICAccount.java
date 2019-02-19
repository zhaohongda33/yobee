package com.ccby.yobee.model.coverages;

/**
 * 燕赵车险账户信息
 * 
 * @author zhaohongda
 */
public class YZICAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;
	private String pass;

	/**
	 * @param user
	 * @param pass
	 */
	public YZICAccount(String user, String pass) {
		super();
		this.user = user;
		this.pass = pass;
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

}
