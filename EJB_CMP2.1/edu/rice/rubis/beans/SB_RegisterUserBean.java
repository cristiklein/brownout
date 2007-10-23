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
 * This is a stateless session bean used to register a new user.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */

public class SB_RegisterUserBean implements SessionBean 
{
  protected SessionContext sessionContext;
  protected Context initialContext = null;

  /**
   * Create a new user.
   *
   * @param firstname user's first name
   * @param lastname user's last name
   * @param nickname user's nick name
   * @param email user's email
   * @param password user's password
   * @param regionName name of the region where the user live
   * @return a string in html format
   * @since 1.1
   */
  public String createUser(String firstname, String lastname, String nickname, String email, String password, String regionName) throws RemoteException
  {
    String html = "";
    UserLocalHome uHome;
    UserLocal user;
    RegionLocalHome regionHome;
    RegionLocal region;
    int userId;
    String creationDate;

      try 
      {
        // Connecting to  region Home thru JNDI
        regionHome = (RegionLocalHome)initialContext.lookup("java:comp/env/ejb/Region");
      } 
      catch (Exception e)
      {
        throw new RemoteException("Cannot lookup Region: " +e+"<br>\n");
      }
      try
      {
        region = regionHome.findByName(regionName);
      }
      catch (Exception e)
      {
        throw new RemoteException(" Region "+regionName+" does not exist in the database!<br>(got exception: " +e+")<br>\n");
      }
    
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
        user = uHome.findByNickName(nickname);
        /* If an exception has not be thrown at this point, it means that
           the nickname already exists. */
        html = "The nickname you have choosen is already taken by someone else. Please choose a new nickname.<br>";
        return html;
      }
      catch (Exception fe)
      {
        try
        {
          user = uHome.create(firstname, lastname, nickname, email, password, region);
          user = uHome.findByNickName(nickname);
          userId = user.getId().intValue();
          creationDate = user.getCreationDate();
          html = "User id       :"+userId+"<br>\n" +
            "Creation date :"+creationDate+"<br>\n" +
            "Rating        :"+user.getRating()+"<br>\n" +
            "Balance       :"+user.getBalance()+"<br>\n";
        }
        catch (Exception e)
        {
          throw new RemoteException("User registration failed (got exception: " +e+")<br>");
        }
        return html;
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
