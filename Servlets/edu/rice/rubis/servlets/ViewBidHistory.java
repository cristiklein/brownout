package edu.rice.rubis.servlets;

import edu.rice.rubis.*;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

/** This servlets displays the list of bids regarding an item.
 * It must be called this way :
 * <pre>
 * http://..../ViewUserInfo?itemId=xx where xx is the id of the item
 * /<pre>
 */

public class ViewBidHistory extends RubisHttpServlet
{
  private ServletPrinter sp = null;
  private PreparedStatement stmt = null;
  private Connection conn = null;

  public int getPoolSize()
  {
    return Config.ViewBidHistoryPoolSize;
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

 
  /** List the bids corresponding to an item */
  private void listBids(Integer itemId) 
  {
    float bid;
    int userId;
    String bidderName, date;
    ResultSet rs = null;

    // Get the list of the user's last bids
    try 
    {
      stmt = conn.prepareStatement("SELECT * FROM bids WHERE item_id=? ORDER BY date DESC");
      stmt.setInt(1, itemId.intValue());
      rs = stmt.executeQuery();
      if (!rs.first())
      {
        sp.printHTML("<h3>There is no bid corresponding to this item.</h3><br>");
        closeConnection();
        return ;
      }
    }
    catch (SQLException e)
    {
      sp.printHTML("Exception getting bids list: " +e+"<br>");
      closeConnection();
      return;
    }

    sp.printBidHistoryHeader();
    try
    {	  
      do 
      {
        // Get the bids
        date = rs.getString("date");
        bid = rs.getFloat("bid");
        userId = rs.getInt("user_id");

        ResultSet urs = null;
        try 
        {
          stmt = conn.prepareStatement("SELECT nickname FROM users WHERE id=?");
          stmt.setInt(1, userId);
          urs = stmt.executeQuery();
          if (!urs.first())
          {
            sp.printHTML("This user does not exist in the database.<br>");
            closeConnection();
            return;
          }
          bidderName = urs.getString("nickname");
        }
        catch (SQLException e)
        {
          sp.printHTML("Couldn't get bidder name: " +e+"<br>");
          closeConnection();
          return;
        }
        sp.printBidHistory(userId, bidderName, bid, date);
      }
      while(rs.next());
    }
    catch (SQLException e)
    {
      sp.printHTML("Exception getting bid: " +e+"<br>");
      closeConnection();
      return;
    }
    sp.printBidHistoryFooter();
  }


  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    doPost(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    String  value = request.getParameter("itemId");
    Integer itemId;
    String itemName;
    ResultSet rs = null;

    sp = new ServletPrinter(response, "ViewBidHistory");

    if ((value == null) || (value.equals("")))
    {
      sp.printHTMLheader("RUBiS ERROR: View bids history");
      sp.printHTML("<h3>You must provide an item identifier !<br></h3>");
      sp.printHTMLfooter();
      return ;
    }
    else
      itemId = new Integer(value);

    sp.printHTMLheader("RUBiS: Bid history");

    // get the item
    try
    {
      conn = getConnection();
      stmt = conn.prepareStatement("SELECT name FROM items WHERE id=?");
      stmt.setInt(1, itemId.intValue());
      rs = stmt.executeQuery();
    }
    catch (Exception e)
    {
      sp.printHTML("Failed to execute Query for item in table items: " +e);
      closeConnection();
      return;
    }
    try 
    {
      if (!rs.first())
      {
        stmt = conn.prepareStatement("SELECT name FROM old_items WHERE id=?");
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
      itemName = rs.getString("name");
      sp.printHTML("<center><h3>Bid History for "+itemName+"<br></h3></center>");
    }
    catch (Exception e)
    {
      sp.printHTML("This item does not exist (got exception: " +e+")<br>");
      sp.printHTMLfooter();
      closeConnection();
      return ;
    }

    listBids(itemId);
    sp.printHTMLfooter();
  }

}
