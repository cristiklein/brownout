package edu.rice.rubis.beans.servlets;

import edu.rice.rubis.beans.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Enumeration;

/** This servlets display the page allowing a user to put a comment
 * on an item.
 * It must be called this way :
 * <pre>
 * http://..../PutComment?to=ww&itemId=xx&nickname=yy&password=zz
 *    where ww is the id of the user that will receive the comment
 *          xx is the item id
 *          yy is the nick name of the user
 *          zz is the user password
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */


public class PutComment extends HttpServlet
{
  private ServletPrinter sp = null;

  private void printError(String errorMsg)
  {
    sp.printHTMLheader("RUBiS ERROR: PutComment");
    sp.printHTML("<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
  }


  /**
   * Describe <code>doGet</code> method here.
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
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

    Context initialContext = null;
    try
    {
      initialContext = new InitialContext();
    } 
    catch (Exception e) 
    {
      printError("Cannot get initial context for JNDI: " + e+"<br>");
      return ;
    }

    // Authenticate the user who want to comment
    Auth auth = new Auth(initialContext, sp);
    int userId = auth.authenticate(name, pass);
    if (userId == -1)
    {
      printError("You don't have an account on RUBiS!<br>You have to register first.<br>");
      return ;	
    }

    // Try to find the user corresponding to the 'to' ID
    UserHome uHome;
    ItemHome iHome;
    try 
    {
      uHome = (UserHome)PortableRemoteObject.narrow(initialContext.lookup("UserHome"),
                                                    UserHome.class);
      iHome = (ItemHome)PortableRemoteObject.narrow(initialContext.lookup("ItemHome"),
                                                    ItemHome.class);
    } 
    catch (Exception e)
    {
      printError("Cannot lookup User or Item: " +e+"<br>");
      return ;
    }

    try
    {
      Integer toId = new Integer(toStr);
      Integer itemId = new Integer(itemStr);
      User    to = uHome.findByPrimaryKey(new UserPK(toId));
      Item    item = iHome.findByPrimaryKey(new ItemPK(itemId));
      String  toName = to.getNickName();

      // Display the form for comment
      sp.printHTMLheader("RUBiS: Comment service");
      sp.printHTML("<center><h2>Give feedback about your experience with "+toName+"</h2><br>\n");
      sp.printHTML("<form action=\"/servlet/edu.rice.rubis.beans.servlets.StoreComment\" method=POST>\n"+
                   "<input type=hidden name=to value="+toStr+">\n"+
                   "<input type=hidden name=from value="+userId+">\n"+
                   "<input type=hidden name=itemId value="+itemId+">\n"+
                   "<center><table>\n"+
                   "<tr><td><b>From</b><td>"+name+"\n"+
                   "<tr><td><b>To</b><td>"+toName+"\n"+
                   "<tr><td><b>About item</b><td>"+item.getName()+"\n"+
                   "<tr><td><b>Rating</b>\n"+
                   "<td><SELECT name=rating>\n"+
                   "<OPTION value=\"5\">Excellent</OPTION>\n"+
                   "<OPTION value=\"3\">Average</OPTION>\n"+
                   "<OPTION selected value=\"0\">Neutral</OPTION>\n"+
                   "<OPTION value=\"-3\">Below average</OPTION>\n"+
                   "<OPTION value=\"-5\">Bad</OPTION>\n"+
                   "</SELECT></table><p><br>\n"+
                   "<TEXTAREA rows=\"20\" cols=\"80\" name=\"comment\">Write your comment here</TEXTAREA><br><p>\n"+
                   "<input type=submit value=\"Post this comment now!\"></center><p>\n");
    }
    catch (Exception e)
    {
      printError("This item does not exist (got exception: " +e+")<br>");
      return ;
    }

    sp.printHTMLfooter();
  }

  /**
   * Call the <code>doGet</code> method.
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    doGet(request, response);
  }
}
