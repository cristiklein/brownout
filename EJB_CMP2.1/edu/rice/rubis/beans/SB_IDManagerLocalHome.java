package edu.rice.rubis.beans;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/** This is the local Home interface of the SB_IDManager Bean */

public interface SB_IDManagerLocalHome extends EJBLocalHome {

  /**
   * This method is used to create a new Bean.
   *
   * @return session bean
   */
  public SB_IDManagerLocal create() throws CreateException;

}
