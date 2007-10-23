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
import javax.transaction.UserTransaction;

/**
 * This is a stateless session bean used to create e new comment for a user.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */

public class SB_StoreCommentBean implements SessionBean 
{
  protected SessionContext sessionContext;
  protected Context initialContext = null;

  /**
   * Create a new comment and update the rating of the user.
   *
   * @param fromId id of the user posting the comment
   * @param toId id of the user who is the subject of the comment
   * @param itemId id of the item related to the comment
   * @param rating value of the rating for the user
   * @param comment text of the comment
   * @since 1.1
   */
  public void createComment(Integer fromId, Integer toId, Integer itemId, int rating, String comment) throws RemoteException
  {
    UserLocalHome uHome;
    UserLocal fromUser, toUser;
    ItemLocalHome iHome;
    ItemLocal item;
    CommentLocalHome cHome;
    
    // Lookup User
    try 
    {
      uHome = (UserLocalHome)initialContext.lookup("java:comp/env/ejb/User");
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot lookup User: " +e+"<br>");
    }
    // Try to find the fromUser
    try 
    {
      fromUser = uHome.findByPrimaryKey(fromId);
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot find User ("+fromId+"): " +e+"<br>");
    }
    // Try to find the toUser
    try 
    {
      toUser = uHome.findByPrimaryKey(toId);
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot find User ("+toId+"): " +e+"<br>");
    }
    
    // Lookup Item
    try 
    {
      iHome = (ItemLocalHome)initialContext.lookup("java:comp/env/ejb/Item");
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot lookup Item: " +e+"<br>");
    }
    // Try to find the item
    try 
    {
      item = iHome.findByPrimaryKey(itemId);
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot find Item ("+itemId+"): " +e+"<br>");
    }
    
    // Lookup Comment
    try 
    {
      cHome = (CommentLocalHome)initialContext.lookup("java:comp/env/ejb/Comment");
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot lookup Comment: " +e+"<br>");
    }
    // Insert the comment
    try
    {
      CommentLocal c = cHome.create(fromUser, toUser, item, rating, comment);
      toUser.updateRating(rating);
    }
    catch (Exception e)
    {
      throw new RemoteException("Error while storing the comment (got exception: " +e+")<br>");
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
