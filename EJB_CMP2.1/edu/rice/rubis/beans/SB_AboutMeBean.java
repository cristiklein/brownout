package edu.rice.rubis.beans;

import java.rmi.RemoteException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;
import java.io.Serializable;
import javax.transaction.UserTransaction;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.net.URLEncoder;
import java.util.Date;

/**
 * This is a stateless session bean used to give to a user the information about himself.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */

public class SB_AboutMeBean implements SessionBean 
{
  protected SessionContext sessionContext;
  protected Context initialContext = null;


  /**
   * Authenticate the user and get the information about the user.
   *
   * @return a string in html format
   * @since 1.1
   */
  public String getAboutMe(String username, String password) throws RemoteException
  {
    UserLocal user;
    StringBuffer html = new StringBuffer();

    // Authenticate the user
    SB_AuthLocalHome authHome = null;
    SB_AuthLocal auth = null;
    try 
    {
      authHome = (SB_AuthLocalHome)initialContext.lookup("java:comp/env/ejb/SB_Auth");
      auth = authHome.create();
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot lookup SB_Auth: " +e);
    }
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
  public String listItem(UserLocal user) throws RemoteException
  {
    ItemLocal         item;
    StringBuffer sell = new StringBuffer();
    Collection   currentItemList, pastItemList;
    Iterator it;
    
    try 
    {
      currentItemList = user.getCurrentSellings();
      pastItemList = user.getPastSellings(30);
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
          item = (ItemLocal)it.next();
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
        item = (ItemLocal)it.next();
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
  public String listBoughtItems(UserLocal user) throws RemoteException 
  {
    BuyNowLocal       buy;
    Collection       buyList=null;
    Iterator it;
    StringBuffer html = new StringBuffer();

    // Get the list of items the user bought
    try 
    {
      buyList = user.getBuyNow(30);
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
       buy = (BuyNowLocal)it.next();
       // display information about the item
       html.append(buy.getItem().printUserBoughtItem(buy.getQuantity()));       
    }
    html.append(printItemFooter());
    return html.toString();   
  }

  /** List items the user won in the last 30 days*/
  public String listWonItems(UserLocal user) throws RemoteException 
  {
    Collection  wonList=null;
    Iterator it;
    ItemLocal         item;
    StringBuffer html;

    // Get the list of the user's won items in the last 30 days
    try 
    {
      wonList = user.getWonItems(30);
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
      item = (ItemLocal)it.next();
      // display information about the item
      html.append(item.printUserWonItem());
    }
    html.append(printItemFooter());
    return html.toString();
  }


  /** List comments about the user */
  public String listComments(UserLocal user) throws RemoteException
  {
    Collection   list;
    Iterator it;
    CommentLocal      comment;
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
        comment = (CommentLocal)it.next();
        String userName;
        UserLocal u = comment.getFromUser();
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
  public String listBids(UserLocal user, String username, String password) throws RemoteException 
  {
    Collection  bidList=null;
    Iterator it;
    ItemLocal         item;
    StringBuffer html;

    // Get the list of the active items the user had bid on
    try 
    {
      bidList = user.getBidItems();
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
      item = (ItemLocal)it.next();
      try
      {
        maxBid = user.getMaxBid(item);
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
  public String printItemUserHasBidOn(float maxBid, ItemLocal item, String username, String password) throws RemoteException
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
  public String printComment(String userName, CommentLocal comment) throws RemoteException
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


// ======================== EJB related methods ============================

/**
 * This method is empty for a stateless session bean
 */
public void ejbCreate() throws CreateException, RemoteException
{
}

/** This method is empty for a stateless session bean */
public void ejbActivate() throws RemoteException {}
/** This method is empty for a stateless session bean */
public void ejbPassivate() throws RemoteException {}
/** This method is empty for a stateless session bean */
public void ejbRemove() throws RemoteException {}


/** 
 * Sets the associated session context. The container calls this method 
 * after the instance creation. This method is called with no transaction context. 
 * We also retrieve the Home interfaces of all RUBiS's beans.
 *
 * @param sessionContext - A SessionContext interface for the instance. 
 * @exception RemoteException - Thrown if the instance could not perform the function 
 *            requested by the container because of a system-level error. 
 */
public void setSessionContext(SessionContext sessionContext) throws RemoteException
{
  this.sessionContext = sessionContext;
  
  try
  {
    initialContext = new InitialContext(); 
  }
  catch (Exception e) 
  {
    throw new RemoteException("Cannot get JNDI InitialContext");
  }
}

}
