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

/** This servlets displays the list of bids regarding an item.
 * It must be called this way :
 * <pre>
 * http://..../ViewUserInfo?itemId=xx where xx is the id of the item
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class ViewBidHistory extends HttpServlet
{
  private ServletPrinter sp = null;
  private Context initialContext = null;

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
   * Display the bid history of an item
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    String  value = request.getParameter("itemId");
    Integer itemId;
    
    sp = new ServletPrinter(response, "ViewBidHistory");

    if ((value == null) || (value.equals("")))
    {
      sp.printHTMLheader("RUBiS ERROR: View bids history");
      sp.printHTML("<h3>You must provide an item identifier !<br></h3>");
      sp.printHTMLfooter();
      return ;
    }
    else
      itemId = new Integer(value);

    sp.printHTMLheader("RUBiS: Bid history");

    try
    {
      initialContext = new InitialContext();
    } 
    catch (Exception e) 
    {
      sp.printHTML("Cannot get initial context for JNDI: " + e+"<br>");
      sp.printHTMLfooter();
      return ;
    }

    SB_ViewBidHistoryHome viewBidHome;
    SB_ViewBidHistory viewBid;
     try 
    {
      viewBidHome = (SB_ViewBidHistoryHome)PortableRemoteObject.narrow(initialContext.lookup("SB_ViewBidHistoryHome"),
                                                     SB_ViewBidHistoryHome.class);
     viewBid  = viewBidHome.create();
    } 
    catch (Exception e)
    {
      sp.printHTML("Cannot lookup SB_ViewBidHistory: " +e+"<br>");
      return ;
    }
     String html;
    try
    {
      html = viewBid.getBidHistory(itemId);
      sp.printHTML(html);

    }
    catch (Exception e)
    {
      sp.printHTML("Cannot get bids history (got exception: " +e+")<br>");
      sp.printHTMLfooter();
      return ;
    }
    sp.printHTMLfooter();
  }

}
