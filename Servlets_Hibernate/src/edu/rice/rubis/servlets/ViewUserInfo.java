package edu.rice.rubis.servlets;

import java.io.IOException;

import java.util.List;
import java.util.Iterator;

import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.rice.rubis.hibernate.User;
import edu.rice.rubis.hibernate.Comment;

/** This servlets displays general information about a user.
 * It must be called this way :
 * <pre>
 * http://..../ViewUserInfo?userId=xx where xx is the id of the user
 * /<pre>
 */

public class ViewUserInfo extends HibernateRubisHttpServlet
{


  public int getPoolSize()
  {
    return Config.ViewUserInfoPoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: View user info");
    sp.printHTML("<h2>We cannot process your request due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    
  }

  private void commentList(User user, Session sess, ServletPrinter sp)
  {
    Transaction trans = null;
    List lst = null;
    
    try
    {
      trans = sess.beginTransaction();
      Query q = sess.createFilter(user.getCommentsTo(), "select this");
      lst = q.list();
    }
    catch (Exception e)
    {
      sp.printHTML("Failed to execute Query for list of comments: " + e);
      try
      {
        trans.rollback();
      }
      catch (HibernateException he)
      {
        printError("Transaction rollback failed: " + he, sp);
      }
      return;
    }
    
    try
    {
      Iterator it = lst.iterator();
      
      if (!it.hasNext())
      {
        sp.printHTML("<h3>There is no comment yet for this user.</h3><br>");
        try
        {
          trans.commit();
        }
        catch (HibernateException he)
        {
          printError("Transaction commit failed: " + he, sp);
        }
        releaseSession(sess);
        return;
      }
      
      sp.printHTML("<br><hr><br><h3>Comments for this user</h3><br>");
      
      sp.printCommentHeader();
      // Display each comment and the name of its author
      do
      {
        Comment comment = (Comment) it.next();
        
        sp.printComment(comment);
      }
      while (it.hasNext());
      
      sp.printCommentFooter();
      
      try
      {
        trans.commit();
      }
      catch (HibernateException he)
      {
        printError("Transaction commit failed: " + he, sp);
      }
    }
    catch (Exception e)
    {
      sp.printHTML("Exception getting comment list: " + e + "<br>");
      try
      {
        trans.rollback();
      }
      catch (HibernateException he)
      {
        sp.printHTML("Transaction rollback failed: " + he + "<br>");
      }
    }
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doPost(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    Integer userId = null;
    User user = null;
    Session sess = null;
    ServletPrinter sp = null;
    
    sp = new ServletPrinter(response, "ViewUserInfo");
    
    String userStr = request.getParameter("userId");
    if (userStr == null || userStr.equals(""))
    {
      sp.printHTMLheader("RUBiS ERROR: View user information");
      sp.printHTML("<h3>You must provide a user identifier !<br></h3>");
      sp.printHTMLfooter();
      return;
    }
    userId = new Integer(userStr);
    
    sp.printHTMLheader("RUBiS: View user information");
    
    // Try to find the user corresponding to the userId
    sess = getSession();
    
    try
    {
      user = (User) sess.get(User.class, userId);
    }
    catch (HibernateException he)
    {
      printError("Exception getting user: " + he + "<br>", sp);
      releaseSession(sess);
      return;
    }
    if (user == null)
    {
      printError("This user does not exist in the database.", sp);
      releaseSession(sess);
      return;
    }
    
    String result = new String();
    
    result = result + "<h2>Information about " + user.getNickname() + "<br></h2>";
    result = result + "Real life name : " + user.getFirstname() + " " + user.getLastname() + "<br>";
    result = result + "Email address  : " + user.getEmail() + "<br>";
    result = result + "User since     : " + user.getCreationDate() + "<br>";
    result = result + "Current rating : <b>" + user.getRating() + "</b><br>";
    sp.printHTML(result);
    
    commentList(user, sess, sp);
    
    releaseSession(sess);
    
    sp.printHTMLfooter();
  }
}
