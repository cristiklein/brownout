package edu.rice.rubis.beans;

import java.rmi.RemoteException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;
import java.io.Serializable;

/**
 * This is a stateless session bean used to register a new item.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */

public class SB_RegisterItemBean implements SessionBean 
{
  protected SessionContext sessionContext;
  protected Context initialContext = null;

  /**
   * Create a new item.
   *
   * @param name name of the item
   * @param description description of the item
   * @param initialPrice initial price
   * @param quantity quantity of items
   * @param reservePrice reserve price
   * @param buyNow price to buy the item without auction
   * @param duration duration of the auction
   * @param userdId seller's id
   * @param categoryId id of the category the item belong to
   * @since 1.1
   */
  public void createItem(String name, String description, float initialPrice, int quantity, float reservePrice, float buyNow, int duration, Integer userId, Integer categoryId) throws RemoteException
  {
    UserLocalHome uHome;
    UserLocal user;
    CategoryLocalHome cHome;
    CategoryLocal category;
    ItemLocalHome iHome;
    ItemLocal item;
    String creationDate;

      try
      {
        uHome = (UserLocalHome)initialContext.lookup("java:comp/env/ejb/User");
      }
      catch (Exception e)
      {
        throw new RemoteException("Cannot lookup User: " +e+"<br>");
      }
      try
      {
        user = uHome.findByPrimaryKey(userId);
      }
      catch (Exception e)
      {
        throw new RemoteException("Cannot find user: " +e+"<br>");
      }

      try
      {
        cHome = (CategoryLocalHome)initialContext.lookup("java:comp/env/ejb/Category");
      }
      catch (Exception e)
      {
        throw new RemoteException("Cannot lookup Category: " +e+"<br>");
      }
      try
      {
        category = cHome.findByPrimaryKey(categoryId);
      }
      catch (Exception e)
      {
        throw new RemoteException("Cannot find category: " +e+"<br>");
      }

      try 
      {
        iHome = (ItemLocalHome)initialContext.lookup("java:comp/env/ejb/Item");
      } 
      catch (Exception e)
      {
        throw new RemoteException("Cannot lookup Item: " +e+"<br>");
      }
      try
      {
        item = iHome.create(name, description, initialPrice, quantity, reservePrice, buyNow, duration, user, category);
      }
      catch (Exception e)
      {
        throw new RemoteException("Item registration failed (got exception: " +e+")<br>");
      }
  }
  




  // ======================== EJB related methods ============================

  /**
   * This method is empty for a stateless session bean
   */
  public void ejbCreate() throws CreateException, RemoteException
  {
  }

  /** This method is empty for a stateless session bean */
  public void ejbActivate() throws RemoteException {}
  /** This method is empty for a stateless session bean */
  public void ejbPassivate() throws RemoteException {}
  /** This method is empty for a stateless session bean */
  public void ejbRemove() throws RemoteException {}


  /** 
   * Sets the associated session context. The container calls this method 
   * after the instance creation. This method is called with no transaction context. 
   * We also retrieve the Home interfaces of all RUBiS's beans.
   *
   * @param sessionContext - A SessionContext interface for the instance. 
   * @exception RemoteException - Thrown if the instance could not perform the function 
   *            requested by the container because of a system-level error. 
   */
  public void setSessionContext(SessionContext sessionContext) throws RemoteException
  {
    this.sessionContext = sessionContext;
    
    try
    {
      initialContext = new InitialContext(); 
    }
    catch (Exception e) 
    {
      throw new RemoteException("Cannot get JNDI InitialContext");
    }
  }

}
