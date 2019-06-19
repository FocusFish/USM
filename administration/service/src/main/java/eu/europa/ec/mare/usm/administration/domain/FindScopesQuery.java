package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A query which contains the criteria for scope search 
 */
public class FindScopesQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	private String scopeName;
	private String applicationName;
	private String status;
	private Paginator paginator;

	/**
	 * Creates a new instance.
	 */
	public FindScopesQuery() {
	}
	
	/**
	 * Gets the value of status
	 *
	 * @return the value of status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the value of status
	 *
	 * @param status new value of status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Gets the value of scope's name
	 *
	 * @return the value of scope's name
	 */
	public String getScopeName() {
		return scopeName;
	}

	/**
	 * Sets the value of scope's name
	 *
	 * @param scopeName new value of scope's name
	 */
	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}
	
	/**
	 * Gets the value of application's name
	 *
	 * @return the value of application's name
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * Sets the value of application's name
	 *
	 * @param applicationName new value of application's name
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	/**
	 * Gets the value of paginator
	 *
	 * @return the value of paginator
	 */
	public Paginator getPaginator() {
		return paginator;
	}

	/**
	 * Sets the value of paginator
	 *
	 * @param paginator new value of paginator
	 */
	public void setPaginator(Paginator paginator) {
		this.paginator = paginator;
	}


	/**
	 * Formats a human-readable view of this instance.
	 * 
	 * @return a human-readable view
	 */
	@Override
	public String toString() {
		return "FindUsersQuery{" + 
            "scopeName=" + scopeName +
            ", applicationName=" + applicationName +
            ", status=" + status + 
            ", paginator=" + paginator +
            '}';
	}

}