package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used to register a new user.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_RegisterUserBean")
@Remote(SB_RegisterUser.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_RegisterUserBean implements SB_RegisterUser
{
  @PersistenceContext
  private EntityManager em;

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
    UserBean user;
    RegionBean region;
    int userId;
    Calendar creationDate;

    try
    {
      Query q = em.createNamedQuery("regionByName");
      q.setParameter(1, regionName);
      region = (RegionBean)q.getSingleResult();
    }
    catch (Exception e)
    {
      throw new RemoteException(" Region "+regionName+" does not exist in the database!<br>(got exception: " +e+")<br>\n");
    }

    try
    {
      Query q = em.createNamedQuery("userByNickName");
      q.setParameter(1, nickname);
      user = (UserBean)q.getSingleResult();
      /* If an exception has not be thrown at this point, it means that
         the nickname already exists. */
      html = "The nickname you have choosen is already taken by someone else. Please choose a new nickname.<br>";
      return html;
    }
    catch (Exception fe)
    {
      try
      {
        user = new UserBean(firstname, lastname, nickname, email, password, region);
        em.persist(user);
        Query q = em.createNamedQuery("userByNickName");
        q.setParameter(1, nickname);
        user = (UserBean)q.getSingleResult();
        userId = user.getId().intValue();
        creationDate = user.getCreationDate();
        html = "User id       :"+userId+"<br>\n" +
          "Creation date :"+TimeManagement.dateToString(creationDate)+"<br>\n" +
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
}
