package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used when a user buy an item.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_StoreBuyNowBean")
@Remote(SB_StoreBuyNow.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_StoreBuyNowBean implements SB_StoreBuyNow
{
  @PersistenceContext
  private EntityManager em;

  /**
   * Create a buyNow and update the item.
   *
   * @param itemId id of the item related to the comment
   * @param userId id of the buyer
   * @param qty quantity of items
   * @since 1.1
   */
  public void createBuyNow(Integer itemId, Integer userId, int qty) throws RemoteException
  {
    // Try to find the Item corresponding to the Item ID
    ItemBean item;
    try 
    {
      item = (ItemBean)em.find(ItemBean.class, itemId);
      if (item == null)
        throw new Exception("Entity does not exist");
      if (item.getQuantity() >= qty)
        item.setQuantity(item.getQuantity() - qty);
      if (item.getQuantity() == 0)
        item.setEndDate(new GregorianCalendar());
    }
    catch (Exception e)
    {
      throw new RemoteException("Cannot update Item: " +e+"<br>");
    }
    // Try to find the User corresponding to the User ID
    UserBean user;
    try 
    {
      user = (UserBean)em.find(UserBean.class, userId);
      if (user == null)
        throw new Exception("Entity does not exist");
    }
    catch (Exception e)
    {
      throw new RemoteException("Cannot find User: " +e+"<br>");
    }
    try
    {
      BuyNowBean b = new BuyNowBean(user, item, qty);
      em.persist(b);
    }
    catch (Exception e)
    {
      throw new RemoteException("Error while storing the buyNow (got exception: " +e+")<br>");
    }
  }
}
