package edu.rice.rubis.servlets;

import edu.rice.rubis.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

/**
 * Build the html page with the list of all items for given category and region.
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public class SearchItemsByRegion extends RubisHttpServlet
{
  private ServletPrinter sp = null;
  private PreparedStatement stmt = null;
  private Connection conn = null;

  public int getPoolSize()
  {
    return Config.SearchItemsByRegionPoolSize;
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
    sp.printHTMLheader("RUBiS ERROR: SearchItemsByRegion");
    sp.printHTML("<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    closeConnection();
  }


  /** List items in the given category for the given region */
  private void itemList(Integer categoryId, Integer regionId, int page, int nbOfItems) 
  {
    String itemName, endDate;
    int itemId, nbOfBids=0;
    float maxBid;
    ResultSet rs = null;

    // get the list of items
    try
    {
      conn = getConnection();
      stmt = conn.prepareStatement("SELECT items.name, items.id, items.end_date, items.max_bid, items.nb_of_bids, items.initial_price FROM items,users WHERE items.category=? AND items.seller=users.id AND users.region=? AND end_date>=NOW() LIMIT ?,?");
      stmt.setInt(1, categoryId.intValue());
      stmt.setInt(2, regionId.intValue());
      stmt.setInt(3, page*nbOfItems);
      stmt.setInt(4, nbOfItems);
      rs = stmt.executeQuery();
    }
    catch (Exception e)
    {
      sp.printHTML("Failed to execute Query for items in region: " +e);
      closeConnection();
      return;
    }
    try 
    {
      if (!rs.first())
      {
        if (page == 0)
        {
          sp.printHTML("<h3>Sorry, but there is no items in this category for this region.</h3><br>");
        }
        else
        {
          sp.printHTML("<h3>Sorry, but there is no more items in this category for this region.</h3><br>");
          sp.printItemHeader();
          sp.printItemFooter("<a href=\"/servlet/edu.rice.rubis.servlets.SearchItemsByRegion?category="+categoryId+
                             "&region="+regionId+"&page="+(page-1)+"&nbOfItems="+nbOfItems+"\">Previous page</a>", "");
        }
        closeConnection();
        return;
      }
  
      sp.printItemHeader();
      do
      {
        itemName = rs.getString("name");
        itemId = rs.getInt("id");
        endDate = rs.getString("end_date");
        maxBid = rs.getFloat("max_bid");
        nbOfBids = rs.getInt("nb_of_bids");
        float initialPrice = rs.getFloat("initial_price");
        if (maxBid <initialPrice)
          maxBid = initialPrice;
        sp.printItem(itemName, itemId, maxBid, nbOfBids, endDate);
      }
      while (rs.next()); 
      if (page == 0)
      {
        sp.printItemFooter("", "<a href=\"/servlet/edu.rice.rubis.servlets.SearchItemsByRegion?category="+categoryId+
                           "&region="+regionId+"&page="+(page+1)+"&nbOfItems="+nbOfItems+"\">Next page</a>");
      }
      else
      {
        sp.printItemFooter("<a href=\"/servlet/edu.rice.rubis.servlets.SearchItemsByRegion?category="+categoryId+
                           "&region="+regionId+"&page="+(page-1)+"&nbOfItems="+nbOfItems+"\">Previous page</a>",
                           "<a href=\"/servlet/edu.rice.rubis.servlets.SearchItemsByRegion?category="+categoryId+
                           "&region="+regionId+"&page="+(page+1)+"&nbOfItems="+nbOfItems+"\">Next page</a>");
      }
    } 
    catch (Exception e) 
    {
      sp.printHTML("Exception getting item list: " + e +"<br>");
    }
  }


  /* Read the parameters, lookup the remote category and region  and build the web page with
     the list of items */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    Integer  categoryId, regionId;
    Integer page;
    Integer nbOfItems;

    sp = new ServletPrinter(response, "SearchItemsByRegion");

    String value = request.getParameter("category");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a category!<br>");
      return ;
    }
    else
      categoryId = new Integer(value);

    value = request.getParameter("region");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a region!<br>");
      return ;
    }
    else
      regionId = new Integer(value);

    value = request.getParameter("page");
    if ((value == null) || (value.equals("")))
      page = new Integer(0);
    else
      page = new Integer(value);

    value = request.getParameter("nbOfItems");
    if ((value == null) || (value.equals("")))
      nbOfItems = new Integer(25);
    else
      nbOfItems = new Integer(value);

    sp.printHTMLheader("RUBiS: Search items by region");
    itemList(categoryId, regionId, page.intValue(), nbOfItems.intValue());
    sp.printHTMLfooter();
  }


  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    doGet(request, response);
  }
}
