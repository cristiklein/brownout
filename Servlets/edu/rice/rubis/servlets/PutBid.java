package edu.rice.rubis.servlets;

import edu.rice.rubis.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

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


public class PutBid extends RubisHttpServlet
{
  private ServletPrinter sp = null;
  private PreparedStatement stmt = null;
  private Connection conn = null;

  public int getPoolSize()
  {
    return Config.PutBidPoolSize;
  }

  private void closeConnection()
  {
    try 
    {
      if (stmt != null) stmt.close();	// close statement
    } 
    catch (Exception ignore) 
    {
    }
  }

  private void printError(String errorMsg)
  {
    sp.printHTMLheader("RUBiS ERROR: PutBid");
    sp.printHTML("<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    closeConnection();
  }


  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    String itemStr = request.getParameter("itemId");
    String name = request.getParameter("nickname");
    String pass = request.getParameter("password");
    sp = new ServletPrinter(response, "PubBid");
    
    if ((itemStr == null) || (itemStr.equals("")) ||
        (name == null) || (name.equals(""))||
        (pass == null) || (pass.equals("")))
    {
      printError("Item id, name and password are required - Cannot process the request<br>");
      return ;
    }
    Integer itemId = new Integer(itemStr);

    conn = getConnection();
    // Authenticate the user who want to bid
    Auth auth = new Auth(conn, sp);
    int userId = auth.authenticate(name, pass);
    if (userId == -1)
    {
      printError(" You don't have an account on RUBiS!<br>You have to register first.<br>");
      return ;	
    }

    // Try to find the Item corresponding to the Item ID
    String itemName, endDate, startDate, description, sellerName;
    float maxBid, initialPrice, buyNow, reservePrice;
    int quantity, sellerId, nbOfBids=0;
    ResultSet rs = null;
    try
    {
      stmt = conn.prepareStatement("SELECT * FROM items WHERE id=?");
      stmt.setInt(1, itemId.intValue());
      rs = stmt.executeQuery();
    }
    catch (Exception e)
    {
      printError("Failed to execute Query for item: " +e);
      return;
    }
    try 
    {
      if (!rs.first())
      {
        printError("<h2>This item does not exist!</h2>");		    
        return;
      }
      itemName = rs.getString("name");
      description = rs.getString("description");
      endDate = rs.getString("end_date");
      startDate = rs.getString("start_date");
      initialPrice = rs.getFloat("initial_price");
      reservePrice = rs.getFloat("reserve_price");
      buyNow = rs.getFloat("buy_now");
      quantity = rs.getInt("quantity");
      sellerId = rs.getInt("seller");
      try 
      {
        PreparedStatement sellerStmt = conn.prepareStatement("SELECT nickname FROM users WHERE id=?");
        sellerStmt.setInt(1, sellerId);
        ResultSet sellerResult = sellerStmt.executeQuery();
        // Get the seller's name		 
        if (sellerResult.first()) 
          sellerName = sellerResult.getString("nickname");
        else
        {	
          printError("Unknown seller");
          return;
        }

      }
      catch (SQLException e)
      {
        printError("Failed to executeQuery for seller: " +e);
        return;
      }
      try 
      {
        PreparedStatement maxBidStmt = conn.prepareStatement("SELECT MAX(bid) AS bid FROM bids WHERE item_id=?");
        maxBidStmt.setInt(1, itemId.intValue());
        ResultSet maxBidResult = maxBidStmt.executeQuery();
        // Get the current price (max bid)		 
        if (maxBidResult.first()) 
          maxBid = maxBidResult.getFloat("bid");
        else
          maxBid = initialPrice;
      }
      catch (SQLException e)
      {
        printError("Failed to executeQuery for max bid: " +e);
        return;
      }
      try 
      {
        PreparedStatement nbStmt = conn.prepareStatement("SELECT COUNT(*) AS bid FROM bids WHERE item_id=?");
        nbStmt.setInt(1, itemId.intValue());
        ResultSet nbResult = nbStmt.executeQuery();
        // Get the number of bids for this item
        if (nbResult.first()) 
          nbOfBids = nbResult.getInt("bid");
      }
      catch (SQLException e)
      {
        printError("Failed to executeQuery for number of bids: " +e);
        return;
      }
      sp.printItemDescription(itemId.intValue(), itemName, description, initialPrice, reservePrice, buyNow, quantity, maxBid, nbOfBids, sellerName, sellerId, startDate, endDate, userId, conn);
    } 
    catch (Exception e) 
    {
      printError("Exception getting item list: " + e +"<br>");
    }
    sp.printHTMLfooter();
  }


  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    doGet(request, response);
  }
}
