package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used to create e new comment for a user.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_StoreCommentBean")
@Remote(SB_StoreComment.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_StoreCommentBean implements SB_StoreComment
{
  @PersistenceContext
  private EntityManager em;

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
    UserBean fromUser, toUser;
    ItemBean item;
    
    // Try to find the fromUser
    try 
    {
      fromUser = (UserBean)em.find(UserBean.class, fromId);
      if (fromUser == null)
        throw new Exception("Entity does not exist");
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot find User ("+fromId+"): " +e+"<br>");
    }
    // Try to find the toUser
    try 
    {
      toUser = (UserBean)em.find(UserBean.class, toId);
      if (toUser == null)
        throw new Exception("Entity does not exist");
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot find User ("+toId+"): " +e+"<br>");
    }
    
    // Try to find the item
    try 
    {
      item = (ItemBean)em.find(ItemBean.class, itemId);
      if (item == null)
        throw new Exception("Entity does not exist");
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot find Item ("+itemId+"): " +e+"<br>");
    }
    
    // Insert the comment
    try
    {
      CommentBean c = new CommentBean(fromUser, toUser, item, rating, comment);
      em.persist(c);
      toUser.updateRating(rating);
    }
    catch (Exception e)
    {
      throw new RemoteException("Error while storing the comment (got exception: " +e+")<br>");
    }
  }
}
