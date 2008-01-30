package edu.rice.rubis.servlets;

import java.io.IOException;

import java.util.Date;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.HibernateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.rice.rubis.hibernate.Item;
import edu.rice.rubis.hibernate.User;
import edu.rice.rubis.hibernate.Bid;


/** This servlet records a bid in the database and display
 * the result of the transaction.
 * It must be called this way :
 * <pre>
 * http://..../StoreBid?itemId=aa&userId=bb&minBid=cc&maxQty=dd&bid=ee&maxBid=ff&qty=gg 
 *   where: aa is the item id 
 *          bb is the user id
 *          cc is the minimum acceptable bid for this item
 *          dd is the maximum quantity available for this item
 *          ee is the user bid
 *          ff is the maximum bid the user wants
 *          gg is the quantity asked by the user
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class StoreBid extends HibernateRubisHttpServlet
{


  public int getPoolSize()
  {
    return Config.StoreBidPoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: StoreBid");
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
   * Store the bid to the database and display resulting message.
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    Integer userId = null; // user id
    Integer itemId = null; // item id
    Float minBid = null; // minimum acceptable bid for this item
    Float bid = null; // user bid
    Float maxBid = null; // maximum bid the user wants
    Integer maxQty = null; // maximum quantity available for this item
    Integer qty = null ; // quantity asked by the user
    ServletPrinter sp = null;
    Session sess = null;
    Transaction trans = null;
    
    sp = new ServletPrinter(response, "StoreBid");
    
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
    
    String minBidStr = request.getParameter("minBid");
    if (minBidStr == null || minBidStr.equals(""))
    {
      printError("<h3>You must provide a minimum bid !<br></h3>", sp);
      return;
    }
    minBid = new Float(minBidStr);
    
    String bidStr = request.getParameter("bid");
    if (bidStr == null || bidStr.equals(""))
    {
      printError("<h3>You must provide a bid !<br></h3>", sp);
      return;
    }
    bid = new Float(bidStr);
    
    String maxBidStr = request.getParameter("maxBid");
    if (maxBidStr == null || maxBidStr.equals(""))
    {
      printError("<h3>You must provide a maximum bid !<br></h3>", sp);
      return;
    }
    maxBid = new Float(maxBidStr);
    
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
    if (bid.compareTo(minBid) < 0)
    {
      printError(
        "<h3>Your bid of $"
          + bid
          + " is not acceptable because it is below the $"
          + minBid
          + " minimum bid !<br></h3>", sp);
      return;
    }
    if (maxBid.compareTo(minBid) < 0)
    {
      printError(
        "<h3>Your maximum bid of $"
          + maxBid
          + " is not acceptable because it is below the $"
          + minBid
          + " minimum bid !<br></h3>", sp);
      return;
    }
    if (maxBid.compareTo(bid) < 0)
    {
      printError(
        "<h3>Your maximum bid of $"
          + maxBid
          + " is not acceptable because it is below your current bid of $"
          + bid
          + " !<br></h3>", sp);
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
          printError("Transaction rollback failed: " + he, sp);
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
          printError("Transaction rollback failed: " + he, sp);
        }
        printError("This user does not exist in the database.", sp);
        releaseSession(sess);
        return;
      }
      */
      
      Date now = new Date();
      
      Bid newBid = new Bid();
      newBid.setUser(user);
      newBid.setItem(item);
      newBid.setQty(qty);
      newBid.setBid(bid);
      newBid.setMaxBid(maxBid);
      newBid.setDate(now);
      
      sess.save(newBid);
      
      if (Hibernate.isInitialized(user) && Hibernate.isInitialized(user.getBids()))
      {
        user.getBids().add(newBid);
      }
      if (Hibernate.isInitialized(item) && Hibernate.isInitialized(item.getBids()))
      {
        item.getBids().add(newBid);
      }
      
      // update the number of bids and the max bid for the item
      item.setNbOfBids(new Integer(item.getNbOfBids().intValue() + 1));
      if (bid.compareTo(item.getMaxBid()) > 0)
      {
        item.setMaxBid(maxBid);
      }
      
      sp.printHTMLheader("RUBiS: Bidding result");
      sp.printHTML("<center><h2>Your bid has been successfully processed.</h2></center>\n");
      trans.commit();
      releaseSession(sess);
    }
    catch (Exception e)
    {
      sp.printHTML("Error while storing the bid (got exception: " + e + ")<br>");
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

}
