package edu.rice.rubis.servlets;

import edu.rice.rubis.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.transaction.UserTransaction;
import java.sql.*;

/** 
 * Add a new user in the database 
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public class RegisterUser extends RubisHttpServlet
{
  private UserTransaction utx = null;
  private ServletPrinter  sp = null;
  private PreparedStatement stmt = null;
  private Connection conn = null;

  public int getPoolSize()
  {
    return Config.RegisterUserPoolSize;
  }


  private void printError(String errorMsg)
  {
    sp.printHTMLheader("RUBiS ERROR: Register user");
    sp.printHTML("<h2>Your registration has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    try 
    {
      if (stmt != null) stmt.close();	// close statement
    } 
    catch (Exception ignore) 
    {
    }
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    String firstname=null, lastname=null, nickname=null, email=null, password=null;
    int    regionId;
    int    userId;
    String creationDate, region;

    sp = new ServletPrinter(response, "RegisterUser");
      
    String value = request.getParameter("firstname");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a first name!<br>");
      return ;
    }
    else
      firstname = value;

    value = request.getParameter("lastname");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a last name!<br>");
      return ;
    }
    else
      lastname = value;

    value = request.getParameter("nickname");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a nick name!<br>");
      return ;
    }
    else
      nickname = value;

    value = request.getParameter("email");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide an email address!<br>");
      return ;
    }
    else
      email = value;

    value = request.getParameter("password");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a password!<br>");
      return ;
    }
    else
      password = value;


    value = request.getParameter("region");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a valid region!<br>");
      return ;
    }
    else
    {
      region = value;
      try
      {
        conn = getConnection();
        stmt = conn.prepareStatement("SELECT id FROM regions WHERE name=?");
        stmt.setString(1, region);
        ResultSet rs = stmt.executeQuery();
        if (!rs.first())
        {
          printError(" Region "+value+" does not exist in the database!<br>");
          return ;
        }
        regionId = rs.getInt("id");
      }
      catch (SQLException e)
      {
        printError("Failed to execute Query for region: " +e);
        return;
      }
    }
    // Try to create a new user
    try
    {
      stmt = conn.prepareStatement("SELECT nickname FROM users WHERE nickname=?");
      stmt.setString(1, nickname);
      ResultSet rs = stmt.executeQuery();
      if (rs.first())
      {
        printError("The nickname you have choosen is already taken by someone else. Please choose a new nickname.<br>");
        return ;
      }
    }
    catch (SQLException e)
    {
      printError("Failed to execute Query to check the nickname: " +e);
      return;
    }
    try 
    {
      String now = TimeManagement.currentDateToString();
      stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, \""+firstname+
                                   "\", \""+lastname+"\", \""+nickname+"\", \""+
                                   password+"\", \""+email+"\", 0, 0,\""+now+"\", "+ 
                                   regionId+")");
      stmt.executeUpdate();
    }
    catch (SQLException e)
    {
      printError("RUBiS internal error: User registration failed (got exception: " +e+")<br>");
      return;
    }
    try
    {
      stmt = conn.prepareStatement("SELECT id, creation_date FROM users WHERE nickname=?");
      stmt.setString(1, nickname);
      ResultSet urs = stmt.executeQuery();
      if (!urs.first())
      {
        printError("This user does not exist in the database.");
        return;
      }
      userId = urs.getInt("id");
      creationDate = urs.getString("creation_date");
    }
    catch (SQLException e)
    {
      printError("Failed to execute Query for user: " +e);
      return;
    }

    sp.printHTMLheader("RUBiS: Welcome to "+nickname);
    sp.printHTML("<h2>Your registration has been processed successfully</h2><br>");
    sp.printHTML("<h3>Welcome "+nickname+"</h3>");
    sp.printHTML("RUBiS has stored the following information about you:<br>");
    sp.printHTML("First Name : "+firstname+"<br>");
    sp.printHTML("Last Name  : "+lastname+"<br>");
    sp.printHTML("Nick Name  : "+nickname+"<br>");
    sp.printHTML("Email      : "+email+"<br>");
    sp.printHTML("Password   : "+password+"<br>");
    sp.printHTML("Region     : "+region+"<br>"); 
    sp.printHTML("<br>The following information has been automatically generated by RUBiS:<br>");
    sp.printHTML("User id       :"+userId+"<br>");
    sp.printHTML("Creation date :"+creationDate+"<br>");
      
    sp.printHTMLfooter();
  }
    
 
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    doGet(request, response);
  }
}
