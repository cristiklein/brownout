package edu.rice.rubis.servlets;

import edu.rice.rubis.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

/** 
 * Add a new item in the database 
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public class RegisterItem extends RubisHttpServlet
{
  private ServletPrinter sp = null;
  private PreparedStatement stmt = null;
  private Connection conn = null;

  public int getPoolSize()
  {
    return Config.RegisterItemPoolSize;
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
    sp.printHTMLheader("RUBiS ERROR: Register user");
    sp.printHTML("<h2>Your registration has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    closeConnection();
  }

  /** Check the values from the html register item form and create a new item */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    String  name=null, description=null;
    float   initialPrice, buyNow, reservePrice;
    Float   stringToFloat;
    int     quantity, duration;
    Integer categoryId, userId, stringToInt;
    String  startDate, endDate;
    int     itemId;

    sp = new ServletPrinter(response, "RegisterItem");
      
    String value = request.getParameter("name");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a name!<br>");
      return ;
    }
    else
      name = value;

    value = request.getParameter("description");
    if ((value == null) || (value.equals("")))
    {
      description="No description.";
    }
    else
      description = value;

    value = request.getParameter("initialPrice");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide an initial price!<br>");
      return ;
    }
    else
    {
      stringToFloat = new Float(value);
      initialPrice = stringToFloat.floatValue();
    }

    value = request.getParameter("reservePrice");
    if ((value == null) || (value.equals("")))
    {
      reservePrice = 0;
    }
    else
    {
      stringToFloat = new Float(value);
      reservePrice = stringToFloat.floatValue();

    }

    value = request.getParameter("buyNow");
    if ((value == null) || (value.equals("")))
    {
      buyNow = 0;
    }
    else
    {
      stringToFloat = new Float(value);
      buyNow = stringToFloat.floatValue();
    }
 
    value = request.getParameter("duration");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a duration!<br>");
      return ;
    }
    else
    {
      stringToInt = new Integer(value);
      duration = stringToInt.intValue();
      GregorianCalendar now, later;
      now = new GregorianCalendar();
      later = TimeManagement.addDays(now, duration);
      startDate = TimeManagement.dateToString(now);
      endDate = TimeManagement.dateToString(later);
    }

    value = request.getParameter("quantity");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a quantity!<br>");
      return ;
    }
    else
    {
      stringToInt = new Integer(value);
      quantity = stringToInt.intValue();
    }
 
    userId = new Integer(request.getParameter("userId"));
    categoryId = new Integer(request.getParameter("categoryId"));

    try 
    {
      conn = getConnection();
      conn.setAutoCommit(false);	// faster if made inside a Tx

      // Try to create a new item
      try 
      {
        stmt = conn.prepareStatement("INSERT INTO items VALUES (NULL, \""+name+
                                     "\", \""+description+"\", \""+initialPrice+"\", \""+
                                     quantity+"\", \""+reservePrice+"\", \""+buyNow+
                                     "\", 0, 0, \""+startDate+"\", \""+endDate+"\", \""+userId+
                                     "\", "+ categoryId+")");
        stmt.executeUpdate();
      }
      catch (SQLException e)
      {
        printError("RUBiS internal error: Item registration failed (got exception: " +e+")<br>");
        return;
      }
      // To test if the item was correctly added in the database
      try
      {
        stmt = conn.prepareStatement("SELECT id FROM items WHERE name=?");
        stmt.setString(1, name);
        ResultSet irs = stmt.executeQuery();
        if (!irs.first())
        {
          printError("This item does not exist in the database.");
          return;
        }
        itemId = irs.getInt("id");
      }
      catch (SQLException e)
      {
        printError("Failed to execute Query for the new item: " +e);
        return;
      }
 
      sp.printHTMLheader("RUBiS: Item to sell "+name);
      sp.printHTML("<h2>Your Item has been successfully registered.</h2><br>");
      sp.printHTML("RUBiS has stored the following information about your item:<br>");
      sp.printHTML("Name         : "+name+"<br>");
      sp.printHTML("Description  : "+description+"<br>");
      sp.printHTML("Initial price: "+initialPrice+"<br>");
      sp.printHTML("ReservePrice : "+reservePrice+"<br>");
      sp.printHTML("Buy Now      : "+buyNow+"<br>");
      sp.printHTML("Quantity     : "+quantity+"<br>");
      sp.printHTML("User id      :"+userId+"<br>");
      sp.printHTML("Category id  :"+categoryId+"<br>");
      sp.printHTML("Duration     : "+duration+"<br>"); 
      sp.printHTML("<br>The following information has been automatically generated by RUBiS:<br>");
      sp.printHTML("Start date   :"+startDate+"<br>"); 
      sp.printHTML("End date     :"+endDate+"<br>"); 
      sp.printHTML("item id      :"+itemId+"<br>");
      
      conn.commit();
      sp.printHTMLfooter();
    } 
    catch (Exception e) 
    {
      sp.printHTML("Exception getting comment list: " + e +"<br>");
      try
      {
        conn.rollback();
      }
      catch (Exception se) 
      {
        sp.printHTML("Transaction rollback failed: " + e +"<br>");
      }
      closeConnection();
    }
  }
    
 
  /** 
   *	Call the doGet method: check the values from the html register item form 
   *	and create a new item 
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    doGet(request, response);
  }
}
