package edu.rice.rubis.servlets;

import edu.rice.rubis.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

/** This servlets display the page allowing a user to put a comment
 * on an item.
 * It must be called this way :
 * <pre>
 * http://..../PutComment?to=ww&itemId=xx&nickname=yy&password=zz
 *    where ww is the id of the user that will receive the comment
 *          xx is the item id
 *          yy is the nick name of the user
 *          zz is the user password
 * /<pre>
 */


public class PutComment extends RubisHttpServlet
{
  private ServletPrinter sp = null;
  private PreparedStatement stmt = null;
  private Connection conn = null;

  public int getPoolSize()
  {
    return Config.PutCommentPoolSize;
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
    sp.printHTMLheader("RUBiS ERROR: PutComment");
    sp.printHTML("<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    closeConnection();
  }


  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    String toStr = request.getParameter("to");
    String itemStr = request.getParameter("itemId");
    String name = request.getParameter("nickname");
    String pass = request.getParameter("password");
    sp = new ServletPrinter(response, "PubComment");
    
    if ((toStr == null) || (toStr.equals("")) ||
        (itemStr == null) || (itemStr.equals(""))||
        (name == null) || (name.equals(""))||
        (pass == null) || (pass.equals("")))
    {
      printError("User id, name and password are required - Cannot process the request<br>");
      return ;
    }

    // Authenticate the user who want to comment
    conn = getConnection();
    Auth auth = new Auth(conn, sp);
    int userId = auth.authenticate(name, pass);
    if (userId == -1)
    {
      printError("You don't have an account on RUBiS!<br>You have to register first.<br>");
      return ;	
    }

    // Try to find the user corresponding to the 'to' ID

    try
    {
      Integer toId = new Integer(toStr);
      Integer itemId = new Integer(itemStr);
      ResultSet urs, irs;
      String toName=null, itemName=null;
      try
      {
        stmt = conn.prepareStatement("SELECT nickname FROM users WHERE id=?");
        stmt.setInt(1, toId.intValue());
        urs = stmt.executeQuery();
        if (urs.first())
          toName = urs.getString("nickname");
      }
      catch (Exception e)
      {
        printError("Failed to execute Query for user: " +e);
        return;
      }
      try
      {
        stmt = conn.prepareStatement("SELECT name FROM items WHERE id=?");
        stmt.setInt(1, itemId.intValue());
        irs = stmt.executeQuery();
        if (irs.first())
          itemName = irs.getString("name");
      }
      catch (Exception e)
      {
        printError("Failed to execute Query for item: " +e);
        return;
      }

      // Display the form for comment
      sp.printHTMLheader("RUBiS: Comment service");
      sp.printHTML("<center><h2>Give feedback about your experience with "+toName+"</h2><br>");
      sp.printHTML("<form action=\"/servlet/edu.rice.rubis.servlets.StoreComment\" method=POST>"+
                   "<input type=hidden name=to value="+toStr+">"+
                   "<input type=hidden name=from value="+userId+">"+
                   "<input type=hidden name=itemId value="+itemId+">"+
                   "<center><table>"+
                   "<tr><td><b>From</b><td>"+name+
                   "<tr><td><b>To</b><td>"+toName+
                   "<tr><td><b>About item</b><td>"+itemName+
                   "<tr><td><b>Rating</b>"+
                   "<td><SELECT name=rating>"+
                   "<OPTION value=\"5\">Excellent</OPTION>"+
                   "<OPTION value=\"3\">Average</OPTION>"+
                   "<OPTION selected value=\"0\">Neutral</OPTION>"+
                   "<OPTION value=\"-3\">Below average</OPTION>"+
                   "<OPTION value=\"-5\">Bad</OPTION>"+
                   "</SELECT></table><p><br>"+
                   "<TEXTAREA rows=\"20\" cols=\"80\" name=\"comment\">Write your comment here</TEXTAREA><br><p>"+
                   "<input type=submit value=\"Post this comment now!\"></center><p>");
    }
    catch (Exception e)
    {
      printError("This item does not exist (got exception: " +e+")<br>");
      return ;
    }

    sp.printHTMLfooter();
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    doGet(request, response);
  }
}
