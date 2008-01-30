package edu.rice.rubis.servlets;

import java.io.IOException;

import net.sf.hibernate.Session;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.rice.rubis.hibernate.Item;

/** This servlets displays the full description of a given item
 * and allows the user to bid on this item.
 * It must be called this way :
 * <pre>
 * http://..../ViewItem?itemId=xx where xx is the id of the item
 * /<pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class ViewItem extends HibernateRubisHttpServlet
{


  public int getPoolSize()
  {
    return Config.ViewItemPoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: View item");
    sp.printHTML("<h2>We cannot process your request due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    Integer itemId = null;
    Item item = null;
    ServletPrinter sp = null;
    Session sess = null;
    
    sp = new ServletPrinter(response, "ViewItem");
    
    String itemStr = request.getParameter("itemId");
    if (itemStr == null || itemStr.equals(""))
    {
      printError("No item identifier received - Cannot process the request<br>", sp);
      return;
    }
    itemId = new Integer(itemStr);
    
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
    
    sp.printItemDescription(item, item.getMaxBid(), item.getNbOfBids(), null, sess);
    
    releaseSession(sess);
    
    sp.printHTMLfooter();
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doGet(request, response);
  }

}
