package edu.rice.rubis.servlets;

import edu.rice.rubis.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

/** This servlets displays the full description of a given item
 * and allows the user to bid on this item.
 * It must be called this way :
 * <pre>
 * http://..../ViewItem?itemId=xx where xx is the id of the item
 * /<pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class ViewItem extends RubisHttpServlet
{
  private ServletPrinter sp = null;
  private PreparedStatement stmt = null;
  private Connection conn = null;

  public int getPoolSize()
  {
    return Config.ViewItemPoolSize;
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
    sp.printHTMLheader("RUBiS ERROR: View item");
    sp.printHTML("<h2>We cannot process your request due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    closeConnection();
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    sp = new ServletPrinter(response, "ViewItem");
    ResultSet rs = null;

    String value = request.getParameter("itemId");
    if ((value == null) || (value.equals("")))
    {
      printError("No item identifier received - Cannot process the request<br>");
      return ;
    }
    Integer itemId = new Integer(value);
    // get the item
    try
    {
      conn = getConnection();
      stmt = conn.prepareStatement("SELECT * FROM items WHERE id=?");
      stmt.setInt(1, itemId.intValue());
      rs = stmt.executeQuery();
    }
    catch (Exception e)
    {
      sp.printHTML("Failed to execute Query for item: " +e);
      closeConnection();
      return;
    }
    try 
    {
      if (!rs.first())
      {
        stmt = conn.prepareStatement("SELECT * FROM old_items WHERE id=?");
        stmt.setInt(1, itemId.intValue());
        rs = stmt.executeQuery();
      }
    }
    catch (Exception e)
    {
      sp.printHTML("Failed to execute Query for item in table old_items: " +e);
      closeConnection();
      return ;
    }
    try 
    {
      if (!rs.first())
      {
        sp.printHTML("<h2>This item does not exist!</h2>");		    
        closeConnection();
        return;
      }
      String itemName, endDate, startDate, description, sellerName;
      float maxBid, initialPrice, buyNow, reservePrice;
      int quantity, sellerId, nbOfBids=0;
      itemName = rs.getString("name");
      description = rs.getString("description");
      endDate = rs.getString("end_date");
      startDate = rs.getString("start_date");
      initialPrice = rs.getFloat("initial_price");
      reservePrice = rs.getFloat("reserve_price");
      buyNow = rs.getFloat("buy_now");
      quantity = rs.getInt("quantity");
      sellerId = rs.getInt("seller");

      maxBid = rs.getFloat("max_bid");
      nbOfBids = rs.getInt("nb_of_bids");
      if (maxBid <initialPrice)
        maxBid = initialPrice;

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
          sp.printHTML("Unknown seller");
          closeConnection();
          return;
        }

      }
      catch (SQLException e)
      {
        sp.printHTML("Failed to executeQuery for seller: " +e);
        closeConnection();
        return;
      }
      sp.printItemDescription(itemId.intValue(), itemName, description, initialPrice, reservePrice, buyNow, quantity, maxBid, nbOfBids, sellerName, sellerId, startDate, endDate, -1, conn);
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
