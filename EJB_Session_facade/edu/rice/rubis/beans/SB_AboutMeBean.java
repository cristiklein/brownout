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
  protected DataSource dataSource = null;
  //private UserTransaction utx = null;


  /**
   * Authenticate the user and get the information about the user.
   *
   * @return a string in html format
   * @since 1.1
   */
  public String getAboutMe(String username, String password) 
  {
    int uid = -1;
    Integer userId;
    String html = "";

    // Authenticate the user
    SB_AuthHome authHome = null;
    SB_Auth auth = null;
    try 
    {
      authHome = (SB_AuthHome)PortableRemoteObject.narrow(initialContext.lookup("java:comp/env/ejb/SB_Auth"), SB_AuthHome.class);
      auth = authHome.create();
    } 
    catch (Exception e)
    {
      throw new EJBException("Cannot lookup SB_Auth: " +e);
    }
    try 
    {
      uid = auth.authenticate(username, password);
    } 
    catch (Exception e)
    {
      throw new EJBException("Authentication failed: " +e);
    }
    if (uid == -1)
    {
      html = (" You don't have an account on RUBiS!<br>You have to register first.<br>");
      return html;
    }
    // Try to find the user corresponding to the userId
      UserHome uHome;
      try 
      {
        userId = new Integer (uid);
        uHome = (UserHome)PortableRemoteObject.narrow(initialContext.lookup("java:comp/env/ejb/User"), UserHome.class);
      } 
      catch (Exception e)
      {
        throw new EJBException("Cannot lookup User: " +e+"<br>");
      }
      try
      {
        User u = uHome.findByPrimaryKey(new UserPK(userId));

        html = u.getHTMLGeneralUserInformation();
      }
      catch (Exception e)
      {
        throw new EJBException("This user does not exist (got exception: " +e+")<br>");
      }

      // Try to find the comments corresponding for this user
      CommentHome cHome;
      try 
      {
        cHome = (CommentHome)PortableRemoteObject.narrow(initialContext.lookup("java:comp/env/ejb/Comment"), CommentHome.class);
      } 
      catch (Exception e)
      {
        throw new EJBException("Cannot lookup Comment: " +e+"<br>");
      }
      // Retrieve ItemHome
      ItemHome iHome;
      try 
      {
        iHome = (ItemHome)PortableRemoteObject.narrow(initialContext.lookup("java:comp/env/ejb/Item"), ItemHome.class);
      } 
      catch (Exception e)
      {
        throw new EJBException("Cannot lookup item: " +e+"<br>");
      }
      // Connecting to Query Home thru JNDI
      QueryHome qHome;
      try 
      {
        qHome = (QueryHome)PortableRemoteObject.narrow(initialContext.lookup("java:comp/env/ejb/Query"), QueryHome.class);
      } 
      catch (Exception e)
      {
        throw new EJBException("Cannot lookup Query: " +e+"<br>");
      }
      try 
      {
        html = html.concat(listItem(userId, iHome));
        html = html.concat(listBoughtItems(userId, iHome));
        html = html.concat(listWonItems(userId, iHome, qHome));
        html = html.concat(listBids(userId, username, password, iHome, qHome));
        html = html.concat(listComments(cHome, userId, uHome));
        
      } 
      catch (Exception e)
      {
        throw new EJBException("Cannot lookup Query: " +e+"<br>");
      }
     return html;
  }
                   
  /** List items the user is currently selling and sold in the past 30 days */
  public String listItem(Integer userId, ItemHome iHome) 
  {
    Item       item;
    String     sell, sold;
    Collection currentItemList, pastItemList;

    try 
    {
      currentItemList = iHome.findUserCurrentSellings(userId);
      pastItemList = iHome.findUserPastSellings(userId);
    }
    catch (Exception e)
    {
      throw new EJBException("Exception getting item list: " +e+"<br>");
    }

    if ((currentItemList == null) || (currentItemList.isEmpty()))
    {
       sell = "<br>";
       sell = sell.concat(printHTMLHighlighted("<h3>You are currently selling no item.</h3>"));
    }
    else
    {
      // display current sellings
      try
      {
        sell = printSellHeader("Items you are currently selling.");

        Iterator it = currentItemList.iterator();
        while (it.hasNext()) 
        {
          // Get the name of the items
            item = (Item)it.next();
          // display information about the item
          sell = sell.concat(item.printSell());
        }
        sell = sell.concat(printItemFooter());
      }
      catch (Exception e) 
      {
        throw new EJBException("Exception getting item: " + e +"<br>");
      }
    }

    if ((pastItemList == null) || (pastItemList.isEmpty()))
    {
      sold = "<br>";
      sold = sold.concat("<h3>You didn't sell any item.</h3>");
      return sell.concat(sold);
    }
    // display past sellings
    sold = "<br>";
    sold = sold.concat(printSellHeader("Items you sold in the last 30 days."));
    try
    {
      Iterator it = pastItemList.iterator();
      while (it.hasNext()) 
      {
        // Get the name of the items
        item = (Item)it.next();
        // display information about the item
        sold = sold.concat(item.printSell());
      }
      sold = sold.concat(printItemFooter());
    }
    catch (Exception e) 
    {
      throw new EJBException("Exception getting item: " + e +"<br>");
    }

    return sell.concat(sold);
  }

  /** List items the user bought in the last 30 days*/
  public String listBoughtItems(Integer userId, ItemHome iHome) 
  {
    BuyNowHome buyHome;
    BuyNow     buy;
    Item       item;
    Collection buyList=null;
    int        quantity;
    String     html;

    // Get the list of items the user bought
    try 
    {
      buyHome = (BuyNowHome)PortableRemoteObject.narrow(initialContext.lookup("java:comp/env/ejb/BuyNow"),
                                                        BuyNowHome.class);
    } 
    catch (Exception e)
    {
      throw new EJBException("Cannot lookup BuyNow: " +e+"<br>");
    }
    try 
    {
      buyList = buyHome.findUserBuyNow(userId);
    }
    catch (Exception e)
    {
      throw new EJBException("Exception getting item list (buy now): " +e+"<br>");
    }
    if ((buyList == null) || (buyList.isEmpty()))
    {
      html = "<br>";
      html = html.concat("<h3>You didn't buy any item in the last 30 days.</h3>");
      html = html.concat("<br>");
      return html;
    }
    html = printUserBoughtItemHeader();
    
    Iterator it = buyList.iterator();
    while (it.hasNext()) 
    {
      // Get the name of the items
      try
      {
        buy = (BuyNow)it.next();
	quantity = buy.getQuantity();
      }
      catch (Exception e) 
      {
        throw new EJBException("Exception getting buyNow quantity: " + e +"<br>");
      }
      try
      {
	item = iHome.findByPrimaryKey(new ItemPK(buy.getItemId()));
        // display information about the item
        html = html.concat(item.printUserBoughtItem(quantity));
      }
      catch (Exception e) 
      {
        throw new EJBException("Exception getting item: " + e +"<br>");
      }
    }
    html = html.concat(printItemFooter());
    return html;

  }

  /** List items the user won in the last 30 days*/
  public String listWonItems(Integer userId, ItemHome iHome, QueryHome qHome) 
  {
    Enumeration wonList=null;
    Query       q;
    Item        item;
    float       price;
    String      name;
    String      html;

    // Get the list of the user's won items
    try 
    {
      q = qHome.create();
      wonList = q.getUserWonItems(userId).elements();
    }
    catch (Exception e)
    {
      throw new EJBException("Exception getting won items list: " +e+"<br>");
    }
    if ((wonList == null) || (!wonList.hasMoreElements()))
    {
      html = "<br>";
      html = html.concat("<h3>You didn't win any item in the last 30 days.</h3>");
      html = html.concat("<br>");
      return html;
    }
    html = printUserWonItemHeader();

    while (wonList.hasMoreElements()) 
    {
      // Get the name of the items
      try
      {
        item = iHome.findByPrimaryKey((ItemPK)wonList.nextElement());
        // display information about the item
        html = html.concat(item.printUserWonItem());
      }
      catch (Exception e) 
      {
        throw new EJBException("Exception getting item: " + e +"<br>");
      }
    }
    html = html.concat(printItemFooter());
    return html;
  }


  /** List comments about the user */
  public String listComments(CommentHome home, Integer userId, UserHome uHome) 
  {
    Collection list;
    Comment    comment;
    String     html;
    try 
    {
      list = home.findByToUser(userId);
      html = "<br>";
      if (list.isEmpty()) 
        html = html.concat(printHTMLHighlighted("<h3>There is no comment yet for this user.</h3>"));
      else
        html = html.concat(printHTMLHighlighted("<h3>Comments for this user</h3>"));
      html = html.concat("<br>");
      html = html.concat(printCommentHeader());
      // Display each comment and the name of its author
      Iterator it = list.iterator();
      while (it.hasNext()) 
      {
        comment = (Comment)it.next();
        String userName;
        try
        {
          User u = uHome.findByPrimaryKey(new UserPK(comment.getFromUserId()));
          userName = u.getNickName();
          html = html.concat(printComment(userName, comment));
        }
        catch (Exception e)
        {
          throw new EJBException("This author does not exist (got exception: " +e+")<br>");
        }
      }
      html = html.concat(printCommentFooter());
    } 
    catch (Exception e) 
    {
      throw new EJBException("Exception getting comment list: " + e +"<br>");
    }
    return html;
  }

  /** List items the user put a bid on in the last 30 days*/
  public String listBids(Integer userId, String username, String password, ItemHome iHome, QueryHome qHome) 
  {
    Enumeration bidList=null;
    Query       q;
    BidHome     bidHome;
    Bid         bid;
    Item        item;
    float       price;
    String      name;
    String      html;

    // Get the list of the user's last bids
    try 
    {
      q = qHome.create();
      bidList = q.getUserBids(userId).elements();
    }
    catch (Exception e)
    {
      throw new EJBException("Exception getting bids list: " +e+"<br>");
    }
    if ((bidList == null) || (!bidList.hasMoreElements()))
    {
      html = printHTMLHighlighted("<h3>You didn't put any bid.</h3>");
      return html;
    }

    // Lookup bid home interface
    try 
    {
      bidHome = (BidHome)PortableRemoteObject.narrow(initialContext.lookup("java:comp/env/ejb/Bid"),
                                                     BidHome.class);
    } 
    catch (Exception e)
    {
      throw new EJBException("Cannot lookup Bid: " +e+"<br>");
    }

    html = printUserBidsHeader();

    while (bidList.hasMoreElements()) 
    {
      // Get the amount of the last bids
      try
      {
        bid = bidHome.findByPrimaryKey((BidPK)bidList.nextElement());
      }
      catch (Exception e) 
      {
        throw new EJBException("Exception getting bid: " + e +"<br>");
      }

      // Get the name of the items
      try
      {
        item = iHome.findByPrimaryKey(new ItemPK(bid.getItemId()));
        html = html.concat(printItemUserHasBidOn(bid, item, username, password));
      }
      catch (Exception e) 
      {
        throw new EJBException("Exception getting item: " + e +"<br>");
      }
      //  display information about user's bids
    }
    html = html.concat(printItemFooter());
    return html;
  }

  /** 
   * user's bought items list header printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printUserBoughtItemHeader()
  {
    String      html = "<br>";
    html = html.concat(printHTMLHighlighted("<p><h3>Items you bouhgt in the past 30 days.</h3>\n"));
    html = html.concat("<TABLE border=\"1\" summary=\"List of items\">\n"+
                "<THEAD>\n"+
                "<TR><TH>Designation<TH>Quantity<TH>Price you bought it<TH>Seller"+
                "<TBODY>\n");
    return html;
  }

  /** 
   * user's won items list header printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printUserWonItemHeader()
  {
    String      html = "<br>";
    html = html.concat(printHTMLHighlighted("<p><h3>Items you won in the past 30 days.</h3>\n"));
    html = html.concat("<TABLE border=\"1\" summary=\"List of items\">\n"+
                "<THEAD>\n"+
                "<TR><TH>Designation<TH>Price you bought it<TH>Seller"+
                "<TBODY>\n");
    return html;
  }

  /** 
   * user's bids list header printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printUserBidsHeader()
  {
    String      html = "<br>";
    html = html.concat(printHTMLHighlighted("<p><h3>Items you have bid on.</h3>\n"));
    html = html.concat("<TABLE border=\"1\" summary=\"Items You've bid on\">\n"+
                "<THEAD>\n"+
                "<TR><TH>Designation<TH>Initial Price<TH>Current price<TH>Your max bid<TH>Quantity"+
                "<TH>Start Date<TH>End Date<TH>Seller<TH>Put a new bid\n"+
                "<TBODY>\n");
    return html;
  }

  /** 
   * items list printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printItemUserHasBidOn(Bid bid, Item item, String username, String password)
  {
    try
    {
      return (item.printItemUserHasBidOn(bid.getMaxBid())+"&nickname="+URLEncoder.encode(username)+"&password="+URLEncoder.encode(password)+"\"><IMG SRC=\"/EJB_HTML/bid_now.jpg\" height=22 width=90></a>\n");
    }
    catch (RemoteException re)
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
    String      html = "";
    html = html.concat(printHTMLHighlighted("<p><h3>"+title+"</h3>\n"));
    html = html.concat("<TABLE border=\"1\" summary=\"List of items\">\n"+
                "<THEAD>\n"+
                "<TR><TH>Designation<TH>Initial Price<TH>Current price<TH>Quantity<TH>ReservePrice<TH>Buy Now"+
                "<TH>Start Date<TH>End Date\n"+
                "<TBODY>\n");
    return html;
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
  public String printComment(String userName, Comment comment)
  {
    try
    {
      return comment.printComment(userName);
    }
    catch (RemoteException re)
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
    String html = "";
    html = html.concat("<TABLE width=\"100%\" bgcolor=\"#CCCCFF\">\n<TR><TD align=\"center\" width=\"100%\"><FONT size=\"4\" color=\"#000000\"><B>").concat(msg).concat("</B></FONT></TD></TR>\n</TABLE><p>\n");
    return html;
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
    if (dataSource == null)
    {
      // Finds DataSource from JNDI
 
      try
      {
        initialContext = new InitialContext(); 
        dataSource = (DataSource)initialContext.lookup("java:comp/env/jdbc/rubis");
      }
      catch (Exception e) 
      {
        throw new EJBException("Cannot get JNDI InitialContext");
      }
    }
  }

}
