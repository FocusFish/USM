/*
 * Developed by the European Commission - Directorate General for Maritime 
 * Affairs and Fisheries © European Union, 2015-2016.
 * 
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite.
 * The IFDM Suite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * The IFDM Suite is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details. You should have received a copy of the GNU General Public 
 * License along with the IFDM Suite. If not, see http://www.gnu.org/licenses/.
 */
package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A query for the retrieval of a role.
 */
public class GetRoleQuery implements Serializable {
  private static final long serialVersionUID = 1L;
  private Long roleId;

  /**
   * Creates a new instance.
   */
  public GetRoleQuery() {
  }
  
  public GetRoleQuery(Long roleId) {
	  this.roleId=roleId;
  }
  
  /**
   * Gets the value of role_id
   *
   * @return the value of role_id
   */
  public Long getRoleId() {
	return roleId;
  }

  /**
   * Sets the value of role_id
   *
   * @param roleId new value of role_id
   */
  public void setRoleId(Long roleId) {
	this.roleId = roleId;
  }

  /**
   * Formats a human-readable view of this instance.
   *
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "GetRoleQuery{"
            + "roleId=" + roleId
            + '}';
  }

}