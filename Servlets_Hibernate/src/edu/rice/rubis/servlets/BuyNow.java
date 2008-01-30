package edu.rice.rubis.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.hibernate.Session;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;

import edu.rice.rubis.hibernate.Item;
import edu.rice.rubis.hibernate.User;

/** This servlets display the page allowing a user to buy an item
 * It must be called this way :
 * <pre>
 * http://..../BuyNow?itemId=xx&nickname=yy&password=zz
 *    where xx is the id of the item
 *          yy is the nick name of the user
 *          zz is the user password
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class BuyNow extends HibernateRubisHttpServlet
{

  public int getPoolSize()
  {
    return Config.BuyNowPoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: Buy now");
    sp.printHTML(
      "<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
  }

  /**
   * Authenticate the user and end the display a buy now form
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    ServletPrinter sp = null;
    Session sess = null;
    User user = null;
    Item item = null;
    
    String itemStr = request.getParameter("itemId");
    String nick = request.getParameter("nickname");
    String pass = request.getParameter("password");
    sp = new ServletPrinter(response, "BuyNow");
    
    if ((itemStr == null)
      || (itemStr.equals(""))
      || (nick == null)
      || (nick.equals(""))
      || (pass == null)
      || (pass.equals("")))
    {
      printError("Item id, name and password are required - Cannot process the request<br>", sp);
      return;
    }
    
    // Authenticate the user who want to bid
    sess = getSession();
    Auth auth = new Auth(sess, sp);
    user = auth.authenticate(nick, pass);
    
    if (user == null)
    {
      sp.printHTML("nick: " + nick + "<br>");
      sp.printHTML("pass: " + pass + "<br>");
      printError(" You don't have an account on RUBiS!<br>You have to register first.<br>", sp);
      releaseSession(sess);
      return;
    }
    
    Integer itemId = new Integer(itemStr);
    // Try to find the Item corresponding to the Item ID
    try
    {
      item = (Item) sess.get(Item.class, itemId);
    }
    catch (HibernateException he)
    {
      printError("Exception getting item: " + he + "<br>", sp);
      releaseSession(sess);
      return;
    }
    if (item == null)
    {
      printError("This item does not exist in the database.", sp);
      releaseSession(sess);
      return;
    }
    
    // Display the form for buying the item
    sp.printItemDescriptionToBuyNow(item, user);
    
    releaseSession(sess);
    
    sp.printHTMLfooter();
  }

  /**
   * Call the doGet method
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doGet(request, response);
  }

  /**
   * Clean up the connection pool.
   */
  public void destroy()
  {
    super.destroy();
  }

}
