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
import javax.jms.*;

/**
 * This servlets displays general information about the user loged in
 * and about his current bids or items to sell.
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public class AboutMe extends HttpServlet
{
  private ServletPrinter sp = null;
  private Context initialContext = null;


  private void printError(String errorMsg)
  {
    sp.printHTMLheader("RUBiS ERROR: About me");
    sp.printHTML("<h3>Your request has not been processed due to the following error :</h3><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
  }


  /**
   * Call <code>doPost</code> method.
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
   * Check username and password and build the web page that display the information about
   * the loged in user.
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    String  password=null, username=null; 
    Integer userId=null;
    
    sp = new ServletPrinter(response, "About me");

    username = request.getParameter("nickname");
    password = request.getParameter("password");    
    // Authenticate the user
    if ((username != null && !username.equals("")) || (password != null && !password.equals("")))
    {
      try
      {
        initialContext = new InitialContext();
      } 
      catch (Exception e) 
      {
        printError("Cannot get initial context for JNDI: " + e+"<br>");
        return ;
      }
     }
    else
    {
      printError("You must provide valid username and password.");
      return ;
    }
    sp.printHTMLheader("RUBiS: About Me");

    TopicConnectionFactory topicFactory = null;
    TopicConnection connection = null;
    TopicSession session  = null;
    Topic topic = null;
    String html;
    try 
    {
      // lookup the connection factory
      topicFactory = (TopicConnectionFactory)initialContext.lookup(Config.TopicConnectionFactoryName);
      // create a connection to the JMS provider
      connection = topicFactory.createTopicConnection();
      // lookup the destination
      topic = (Topic) initialContext.lookup(Config.PrefixTopicName+"topicAboutMe");
      // create a session
      session  = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE); // no transaction and auto ack
    } 
    catch (Exception e)
    {
      sp.printHTML("Cannot connect to message bean MDB_AboutMe : " +e+"<br>");
      return ;
    }
    try 
    {
      // create a requestor to receive the reply
      TopicRequestor requestor = new TopicRequestor(session, topic);
      // create a message
      MapMessage message = session.createMapMessage();
      // set parameters
      message.setString("username", username);
      message.setString("password", password);
      message.setJMSCorrelationID("aboutMe");
      // send the message and receive the reply
      connection.start(); // allows message to be delivered (default is connection stopped)
      TextMessage reply = (TextMessage)requestor.request(message);
      connection.stop();
      // read the reply
      html = reply.getText();
      // close connection and session
      requestor.close(); // also close the session
      connection.close();
    } 
    catch (Exception e)
    {
      sp.printHTML("Cannot retrieve information about you: " +e+"<br>");
      return ;
    }
    sp.printHTML(html);
    sp.printHTMLfooter();
  }

}
