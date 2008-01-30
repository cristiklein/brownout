package edu.rice.rubis.servlets;

import java.io.IOException;

import java.util.List;
import java.util.Iterator;

import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.rice.rubis.hibernate.User;
import edu.rice.rubis.hibernate.Item;
import edu.rice.rubis.hibernate.Bid;
import edu.rice.rubis.hibernate.Buy;
import edu.rice.rubis.hibernate.Comment;

/**
 * This servlets displays general information about the user loged in
 * and about his current bids or items to sell.
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public class AboutMe extends HibernateRubisHttpServlet
{

  public int getPoolSize()
  {
    return Config.AboutMePoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    //sp.printHTMLheader("RUBiS ERROR: About me");
    sp.printHTML(
      "<h3>Your request has not been processed due to the following error :</h3><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    
  }

  /** List items the user is currently selling and sold in the past 30 days */
  private void listItems(User user, Session sess, ServletPrinter sp)
  {
    List lst = null;
    
    // current sellings
    try
    {
      Query q = sess.createFilter(user.getItems(), "select this where this.endDate >= NOW()");
      lst = q.list();
    }
    catch (HibernateException he)
    {
      printError("Exception getting current sellings list: " + he + "<br>", sp);
      return;
    }
    
    try
    {
      Iterator it = lst.iterator();
      
      if (!it.hasNext())
      {
        sp.printHTML("<br>");
        sp.printHTMLHighlighted("<h3>You are currently selling no item.</h3>");
      }
      else
      {
        // display current sellings
        sp.printHTML("<br>");
        sp.printSellHeader("Items you are currently selling.");
        do
        {
          // Get the item
          Item item = (Item) it.next();
          
          // display information about the item
          sp.printSell(item);
        }
        while (it.hasNext());
        sp.printItemFooter();
      }
    }
    catch (Exception e)
    {
      printError("Exception getting current items in sell: " + e + "<br>", sp);
      return;
    }
    
    // Past sellings
    try
    {
      Query q = sess.createFilter(user.getItems(), "select this where  TO_DAYS(this.endDate) > TO_DAYS(NOW()) - 30");
      lst = q.list();
    }
    catch (HibernateException he)
    {
      printError("Exception getting past sellings list: " + he + "<br>", sp);
      return;
    }
    
    try
    {
      Iterator it = lst.iterator();
      
      if (!it.hasNext())
      {
        sp.printHTML("<br>");
        sp.printHTMLHighlighted("<h3>You didn't sell any item.</h3>");
        return;
      }
      // display past sellings
      sp.printHTML("<br>");
      sp.printSellHeader("Items you sold in the last 30 days.");
      do
      {
        // Get the name of the items
        Item item = (Item) it.next();
        
        // display information about the item
        sp.printSell(item);
      }
      while (it.hasNext());
    }
    catch (Exception e)
    {
      printError("Exception getting sold items: " + e + "<br>", sp);
      return;
    }
    sp.printItemFooter();
  }

  /** List items the user bought in the last 30 days*/
  private void listBoughtItems(User user, Session sess, ServletPrinter sp)
  {
    List lst = null;
    
    // Get the list of items the user bought
    try
    {
      Query q = sess.createFilter(user.getBuys(), "select this.item, this.qty where TO_DAYS(this.date) >= TO_DAYS(NOW()) - 30");
      lst = q.list();
    }
    catch (HibernateException he)
    {
      printError("Exception getting bought items list: " + he + "<br>", sp);
      return;
    }
    
    try
    {
      Iterator it = lst.iterator();
      
      if (!it.hasNext())
      {
        sp.printHTML("<br>");
        sp.printHTMLHighlighted("<h3>You didn't buy any item in the last 30 days.</h3>");
        sp.printHTML("<br>");
        return;
      }
      
      sp.printUserBoughtItemHeader();
      
      do
      {
        Object[] t = (Object[]) it.next();
        Item item = (Item) t[0];
        Integer qty = (Integer) t[1];
        
        // display information about the item
        sp.printUserBoughtItem(item, qty);
      }
      while (it.hasNext());
    }
    catch (Exception e)
    {
      printError("Exception getting bought items: " + e + "<br>", sp);
      return;
    }
    sp.printItemFooter();
  }

  /** List items the user won in the last 30 days*/
  private void listWonItems(User user, Session sess, ServletPrinter sp)
  {
    List lst = null;
    
    // Get the list of the user's won items
    try
    {
      Query q = sess.createFilter(user.getBids(), "select this.item where TO_DAYS(this.item.endDate) > TO_DAYS(NOW()) - 30 group by this.item");
      lst = q.list();
    }
    catch (HibernateException he)
    {
      sp.printHTML("Exception getting won items list: " + he + "<br>");
      return;
    }
    
    try
    {
      Iterator it = lst.iterator();
      
      if (!it.hasNext())
      {
        sp.printHTML("<br>");
        sp.printHTMLHighlighted("<h3>You didn't win any item in the last 30 days.</h3>");
        sp.printHTML("<br>");
         return;
      }
      
      sp.printUserWonItemHeader();
      
      do
      {
        Item item = (Item) it.next();
        
        // display information about the item
        sp.printUserWonItem(item);
      }
      while (it.hasNext());
    }
    catch (Exception e)
    {
      sp.printHTML("Exception getting won items: " + e + "<br>");
      return;
    }
    sp.printItemFooter();
  }

  /** List comments about the user */
  private void listComment(User user, Session sess, ServletPrinter sp)
  {
    List lst = null;
    Transaction trans = null;
    
    try
    {
      trans = sess.beginTransaction();
      
      Iterator it = user.getCommentsTo().iterator();
      
      if (!it.hasNext())
      {
        sp.printHTML("<br>");
        sp.printHTMLHighlighted("<h3>There is no comment yet for this user.</h3>");
        sp.printHTML("<br>");
        trans.commit();
        return;
      }
      sp.printHTML("<br><hr><br><h3>Comments for this user</h3><br>");
      
      sp.printCommentHeader();
      // Display each comment and the name of its author
      do
      {
        Comment comment = (Comment) it.next();
        
        sp.printComment(comment);
      }
      while (it.hasNext());
      
      sp.printCommentFooter();
      
      trans.commit();
    }
    catch (Exception e)
    {
      sp.printHTML("Exception getting comment list: " + e + "<br>");
      if (trans != null)
      {
        try
        {
          trans.rollback();
        }
        catch (HibernateException he)
        {
          sp.printHTML("Transaction rollback failed: " + he + "<br>");
        }
      }
    }
  }

  /** List items the user put a bid on in the last 30 days*/
  private void listBids(User user, Session sess, ServletPrinter sp)
  {
    List lst = null;
    
    // Get the list of the user's last bids
    try
    {
      Query q = sess.createFilter(user.getBids(), "select this.item, this.maxBid where this.item.endDate >= NOW() group by this.item");
      lst = q.list();
    }
    catch (HibernateException he)
    {
      sp.printHTML("Exception getting bids list: " + he + "<br>");
      return;
    }
    
    try
    {
      Iterator it = lst.iterator();
      
      if (!it.hasNext())
      {
        sp.printHTMLHighlighted("<h3>You didn't put any bid.</h3>");
        sp.printHTML("<br>");
        return;
      }
      
      sp.printUserBidsHeader();
      
      do
      {
        Object[] t = (Object[]) it.next();
        Item item = (Item) t[0];
        Float maxBid = (Float) t[1];
        
        //  display information about user's bids
        sp.printItemUserHasBidOn(item, maxBid, user);
      }
      while (it.hasNext());
    }
    catch (Exception e)
    {
      printError("Exception getting items the user has bid on: " + e + "<br>", sp);
      return;
    }
    sp.printItemFooter();
  }

  /**
   * Call <code>doPost</code> method.
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doPost(request, response);
  }

  /** 
   * Check username and password and build the web page that display the information about
   * the loged in user.
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    Session sess = null;
    String nick = null, pass = null;
    User user = null;
    ServletPrinter sp = null;
    
    sp = new ServletPrinter(response, "About me");
    
    nick = request.getParameter("nickname");
    pass = request.getParameter("password");
    
    sess = getSession();
    
    // Authenticate the user
    if ((nick != null && !nick.equals(""))
      || (pass != null && !pass.equals("")))
    {
      Auth auth = new Auth(sess, sp);
      user = auth.authenticate(nick, pass);
      if (user == null)
      {
        printError("You don't have an account on RUBiS!<br>You have to register first.<br>", sp);
        releaseSession(sess);
        return;
      }
    }
    else
    {
      printError(" You must provide valid username and password.", sp);
      releaseSession(sess);
      return;
    }
    
    String result = new String();
    
    result = result + "<h2>Information about " + user.getNickname() + "<br></h2>";
    result = result + "Real life name : " + user.getFirstname() + " " + user.getLastname() + "<br>";
    result = result + "Email address  : " + user.getEmail() + "<br>";
    result = result + "User since     : " + user.getCreationDate() + "<br>";
    result = result + "Current rating : <b>" + user.getRating() + "</b><br>";
    sp.printHTMLheader("RUBiS: About " + user.getNickname());
    sp.printHTML(result);
    
    listBids(user, sess, sp);
    listItems(user, sess, sp);
    listWonItems(user, sess, sp);
    listBoughtItems(user, sess, sp);
    listComment(user, sess, sp);
    
    sp.printHTMLfooter();
    
    releaseSession(sess);
  }

}
