package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used to give to a user the information about himself.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_AboutMeBean")
@Remote(SB_AboutMe.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_AboutMeBean implements SB_AboutMe
{
  @PersistenceContext
  private EntityManager em;
  @EJB
  private SB_AuthLocal auth;

  /**
   * Authenticate the user and get the information about the user.
   *
   * @return a string in html format
   * @since 1.1
   */
  public String getAboutMe(String username, String password) throws RemoteException
  {
    UserBean user;
    StringBuffer html = new StringBuffer();

    // Authenticate the user
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
      return "You don't have an account on RUBiS!<br>You have to register first.<br>";
    }

    try 
    {
      html.append(listItem(user));
      html.append(listBoughtItems(user));
      html.append(listWonItems(user));
      html.append(listBids(user, username, password));
      html.append(listComments(user));
        
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot get information about the user: " +e+"<br>");
    }
    return html.toString();
  }

  /** 
   * List items the user is currently selling and 
   * the items he sold in the past 30 days. 
   */
  public String listItem(UserBean user) throws RemoteException
  {
    ItemBean item;
    StringBuffer sell = new StringBuffer();
    List currentItemList, pastItemList;
    Iterator it;
    
    try 
    {
      GregorianCalendar now = new GregorianCalendar();
      GregorianCalendar oldest = TimeManagement.addDays(now, 30);
      
      Query q = em.createNamedQuery("userCurrentSellings");
      q.setParameter(1, user);
      q.setParameter(2, now);
      currentItemList = q.getResultList();
      
      q = em.createNamedQuery("userPastSellings");
      q.setParameter(1, user);
      q.setParameter(2, now);
      q.setParameter(3, oldest);
      pastItemList = q.getResultList();
    }
    catch (Exception e)
    {
      throw new RemoteException("Exception getting current and past sellings: " +e+"<br>");
    }

    if (currentItemList.isEmpty())
    {
      sell.append("<br>");
      sell.append(printHTMLHighlighted("<h3>You are currently selling no item.</h3>"));
    }
    else
    {
      // display current sellings
      try
      {
        sell.append(printSellHeader("Items you are currently selling."));
        
        it = currentItemList.iterator();
        while (it.hasNext()) 
        {
          item = (ItemBean)it.next();
          // display information about the item
          sell.append(item.printSell());
        }
        sell.append(printItemFooter());
      }
      catch (Exception e) 
      {
        throw new RemoteException("Exception getting item for current sells: " + e +"<br>");
      }
    }
    
    if (pastItemList.isEmpty())
    {
      sell.append("<br><h3>You didn't sell any item.</h3>");
      return sell.toString();
    }
    // display past sellings
    sell.append("<br>");
    sell.append(printSellHeader("Items you sold in the last 30 days."));
    try
    {
      it = pastItemList.iterator();
      while (it.hasNext()) 
      {
        item = (ItemBean)it.next();
        // display information about the item
        sell.append(item.printSell());
      }
      sell.append(printItemFooter());
    }
    catch (Exception e) 
    {
      throw new RemoteException("Exception getting item for past sells: " + e +"<br>");
    }
    
    return sell.toString();
  }

  /** List items the user bought in the last 30 days*/
  public String listBoughtItems(UserBean user) throws RemoteException 
  {
    BuyNowBean buy;
    Collection buyList;
    Iterator it;
    StringBuffer html = new StringBuffer();

    // Get the list of items the user bought
    try 
    {
      GregorianCalendar now = new GregorianCalendar();
      GregorianCalendar oldest = TimeManagement.addDays(now, 30);
      
      Query q = em.createNamedQuery("userBuyNow");
      q.setParameter(1, user);
      q.setParameter(2, oldest);
      buyList = q.getResultList();
    }
    catch (Exception e)
    {
      throw new RemoteException("Exception getting buy now items list: " +e+"<br>");
    }
    if (buyList.isEmpty())
    {
      return "<br><h3>You did not buy any item in the last 30 days.</h3><br>";
    }
    html.append(printUserBoughtItemHeader());
    it = buyList.iterator();
    while (it.hasNext()) 
    {
       buy = (BuyNowBean)it.next();
       // display information about the item
       html.append(buy.getItem().printUserBoughtItem(buy.getQuantity()));       
    }
    html.append(printItemFooter());
    return html.toString();   
  }

  /** List items the user won in the last 30 days*/
  public String listWonItems(UserBean user) throws RemoteException 
  {
    Collection  wonList;
    Iterator it;
    ItemBean item;
    StringBuffer html;

    // Get the list of the user's won items in the last 30 days
    try 
    {
      GregorianCalendar now = new GregorianCalendar();
      GregorianCalendar oldest = TimeManagement.addDays(now, 30);
      
      Query q = em.createNamedQuery("userWonItems");
      q.setParameter(1, user);
      q.setParameter(2, now);
      q.setParameter(3, oldest);
      wonList = q.getResultList();
    }
    catch (Exception e)
    {
      throw new RemoteException("Exception getting won items list: " +e+"<br>");
    }

    if (wonList.isEmpty())
    {
      return "<br><h3>You didn't win any item in the last 30 days.</h3><br>";
    }
    html = new StringBuffer(printUserWonItemHeader());

    it = wonList.iterator();
    while (it.hasNext()) 
    {
      item = (ItemBean)it.next();
      // display information about the item
      html.append(item.printUserWonItem());
    }
    html.append(printItemFooter());
    return html.toString();
  }

  /** List comments about the user */
  public String listComments(UserBean user) throws RemoteException
  {
    Collection list;
    Iterator it;
    CommentBean comment;
    StringBuffer html;
    
    try 
    {
      list = user.getToComments();
      html = new StringBuffer("<br>");
      if (list.isEmpty()) 
        html.append(printHTMLHighlighted("<h3>There is no comment yet for this user.</h3>"));
      else
        html.append(printHTMLHighlighted("<h3>Comments for this user</h3>"));
      html.append("<br>");
      html.append(printCommentHeader());
      // Display each comment and the name of its author
      it = list.iterator();
      while (it.hasNext()) 
      {
        comment = (CommentBean)it.next();
        String userName;
        UserBean u = comment.getFromUser();
        userName = u.getNickName();
        html.append(printComment(userName, comment));
      }
      html.append(printCommentFooter());
    } 
    catch (Exception e) 
    {
      throw new RemoteException("Exception getting comment list: " + e +"<br>");
    }
    return html.toString();
  }

  /** List items the user put a bid on in the last 30 days*/
  public String listBids(UserBean user, String username, String password) throws RemoteException 
  {
    Collection bidList;
    Iterator it;
    ItemBean item;
    StringBuffer html;

    // Get the list of the active items the user had bid on
    try 
    {
      GregorianCalendar now = new GregorianCalendar();
      
      Query q = em.createNamedQuery("userBidItems");
      q.setParameter(1, user);
      q.setParameter(2, now);
      bidList = q.getResultList();
    }
    catch (Exception e)
    {
      throw new RemoteException("Exception getting bid items list: " +e+"<br>");
    }
    if (bidList.isEmpty())
    {
      return printHTMLHighlighted("<h3>You didn't put any bid.</h3>");
    }

    html = new StringBuffer(printUserBidsHeader());

    it = bidList.iterator();
    while (it.hasNext()) 
    {
      float maxBid;
      item = (ItemBean)it.next();
      try
      {
        Query q = em.createNamedQuery("userMaxBid");
        q.setParameter(1, user);
        q.setParameter(2, item);
        maxBid = (Float)q.getSingleResult();
      }
      catch (Exception e)
      {
        throw new RemoteException("Exception getting max bid: " +e+"<br>");
      }
      //  display information about user's bids
      html.append(printItemUserHasBidOn(maxBid, item, username, password));
    }
    html.append(printItemFooter());
    return html.toString();
  }

  /** 
   * user's bought items list header printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printUserBoughtItemHeader()
  {
    return "<br>"+
      printHTMLHighlighted("<p><h3>Items you bouhgt in the past 30 days.</h3>\n")+
      "<TABLE border=\"1\" summary=\"List of items\">\n"+
      "<THEAD>\n"+
      "<TR><TH>Designation<TH>Quantity<TH>Price you bought it<TH>Seller"+
      "<TBODY>\n";
  }

  /** 
   * user's won items list header printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printUserWonItemHeader()
  {
    return "<br>"+
      printHTMLHighlighted("<p><h3>Items you won in the past 30 days.</h3>\n")+
      "<TABLE border=\"1\" summary=\"List of items\">\n"+
      "<THEAD>\n"+
      "<TR><TH>Designation<TH>Price you bought it<TH>Seller"+
      "<TBODY>\n";
  }

  /** 
   * user's bids list header printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printUserBidsHeader()
  {
    return "<br>"+
      printHTMLHighlighted("<p><h3>Items you have bid on.</h3>\n")+
      "<TABLE border=\"1\" summary=\"Items You've bid on\">\n"+
      "<THEAD>\n"+
      "<TR><TH>Designation<TH>Initial Price<TH>Current price<TH>Your max bid<TH>Quantity"+
      "<TH>Start Date<TH>End Date<TH>Seller<TH>Put a new bid\n"+
      "<TBODY>\n";
  }


  /** 
   * items list printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printItemUserHasBidOn(float maxBid, ItemBean item, String username, String password) throws RemoteException
  {
    try
    {
      return item.printItemUserHasBidOn(maxBid)+"&nickname="+URLEncoder.encode(username)+"&password="+URLEncoder.encode(password)+"\"><IMG SRC=\""+BeanConfig.context+"/bid_now.jpg\" height=22 width=90></a>\n";
    }
    catch (EJBException re)
    {
      throw new EJBException("Unable to print Item (exception: "+re+")<br>\n");
    }
  }


  /** 
   * user's sellings header printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printSellHeader(String title)
  {
    return printHTMLHighlighted("<p><h3>"+title+"</h3>\n")+
      "<TABLE border=\"1\" summary=\"List of items\">\n"+
      "<THEAD>\n"+
      "<TR><TH>Designation<TH>Initial Price<TH>Current price<TH>Quantity<TH>ReservePrice<TH>Buy Now"+
      "<TH>Start Date<TH>End Date\n"+
      "<TBODY>\n";
  }


  /** 
   * Item footer printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printItemFooter()
  {
    return "</TABLE>\n";
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

  /**
   * Construct a html highlighted string.
   * @param msg the message to display
   * @return a string in html format
   * @since 1.1
   */
  public String printHTMLHighlighted(String msg)
  {
    return "<TABLE width=\"100%\" bgcolor=\"#CCCCFF\">\n<TR><TD align=\"center\" width=\"100%\"><FONT size=\"4\" color=\"#000000\"><B>" + msg + "</B></FONT></TD></TR>\n</TABLE><p>\n";
  }
}
