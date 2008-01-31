package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used to get the information about a user.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_ViewUserInfoBean")
@Remote(SB_ViewUserInfo.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_ViewUserInfoBean implements SB_ViewUserInfo
{
  @PersistenceContext
  private EntityManager em;

  /**
   * Get the comment related to a specific user.
   *
   * @param userHome an <code>UserHome</code> value
   * @param userId a user id
   * @return a string in html format
   * @since 1.1
   */
  public String getComments(UserBean user) throws RemoteException
  {
    Collection list;
    Iterator it;
    StringBuffer html;
    CommentBean comment;

    // Try to find the comments corresponding for this user
    try 
    {
      list = user.getToComments();
      if (list.isEmpty())
       html = new StringBuffer("<h3>There is no comment yet for this user.</h3><br>");
      else
      {
        html = new StringBuffer("<br><hr><br><h3>Comments for this user</h3><br>");

        html.append(printCommentHeader());
        // Display each comment and the name of its author
        it = list.iterator();
        while (it.hasNext())
        {
          comment = (CommentBean)it.next();
          String userName = comment.getFromUser().getNickName();
          html.append(printComment(userName, comment));
        }
        html.append(printCommentFooter());
      }
    } 
    catch (Exception e) 
    {
      throw new RemoteException("Exception getting comment list: " + e +"<br>");
    }
    return html.toString();
  }


  /**
   * Get the information about a user.
   *
   * @param userId a user id
   * @return a string in html format
   * @since 1.1
   */
  public String getUserInfo(Integer userId) throws RemoteException
  {
    StringBuffer html = new StringBuffer();
    UserBean user;

    // Try to find the user corresponding to the userId
    try
    {
      user = (UserBean)em.find(UserBean.class, userId);
      if (user == null)
        throw new Exception("Entity does not exist");
      html.append(user.getHTMLGeneralUserInformation());
      html.append(getComments(user));
    }
    catch (Exception e)
    {
      throw new RemoteException("Cannot get user information (got exception: " +e+")<br>");
    }
    return html.toString();
  }

  /** 
   * Comment header printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printCommentHeader()
  {
    return "<DL>\n";
  }

  /** 
   * Comment printed function
   *
   * @param userName the name of the user who is the subject of the comments
   * @param comment the comment to display
   * @return a string in html format
   * @since 1.1
   */
  public String printComment(String userName, CommentBean comment) throws RemoteException
  {
    try
    {
      return comment.printComment(userName);
    }
    catch (EJBException re)
    {
      throw new EJBException("Unable to print Comment (exception: "+re+")<br>\n");
    }
  }

  /** 
   * Comment footer printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printCommentFooter()
  {
    return "</DL>\n";
  }
}
