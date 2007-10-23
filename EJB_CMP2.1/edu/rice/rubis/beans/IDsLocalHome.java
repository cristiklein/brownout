package edu.rice.rubis.beans;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import java.rmi.RemoteException;
import java.util.Collection;

/** This is the LocalHome Interface of the IDs Bean */

public interface IDsLocalHome extends EJBLocalHome {

  /**
   * This method is used to create a new IDs Bean.
   */
  public IDsLocal create() throws CreateException;


  /**
   * This method is used to retrieve a Category Bean from its primary key,
   * that is to say its id.
   *
   * @param id IDs id (primary key)
   *
   * @return the IDs if found else null
   */
  public IDsLocal findByPrimaryKey(IDsPK id) throws FinderException;
}
