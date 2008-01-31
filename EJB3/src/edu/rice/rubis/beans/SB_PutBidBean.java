package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used to build the html form to put a bid.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_PutBidBean")
@Remote(SB_PutBid.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_PutBidBean implements SB_PutBid 
{
  @PersistenceContext
  private EntityManager em;
  @EJB
  private SB_AuthLocal auth;
  @EJB
  private SB_ViewItem viewItem;


  /**
   * Authenticate the user and get the information to build the html form.
   *
   * @return a string in html format
   * @since 1.1
   */
  public String getBiddingForm(Integer itemId, String username, String password) throws RemoteException 
  {
    UserBean user = null;
    String html = "";

    // Authenticate the user who want to comment
    if ((username != null && !username.equals("")) || (password != null && !password.equals("")))
    {
      try 
      {
        user = auth.authenticate(username, password);
      }
      catch (Exception e)
      {
        throw new RemoteException("Authentication failed: " +e);
      }
      if (user == null)
      {
         html = (" You don't have an account on RUBiS!<br>You have to register first.<br>");
         return html;
      }
    }

    try
    {
      html = viewItem.getItemDescription(itemId, user.getId());
    } 
    catch (Exception e) 
    {
      throw new RemoteException("Exception getting the item information: "+ e +"<br>");
    }

    return html;
  }
}
