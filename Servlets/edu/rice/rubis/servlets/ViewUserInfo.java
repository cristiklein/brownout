package edu.rice.rubis.servlets;

import edu.rice.rubis.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

/** This servlets displays general information about a user.
 * It must be called this way :
 * <pre>
 * http://..../ViewUserInfo?userId=xx where xx is the id of the user
 * /<pre>
 */

public class ViewUserInfo extends RubisHttpServlet
{
  private ServletPrinter sp = null;
  private PreparedStatement stmt = null;
  private Connection conn = null;

  public int getPoolSize()
  {
    return Config.ViewUserInfoPoolSize;
  }

  private void closeConnection()
  {
    try 
    {
      if (stmt != null) stmt.close();	// close statement
//      if (conn != null) conn.close();	// release connection
    } 
    catch (Exception ignore) 
    {
    }
  }


  private void commentList(Integer userId) 
  {
    ResultSet rs = null;
    String date, comment;
    int authorId;
      
    try 
    {
      conn.setAutoCommit(false);	// faster if made inside a Tx

      // Try to find the comment corresponding to the user
      try
      {
        stmt = conn.prepareStatement("SELECT * FROM comments WHERE to_user_id=?");
        stmt.setInt(1, userId.intValue());
        rs = stmt.executeQuery();
      }
      catch (Exception e)
      {
        sp.printHTML("Failed to execute Query for list of comments: " +e);
        closeConnection();
        return;
      }
      if (!rs.first()) 
      {
        sp.printHTML("<h3>There is no comment yet for this user.</h3><br>");
        return;
      }
      else
        sp.printHTML("<br><hr><br><h3>Comments for this user</h3><br>");

      sp.printCommentHeader();
      // Display each comment and the name of its author
      do 
      {
        comment = rs.getString("comment");
        date = rs.getString("date");
        authorId = rs.getInt("from_user_id");

        String authorName = "none";
	ResultSet authorRS = null;
	try
	{
          stmt = conn.prepareStatement("SELECT nickname FROM users WHERE id=?");
          stmt.setInt(1, authorId);
          authorRS = stmt.executeQuery();
          if (authorRS.first())
            authorName = authorRS.getString("nickname");
	}
	catch (Exception e)
	{
          sp.printHTML("Failed to execute Query for the comment author: " +e);
          closeConnection();
          return;
	}
        sp.printComment(authorName, authorId, date, comment);
      }
      while (rs.next());
      sp.printCommentFooter();
      conn.commit();
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


  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    doPost(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    String  value = request.getParameter("userId");
    Integer userId;
    ResultSet rs = null;

    sp = new ServletPrinter(response, "ViewUserInfo");

    if ((value == null) || (value.equals("")))
    {
      sp.printHTMLheader("RUBiS ERROR: View user information");
      sp.printHTML("<h3>You must provide a user identifier !<br></h3>");
      closeConnection();
      sp.printHTMLfooter();
      return ;
    }
    else
      userId = new Integer(value);

    sp.printHTMLheader("RUBiS: View user information");

    // Try to find the user corresponding to the userId
    try
    {
      conn = getConnection();
      stmt = conn.prepareStatement("SELECT * FROM users WHERE id=?");
      stmt.setInt(1, userId.intValue());
      rs = stmt.executeQuery();
    }
    catch (Exception e)
    {
      sp.printHTML("Failed to execute Query for user: " +e);
      closeConnection();
      sp.printHTMLfooter();
      return;
    }
    try 
    {
      if (!rs.first())
      {
        sp.printHTML("<h2>This user does not exist!</h2>");		    
        closeConnection();
        sp.printHTMLfooter();
        return;
      }
      String firstname = rs.getString("firstname");
      String lastname = rs.getString("lastname");
      String nickname = rs.getString("nickname");
      String email = rs.getString("email");
      String date = rs.getString("creation_date");
      int rating = rs.getInt("rating");

      String result = new String();

      result = result+"<h2>Information about "+nickname+"<br></h2>";
      result = result+"Real life name : "+firstname+" "+lastname+"<br>";
      result = result+"Email address  : "+email+"<br>";
      result = result+"User since     : "+date+"<br>";
      result = result+"Current rating : <b>"+rating+"</b><br>";
      sp.printHTML(result);

    }
    catch (SQLException s)
    {
      sp.printHTML("Failed to get general information about the user: " +s);
      closeConnection();
      sp.printHTMLfooter();
      return;
    }
    commentList(userId);
    sp.printHTMLfooter();
  }

}
