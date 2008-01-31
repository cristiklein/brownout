package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;

/**
 * This is a stateless session bean used to provides user authentication 
 * services to servlets.
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless
@Local(SB_AuthLocal.class)
public class SB_AuthBean implements SB_AuthLocal 
{
  @PersistenceContext
  private EntityManager em;

  /**
   * Describe <code>authenticate</code> method here.
   *
   * @param name user nick name
   * @param password user password
   * @return a user or null if the password in incorrect
   */
  public UserBean authenticate (String name, String password)
  {
    UserBean user;

    // get the User
    try
    {
      Query q = em.createNamedQuery("userByNickName");
      q.setParameter(1, name);
      user = (UserBean)q.getSingleResult();
      String pwd = user.getPassword();
      if (!pwd.equals(password))
      {
        user = null;
      }
    }
    catch (Exception e)
    {
      throw new EJBException(" User "+name+" does not exist in the database!<br>(got exception: " +e);
    }

    return user;
  }
}
