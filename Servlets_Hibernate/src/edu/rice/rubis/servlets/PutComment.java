package edu.rice.rubis.servlets;

import java.io.IOException;

import net.sf.hibernate.Session;
import net.sf.hibernate.HibernateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.rice.rubis.hibernate.Comment;
import edu.rice.rubis.hibernate.User;
import edu.rice.rubis.hibernate.Item;

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

public class PutComment extends HibernateRubisHttpServlet
{

  public int getPoolSize()
  {
    return Config.PutCommentPoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: PutComment");
    sp.printHTML(
      "<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
 
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    ServletPrinter sp = null;
    Session sess = null;
    User fromUser = null;
    User toUser = null;
    Item item = null;
    
    String toStr = request.getParameter("to");
    String itemStr = request.getParameter("itemId");
    String nick = request.getParameter("nickname");
    String pass = request.getParameter("password");
    sp = new ServletPrinter(response, "PutComment");
    
    if (toStr == null
      || toStr.equals("")
      || itemStr == null
      || itemStr.equals("")
      || nick == null
      || nick.equals("")
      || pass == null
      || pass.equals(""))
    {
      printError("User id, name and password are required - Cannot process the request<br>", sp);
      return;
    }
    
    // Authenticate the user who want to comment
    sess = getSession();
    Auth auth = new Auth(sess, sp);
    fromUser = auth.authenticate(nick, pass);
    if (fromUser == null)
    {
      printError("You don't have an account on RUBiS!<br>You have to register first.<br>", sp);
      releaseSession(sess);
      return;
    }
    
    // Try to find the user corresponding to the 'to' ID
    try
    {
      Integer toId = new Integer(toStr);
      Integer itemId = new Integer(itemStr);
      
      try
      {
        toUser = (User) sess.get(User.class, toId);
      }
      catch (HibernateException he)
      {
        printError("Failed to execute Query for user: " + he, sp);
        releaseSession(sess);
        return;
      }
      try
      {
        item = (Item) sess.get(Item.class, itemId);
      }
      catch (HibernateException he)
      {
        printError("Failed to execute Query for item: " + he, sp);
        releaseSession(sess);
        return;
      }
      
      if (toUser == null)
      {
        printError("<h2>This user does not exist!</h2>", sp);
        releaseSession(sess);
        return;
      }
      if (item == null)
      {
        printError("<h2>This item does not exist!</h2>", sp);
        releaseSession(sess);
        return;
      }
      
      // Display the form for comment
      sp.printHTMLheader("RUBiS: Comment service");
      sp.printHTML(
        "<center><h2>Give feedback about your experience with "
          + toUser.getNickname()
          + "</h2><br>");
      sp.printHTML(
        "<form action=\"/servlet/edu.rice.rubis.servlets.StoreComment\" method=POST>"
          + "<input type=hidden name=to value="
          + toUser.getId()
          + ">"
          + "<input type=hidden name=from value="
          + fromUser.getId()
          + ">"
          + "<input type=hidden name=itemId value="
          + item.getId()
          + ">"
          + "<center><table>"
          + "<tr><td><b>From</b><td>"
          + fromUser.getNickname()
          + "<tr><td><b>To</b><td>"
          + toUser.getNickname()
          + "<tr><td><b>About item</b><td>"
          + item.getName()
          + "<tr><td><b>Rating</b>"
          + "<td><SELECT name=rating>"
          + "<OPTION value=\"5\">Excellent</OPTION>"
          + "<OPTION value=\"3\">Average</OPTION>"
          + "<OPTION selected value=\"0\">Neutral</OPTION>"
          + "<OPTION value=\"-3\">Below average</OPTION>"
          + "<OPTION value=\"-5\">Bad</OPTION>"
          + "</SELECT></table><p><br>"
          + "<TEXTAREA rows=\"20\" cols=\"80\" name=\"comment\">Write your comment here</TEXTAREA><br><p>"
          + "<input type=submit value=\"Post this comment now!\"></center><p>");
    }
    catch (Exception e)
    {
      printError("This item does not exist (got exception: " + e + ")<br>", sp);
      releaseSession(sess);
      return;
    }
    
    releaseSession(sess);
    
    sp.printHTMLfooter();
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doGet(request, response);
  }

}
