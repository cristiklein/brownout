package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used to build the html form to buy an item.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_BuyNowBean")
@Remote(SB_BuyNow.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_BuyNowBean implements SB_BuyNow 
{
  @PersistenceContext
  private EntityManager em;
  @EJB
  private SB_AuthLocal auth;

  /**
   * Authenticate the user and get the information to build the html form.
   *
   * @return a string in html format
   * @since 1.1
   */
  public String getBuyNowForm(Integer itemId, String username, String password) throws RemoteException
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
    // Try to find the Item corresponding to the Item ID
    try
    {
      ItemBean item = (ItemBean)em.find(ItemBean.class, itemId);
      if (item == null)
        throw new Exception("Entity does not exist");

      // Display the form for buying the item
      html = printItemDescriptionToBuyNow(item, user.getId());
    } 
    catch (Exception e) 
    {
      throw new RemoteException("Exception getting the item information: "+ e +"<br>");
    }

    return html;
  }

  /**
   * Print the full description of an item and the buy now option
   *
   * @param item an <code>Item</code> value
   * @param userId an authenticated user id
   */
  public String printItemDescriptionToBuyNow(ItemBean item, int userId) throws RemoteException
  {
    String html = "";
    try
    {
      String itemName = item.getName();
      html = html + "<TABLE width=\"100%\" bgcolor=\"#CCCCFF\">\n<TR><TD align=\"center\" width=\"100%\"><FONT size=\"4\" color=\"#000000\"><B>You are ready to buy this item: "+itemName+"</B></FONT></TD></TR>\n</TABLE><p>\n" + item.printItemDescriptionToBuyNow(userId);
      ;
    }
    catch (Exception re)
    {
      throw new EJBException("Unable to print Item description (exception: "+re+")<br>\n");
    }
    return html;
  }
}
