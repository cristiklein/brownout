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

import edu.rice.rubis.hibernate.Item;
import edu.rice.rubis.hibernate.Bid;

/** This servlets displays the list of bids regarding an item.
 * It must be called this way :
 * <pre>
 * http://..../ViewUserInfo?itemId=xx where xx is the id of the item
 * /<pre>
 */

public class ViewBidHistory extends HibernateRubisHttpServlet
{


  public int getPoolSize()
  {
    return Config.ViewBidHistoryPoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: View bid history");
    sp.printHTML("<h2>We cannot process your request due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    
  }

  /** List the bids corresponding to an item */
  private void listBids(Item item, Session sess, ServletPrinter sp)
  {
    List lst = null;
    
    // Get the list of the item's last bids
    try
    {
      Query q = sess.createFilter(item.getBids(), "select this order by this.date desc");
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
        sp.printHTML("<h3>There is no bid corresponding to this item.</h3><br>");
        return;
      }
      
      sp.printBidHistoryHeader();
      
      do
      {
        Bid bid = (Bid) it.next();
        
        sp.printBidHistory(bid);
      }
      while (it.hasNext());
    }
    catch (Exception e)
    {
      sp.printHTML("Exception getting bid: " + e + "<br>");
      return;
    }
    
    sp.printBidHistoryFooter();
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doPost(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    Integer itemId = null;
    Item item = null;
    ServletPrinter sp = null;
    Session sess = null;
    
    sp = new ServletPrinter(response, "ViewBidHistory");
    
    String itemStr = request.getParameter("itemId");
    if (itemStr == null || itemStr.equals(""))
    {
      sp.printHTMLheader("RUBiS ERROR: View bids history");
      sp.printHTML("<h3>You must provide an item identifier !<br></h3>");
      sp.printHTMLfooter();
      return;
    }
    itemId = new Integer(itemStr);
    
    sp.printHTMLheader("RUBiS: Bid history");
    
    sess = getSession();
    
    try
    {
      item = (Item) sess.get(Item.class, itemId);
    }
    catch (HibernateException he)
    {
      printError("Exception getting item: " + he + "<br>", sp);
      releaseSession(sess);
      return;
    }
    if (item == null)
    {
      printError("This item does not exist in the database.", sp);
      releaseSession(sess);
      return;
    }
    
    sp.printHTML("<center><h3>Bid History for " + item.getName() + "<br></h3></center>");
    
    listBids(item, sess, sp);
    
    releaseSession(sess);
    
    sp.printHTMLfooter();
  }

}
