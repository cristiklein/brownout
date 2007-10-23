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
import java.util.Enumeration;
import java.util.Iterator;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Date;

/**
 * This is a stateless session bean used to get the list of items
 * that belong to a specific category. 
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */

public class SB_SearchItemsByCategoryBean implements SessionBean 
{
  protected SessionContext sessionContext;
  protected Context initialContext = null;


  /**
   * Get the items in a specific category.
   *
   * @return a string that is the list of items in html format
   * @since 1.1
   */
  public String getItems(Integer categoryId, int page, int nbOfItems) throws RemoteException
  {
    
    Collection list;
    CategoryLocal category;
    StringBuffer html = new StringBuffer(); 

    try
    {
      CategoryLocalHome cHome = (CategoryLocalHome)initialContext.lookup("java:comp/env/ejb/Category");
      category = cHome.findByPrimaryKey(categoryId);
    }
    catch (Exception e)
    {
      throw new RemoteException("Cannot get category: " +e);
    }

    try 
    {
      list = category.getCurrentItemsInCategory(page*nbOfItems, nbOfItems);
      Iterator it = list.iterator();
      while (it.hasNext()) 
      {
        ItemLocal item = (ItemLocal)it.next();
        html.append(printItem(item));
      }
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot get items list: " +e);
    }
    return html.toString();
  }


  /** 
   * Item related printed function
   *
   * @param item the item to display
   * @return a string in html format
   * @since 1.1
   */
  public String printItem(ItemLocal item) throws RemoteException
  {
    try
    {
      return item.printItem();
    }
    catch (EJBException re)
    {
      throw new EJBException("Unable to print Item (exception: "+re+")<br>\n");
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
