package edu.rice.rubis.servlets;

import java.io.IOException;

import java.util.Date;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.rice.rubis.hibernate.User;
import edu.rice.rubis.hibernate.Item;
import edu.rice.rubis.hibernate.Buy;

/** This servlet records a BuyNow in the database and display
 * the result of the transaction.
 * It must be called this way :
 * <pre>
 * http://..../StoreBuyNow?itemId=aa&userId=bb&minBuyNow=cc&maxQty=dd&BuyNow=ee&maxBuyNow=ff&qty=gg 
 *   where: aa is the item id 
 *          bb is the user id
 *          cc is the minimum acceptable BuyNow for this item
 *          dd is the maximum quantity available for this item
 *          ee is the user BuyNow
 *          ff is the maximum BuyNow the user wants
 *          gg is the quantity asked by the user
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class StoreBuyNow extends HibernateRubisHttpServlet
{

  public int getPoolSize()
  {
    return Config.StoreBuyNowPoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: StoreBuyNow");
    sp.printHTML(
      "<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    
  }

  /**
   * Call the <code>doPost</code> method.
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
   * Store the BuyNow to the database and display resulting message.
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    Integer userId = null; // item id
    Integer itemId = null; // user id
    Integer maxQty = null; // maximum quantity available for this item
    Integer qty = null; // quantity asked by the user
    ServletPrinter sp = null;
    Session sess = null;
    Transaction trans = null;
    
    sp = new ServletPrinter(response, "StoreBuyNow");
    
    /* Get and check all parameters */
    String userStr = request.getParameter("userId");
    if (userStr == null || userStr.equals(""))
    {
      printError("<h3>You must provide a user identifier !<br></h3>", sp);
      return;
    }
    userId = new Integer(userStr);
    
    String itemStr = request.getParameter("itemId");
    if (itemStr == null || itemStr.equals(""))
    {
      printError("<h3>You must provide an item identifier !<br></h3>", sp);
      return;
    }
    itemId = new Integer(itemStr);
    
    String maxQtyStr = request.getParameter("maxQty");
    if (maxQtyStr == null || maxQtyStr.equals(""))
    {
      printError("<h3>You must provide a maximum quantity !<br></h3>", sp);
      return;
    }
    maxQty = new Integer(maxQtyStr);
    
    String qtyStr = request.getParameter("qty");
    if (qtyStr == null || qtyStr.equals(""))
    {
      printError("<h3>You must provide a quantity !<br></h3>", sp);
      return;
    }
    qty = new Integer(qtyStr);
    
    /* Check for invalid values */
    if (qty.compareTo(maxQty) > 0)
    {
      printError(
        "<h3>You cannot request "
          + qty
          + " items because only "
          + maxQty
          + " are proposed !<br></h3>", sp);
      return;
    }
    
    try
    {
      sess = getSession();
      trans = sess.beginTransaction();
      
      Item item = (Item) sess.load(Item.class, itemId);
      User user = (User) sess.load(User.class, userId);
      
      /*
      if (item == null)
      {
        try
        {
          trans.rollback();
        }
        catch (HibernateException he)
        {
          printError("Transaction rollback failed: " + he + "<br>", sp);
        }
        printError("This item does not exist in the database.", sp);
        releaseSession(sess);
        return;
      }
      if (user == null)
      {
        try
        {
          trans.rollback();
        }
        catch (HibernateException he)
        {
          printError("Transaction rollback failed: " + he + "<br>", sp);
        }
        printError("This item does not exist in the database.", sp);
        releaseSession(sess);
        return;
      }
      */
      
      Date now = new Date();
      
      Buy newBuy = new Buy();
      newBuy.setBuyer(user);
      newBuy.setItem(item);
      newBuy.setQty(qty);
      newBuy.setDate(now);
      
      sess.save(newBuy);
      
      if (Hibernate.isInitialized(item) && Hibernate.isInitialized(item.getBuys()))
      {
        item.getBuys().add(newBuy);
      }
      if (Hibernate.isInitialized(user) && Hibernate.isInitialized(user.getBuys()))
      {
        user.getBuys().add(newBuy);
      }
      
      item.setQuantity(new Integer(item.getQuantity().intValue() - qty.intValue()));
      if (item.getQuantity().intValue() == 0)
      {
        item.setEndDate(now);
      }
      
      sp.printHTMLheader("RUBiS: BuyNow result");
      if (qty.intValue() == 1)
        sp.printHTML(
          "<center><h2>Your have successfully bought this item.</h2></center>\n");
      else
        sp.printHTML(
          "<center><h2>Your have successfully bought these items.</h2></center>\n");
          
      trans.commit();
      releaseSession(sess);
    }
    catch (Exception e)
    {
      sp.printHTML("Failed to execute query: " + e + "<br>");
      try
      {
        trans.rollback();
      }
      catch (HibernateException he)
      {
        printError("Transaction rollback failed: " + he, sp);
      }
      releaseSession(sess);
      return;
    }
    
    sp.printHTMLfooter();
  }

  /**
  * Clean up the connection pool.
  */
  public void destroy()
  {
    super.destroy();
  }

}
