package edu.rice.rubis.beans.servlets;

import edu.rice.rubis.beans.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.transaction.UserTransaction;
import java.util.Enumeration;

/** This servlets records a comment in the database and display
 * the result of the transaction.
 * It must be called this way :
 * <pre>
 * http://..../StoreComment?itemId=aa&userId=bb&minComment=cc&maxQty=dd&comment=ee&maxComment=ff&qty=gg 
 *   where: aa is the item id 
 *          bb is the user id
 *          cc is the minimum acceptable comment for this item
 *          dd is the maximum quantity available for this item
 *          ee is the user comment
 *          ff is the maximum comment the user wants
 *          gg is the quantity asked by the user
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class StoreComment extends HttpServlet
{
  private ServletPrinter sp = null;
  private Context initialContext = null;


  private void printError(String errorMsg)
  {
    sp.printHTMLheader("RUBiS ERROR: StoreComment");
    sp.printHTML("<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
  }


  /**
   * Call the <code>doPost</code> method.
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    doPost(request, response);
  }

  /**
   * Store the comment to the database and display the resulting message.
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    Integer toId;    // to user id
    Integer fromId;  // from user id
    Integer itemId;  // item id
    String  comment; // user comment
    Integer rating;  // user rating

    sp = new ServletPrinter(response, "StoreComment");

    /* Get and check all parameters */

    String value = request.getParameter("to");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide a 'to user' identifier !<br></h3>");
      return ;
    }
    else
      toId = new Integer(value);

    value = request.getParameter("from");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide a 'from user' identifier !<br></h3>");
      return ;
    }
    else
      fromId = new Integer(value);

    value = request.getParameter("itemId");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide an item identifier !<br></h3>");
      return ;
    }
    else
      itemId = new Integer(value);

    value = request.getParameter("rating");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide a rating !<br></h3>");
      return ;
    }
    else
      rating = new Integer(value);

    comment = request.getParameter("comment");
    if ((comment == null) || (comment.equals("")))
    {
      printError("<h3>You must provide a comment !<br></h3>");
      return ;
    }
    try
    {
      initialContext = new InitialContext();
    } 
    catch (Exception e) 
    {
      printError("Cannot get initial context for JNDI: " + e+"<br>");
      return ;
    }
    // Try to find the user corresponding to the 'to' ID
    User to;
    try 
    {
      UserHome uHome = (UserHome)PortableRemoteObject.narrow(initialContext.lookup("UserHome"),
                                                             UserHome.class);
      to = uHome.findByPrimaryKey(new UserPK(toId));
    } 
    catch (Exception e)
    {
      printError("Cannot lookup User or Item: " +e+"<br>");
      return ;
    }
    CommentHome cHome;
    try 
    {
      cHome = (CommentHome)PortableRemoteObject.narrow(initialContext.lookup("CommentHome"),
                                                       CommentHome.class);
    } 
    catch (Exception e)
    {
      printError("Cannot lookup Comment: " +e+"<br>");
      return ;
    }
    // We want to start transactions from client
    UserTransaction utx = null;
    try
    {
      utx = (javax.transaction.UserTransaction)initialContext.lookup(Config.UserTransaction);
      utx.begin();	
    } 
    catch (Exception e)
    {
      printError("Cannot lookup UserTransaction: "+e+"<br>");
      return ;
    }
    try
    {
      Comment c = cHome.create(fromId, toId, itemId, rating.intValue(), comment);
      to.updateRating(rating.intValue());
      utx.commit();
      sp.printHTMLheader("RUBiS: Comment posting");
      sp.printHTML("<center><h2>Your comment has been successfully posted.</h2></center>\n");
    }
    catch (Exception e)
    {
      printError("Error while storing the comment (got exception: " +e+")<br>");
      try
      {
        utx.rollback();
      }
      catch (Exception se) 
      {
        printError("Transaction rollback failed: " + e +"<br>");
      }
      return ;
    }
		
    sp.printHTMLfooter();
  }

}
