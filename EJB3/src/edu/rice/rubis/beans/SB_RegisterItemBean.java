package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used to register a new item.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_RegisterItemBean")
@Remote(SB_RegisterItem.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_RegisterItemBean implements SB_RegisterItem
{
  @PersistenceContext
  private EntityManager em;

  /**
   * Create a new item.
   *
   * @param name name of the item
   * @param description description of the item
   * @param initialPrice initial price
   * @param quantity quantity of items
   * @param reservePrice reserve price
   * @param buyNow price to buy the item without auction
   * @param duration duration of the auction
   * @param userdId seller's id
   * @param categoryId id of the category the item belong to
   * @since 1.1
   */
  public void createItem(String name, String description, float initialPrice, int quantity, float reservePrice, float buyNow, int duration, Integer userId, Integer categoryId) throws RemoteException
  {
    UserBean user;
    CategoryBean category;
    ItemBean item;
    String creationDate;

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
      category = (CategoryBean)em.find(CategoryBean.class, categoryId);
    }
    catch (Exception e)
    {
      throw new RemoteException("Cannot find category: " +e+"<br>");
    }

    try
    {
      item = new ItemBean(name, description, initialPrice, quantity, reservePrice, buyNow, duration, user, category);
      em.persist(item);
    }
    catch (Exception e)
    {
      throw new RemoteException("Item registration failed (got exception: " +e+")<br>");
    }
  }
}
