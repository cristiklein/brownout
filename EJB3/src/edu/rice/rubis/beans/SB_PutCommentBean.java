package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used to get the information to build the html form
 * used to put a comment on a user. 
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_PutCommentBean")
@Remote(SB_PutComment.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_PutCommentBean implements SB_PutComment 
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
  public String getCommentForm(Integer itemId, Integer toId, String username, String password) throws RemoteException 
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
    // Try to find the user corresponding to the 'to' ID and the item
    try
    {
      UserBean to = (UserBean)em.find(UserBean.class, toId);
      if (to == null)
        throw new Exception("Entity does not exist");
      ItemBean item = (ItemBean)em.find(ItemBean.class, itemId);
      if (item == null)
        throw new Exception("Entity does not exist");
      String toName = to.getNickName();

      html = "<center><h2>Give feedback about your experience with "+toName+"</h2><br>\n" +
        "<form action=\""+BeanConfig.context+"/servlet/StoreComment\" method=POST>\n"+
        "<input type=hidden name=to value="+toId.intValue()+">\n"+
        "<input type=hidden name=from value="+user.getId()+">\n"+
        "<input type=hidden name=itemId value="+item.getId().intValue()+">\n"+
        "<center><table>\n"+
        "<tr><td><b>From</b><td>"+username+"\n"+
        "<tr><td><b>To</b><td>"+toName+"\n"+
        "<tr><td><b>About item</b><td>"+item.getName()+"\n";
    }
    catch (Exception e)
    {
      throw new RemoteException("Cannot build comment form: " +e);
    }
    return html;
  }
}
