package edu.rice.rubis.servlets;

import edu.rice.rubis.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

/** Builds the html page with the list of all categories and provides links to browse all
    items in a category or items in a category for a given region */
public class BrowseCategories extends RubisHttpServlet
{
  private ServletPrinter sp = null;
  private PreparedStatement stmt = null;
  private Connection conn = null;

  public int getPoolSize()
  {
    return Config.BrowseCategoriesPoolSize;
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


  /** List all the categories in the database */
  private void categoryList(int regionId, int userId) 
  {
    String categoryName;
    int categoryId;
    ResultSet rs = null;
    //     try 
    //     {
    // 	faster if made inside a Tx
    // 	conn.setAutoCommit(false);
    //     }
    //     catch (Exception ex)
    //     {
    // 	sp.printHTML("Failed to create transaction: " +ex);
    // 	closeConnection();
    //     }
    // get the list of categories
    try
    {
      stmt = conn.prepareStatement("SELECT name, id FROM categories");
      rs = stmt.executeQuery();
    }
    catch (Exception e)
    {
      sp.printHTML("Failed to execute Query for categories list: " +e);
      closeConnection();
      return;
    }
    try 
    {
      if (!rs.first()) 
      {
        sp.printHTML("<h2>Sorry, but there is no category available at this time. Database table is empty</h2><br>");
        closeConnection();
        return;
      }
      else
        sp.printHTML("<h2>Currently available categories</h2><br>");
  
      do
      {
        categoryName = rs.getString("name");
        categoryId = rs.getInt("id");

        if (regionId != -1)
        {
          sp.printCategoryByRegion(categoryName, categoryId, regionId);
        }
        else
        {
          if (userId != -1)
            sp.printCategoryToSellItem(categoryName, categoryId, userId);
          else
            sp.printCategory(categoryName, categoryId);
        }
      }
      while (rs.next());
      //conn.commit();
    } 
    catch (Exception e) 
    {
      sp.printHTML("Exception getting categories list: " + e +"<br>");
	
      // 		       try
      // 		       {
      // 		         conn.rollback();
      // 		       }
      // 		       catch (Exception se) 
      // 		       {
      // 		         sp.printHTML("Transaction rollback failed: " + e +"<br>");
      // 		       }
      closeConnection();
    }
  }


  /** Build the html page for the response */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    int     regionId = -1, userId = -1;
    String  username=null, password=null;

    sp = new ServletPrinter(response, "BrowseCategories");
    sp.printHTMLheader("RUBiS available categories");

    username = request.getParameter("nickname");
    password = request.getParameter("password");
    
    conn = getConnection();

    // Authenticate the user who want to sell items
    if ((username != null && username !="") || (password != null && password !=""))
    {
      Auth auth = new Auth(conn, sp);
      userId = auth.authenticate(username, password);
      if (userId == -1)
      {
        sp.printHTML(" You don't have an account on RUBiS!<br>You have to register first.<br>");
        sp.printHTMLfooter();
	closeConnection();
        return ;	
      }
    }
    
    String value = request.getParameter("region");
    if ((value != null) && (!value.equals("")))
    {
      // get the region ID
      try
      {
        stmt = conn.prepareStatement("SELECT id FROM regions WHERE name=?");
        stmt.setString(1, value);
        ResultSet rs = stmt.executeQuery();
        if (!rs.first())
        {
          sp.printHTML(" Region "+value+" does not exist in the database!<br>");
          closeConnection();
          return ;
        }
        regionId = rs.getInt("id");


      }
      catch (SQLException e)
      {
        sp.printHTML("Failed to execute Query for region: " +e);
        closeConnection();
        return;
      }
    }

    categoryList(regionId, userId);    	

    sp.printHTMLfooter();

  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    doGet(request, response);
  }

}
