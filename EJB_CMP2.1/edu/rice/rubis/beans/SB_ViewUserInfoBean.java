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
import java.util.Collection;
import java.util.Iterator;
import java.net.URLEncoder;

/**
 * This is a stateless session bean used to get the information about a user.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */

public class SB_ViewUserInfoBean implements SessionBean 
{
  protected SessionContext sessionContext;
  protected Context initialContext = null;

  /**
   * Get the comment related to a specific user.
   *
   * @param userHome an <code>UserHome</code> value
   * @param userId a user id
   * @return a string in html format
   * @since 1.1
   */
  public String getComments(UserLocal user) throws RemoteException
  {
    Collection   list;
    Iterator it;
    StringBuffer html;
    CommentLocal      comment = null;

    // Try to find the comments corresponding for this user
    try 
    {
      list = user.getToComments();
      if (list.isEmpty())
       html = new StringBuffer("<h3>There is no comment yet for this user.</h3><br>");
      else
      {
        html = new StringBuffer("<br><hr><br><h3>Comments for this user</h3><br>");

        html.append(printCommentHeader());
        // Display each comment and the name of its author
        it = list.iterator();
        while (it.hasNext())
        {
          comment = (CommentLocal)it.next();
          String userName = comment.getFromUser().getNickName();
          html.append(printComment(userName, comment));
        }
        html.append(printCommentFooter());
      }
    } 
    catch (Exception e) 
    {
      throw new RemoteException("Exception getting comment list: " + e +"<br>");
    }
    return html.toString();
  }


  /**
   * Get the information about a user.
   *
   * @param userId a user id
   * @return a string in html format
   * @since 1.1
   */
  public String getUserInfo(Integer userId) throws RemoteException
  {
    StringBuffer html = new StringBuffer();
    UserLocalHome     uHome = null;
    UserLocal         user = null;
 

    // Try to find the user corresponding to the userId
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
      html.append(user.getHTMLGeneralUserInformation());
      html.append(getComments(user));
    }
    catch (Exception e)
    {
      throw new RemoteException("Cannot get user information (got exception: " +e+")<br>");
    }
    return html.toString();
  }

  /** 
   * Comment header printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printCommentHeader()
  {
    return "<DL>\n";
  }

  /** 
   * Comment printed function
   *
   * @param userName the name of the user who is the subject of the comments
   * @param comment the comment to display
   * @return a string in html format
   * @since 1.1
   */
  public String printComment(String userName, CommentLocal comment) throws RemoteException
  {
    try
    {
      return comment.printComment(userName);
    }
    catch (EJBException re)
    {
      throw new EJBException("Unable to print Comment (exception: "+re+")<br>\n");
    }
  }

  /** 
   * Comment footer printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printCommentFooter()
  {
    return "</DL>\n";
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
