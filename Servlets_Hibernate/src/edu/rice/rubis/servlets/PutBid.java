package edu.rice.rubis.servlets;

import java.io.IOException;

import java.util.List;
import java.util.Iterator;

import net.sf.hibernate.Session;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.rice.rubis.hibernate.User;
import edu.rice.rubis.hibernate.Item;

/** This servlets display the page allowing a user to put a bid
 * on an item.
 * It must be called this way :
 * <pre>
 * http://..../PutBid?itemId=xx&nickname=yy&password=zz
 *    where xx is the id of the item
 *          yy is the nick name of the user
 *          zz is the user password
 * /<pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class PutBid extends HibernateRubisHttpServlet
{

  public int getPoolSize()
  {
    return Config.PutBidPoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: PutBid");
    sp.printHTML(
      "<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    ServletPrinter sp = null;
    Session sess = null;
    User user = null;
    Item item = null;
    Float maxBid = null;
    Integer nbOfBids = null;
    
    String itemStr = request.getParameter("itemId");
    String nick = request.getParameter("nickname");
    String pass = request.getParameter("password");
    sp = new ServletPrinter(response, "PutBid");
    
    if ((itemStr == null)
      || (itemStr.equals(""))
      || (nick == null)
      || (nick.equals(""))
      || (pass == null)
      || (pass.equals("")))
    {
      printError("Item id, name and password are required - Cannot process the request<br>", sp);
      return;
    }
    
    // Authenticate the user who want to bid
    sess = getSession();
    Auth auth = new Auth(sess, sp);
    user = auth.authenticate(nick, pass);
    if (user == null)
    {
      printError(" You don't have an account on RUBiS!<br>You have to register first.<br>", sp);
      releaseSession(sess);
      return;
    }
    
    // Try to find the Item corresponding to the Item ID
    Integer itemId = new Integer(itemStr);
    
    try
    {
      item = (Item) sess.get(Item.class, itemId);
    }
    catch (HibernateException he)
    {
      printError("Failed to execute Query for item: " + he, sp);
      releaseSession(sess);
      return;
    }
    if (item == null)
    {
      printError("<h2>This item does not exist!</h2>", sp);
      releaseSession(sess);
      return;
    }
    
    try
    {
      Query q = sess.createFilter(item.getBids(), "select max(this.bid)");
      List lst = q.list();
      Iterator it = lst.iterator();
      
      // Get the current price (max bid)
      if (it.hasNext())
        maxBid = (Float) it.next();
      else
        maxBid = item.getInitialPrice();
    }
    catch (HibernateException he)
    {
      printError("Failed to executeQuery for max bid: " + he, sp);
      releaseSession(sess);
      return;
    }
    
    try
    {
      Query q = sess.createFilter(item.getBids(), "select count(*)");
      List lst = q.list();
      Iterator it = lst.iterator();
      
      if (it.hasNext())
        nbOfBids = (Integer) it.next();
    }
    catch (HibernateException he)
    {
      printError("Failed to executeQuery for number of bids: " + he, sp);
      releaseSession(sess);
      return;
    }
    
    sp.printItemDescription(item, maxBid, nbOfBids, user, sess);
    
    releaseSession(sess);
    
    sp.printHTMLfooter();
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doGet(request, response);
  }

  /**
  * Clean up the connection pool.
  */
  public void destroy()
  {
    super.destroy();
  }
}
