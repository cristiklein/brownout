package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used to create e new bid.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_StoreBidBean")
@Remote(SB_StoreBid.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_StoreBidBean implements SB_StoreBid
{
  @PersistenceContext
  private EntityManager em;

  /**
   * Create a new bid and update the number of bids and maxBid on the item.
   *
   * @param userId id of the user posting the bid
   * @param itemId id of the item related to the bid
   * @param bid value of the bid
   * @param maxBid maximun bid
   * @param qty number of items
   * @since 1.1
   */
  public void createBid(Integer userId, Integer itemId, float bid, float maxBid, int qty) throws RemoteException
  {
    UserBean user;
    ItemBean item;
    
    try 
    {
      user = (UserBean)em.find(UserBean.class, userId);
      if (user == null)
        throw new Exception("Entity does not exist");
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot find user: " +e+"<br>");
    }
    
    try
    {
      item = (ItemBean)em.find(ItemBean.class, itemId);
      if (item == null)
        throw new Exception("Entity does not exist");
    }
    catch (Exception e)
    {
      throw new RemoteException("Cannot find item: " +e+"<br>");
    }
    
    try
    {
      BidBean b = new BidBean(user, item, bid, maxBid, qty);
      em.persist(b);
    }
    catch (Exception e)
    {
      throw new RemoteException("Error while storing the bid (got exception: " +e+")<br>");
    }
  }
}
