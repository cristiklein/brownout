package edu.rice.rubis.beans.servlets;

import edu.rice.rubis.beans.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.jms.*;

/**
 * Builds the html page with the list of all categories and provides links to browse all
 * items in a category or items in a category for a given region
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public class BrowseCategories extends HttpServlet
{
  private ServletPrinter sp = null;

  private void printError(String errorMsg)
  {
    sp.printHTMLheader("RUBiS ERROR: Browse Categories");
    sp.printHTML("<h3>Your request has not been processed due to the following error :</h3><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
  }

  /**
   * Build the html page for the response
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    String  region=null;
    String  username=null, password=null;
    Context initialContext = null;

    sp = new ServletPrinter(response, "BrowseCategories");
    sp.printHTMLheader("RUBiS available categories");
    sp.printHTML("<h2>Currently available categories</h2><br>");
    try
    {
      initialContext = new InitialContext();
    } 
    catch (Exception e) 
    {
      printError("Cannot get initial context for JNDI: " +e+"<br>");
      return ;
    }

    region = request.getParameter("region");
    username = request.getParameter("nickname");
    password = request.getParameter("password");
    
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
      topic = (Topic) initialContext.lookup("topic/topicBrowseCategories");
      // create a session
      session  = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE); // no transaction and auto ack
    } 
    catch (Exception e)
    {
      sp.printHTML("Cannot connect to message bean MDB_BrowseCategories : " +e+"<br>");
      return ;
    }
    try 
    {
      // create a requestor to receive the reply
      TopicRequestor requestor = new TopicRequestor(session, topic);
      System.out.println("create requestor");
      // create a message
      MapMessage message = session.createMapMessage();
      System.out.println("create message:"+message);
      // set parameters
      if (region != null)
        message.setString("region", region);
      System.out.println("set parameter 1");
      if (username != null)
        message.setString("nickname", username);
      System.out.println("set parameter 2");
      if (password != null)
        message.setString("password", password);
      System.out.println("set parameter 3");
      message.setJMSCorrelationID("category");
      System.out.println("set correlation id");
      // send the message and receive the reply
      connection.start(); // allows message to be delivered (default is connection stopped)
      System.out.println("start connection");
      TextMessage reply = (TextMessage)requestor.request(message);
      System.out.println("get reply");
      connection.stop();
      System.out.println("stop connection");
      // read the reply
      html = reply.getText();
      System.out.println("read reply");
      // close connection and session
      requestor.close(); // also close the session
      System.out.println("close requestor");
      connection.close();
      System.out.println("close connection");
    } 
    catch (Exception e)
    {
      sp.printHTML("Cannot get the list of categories: " +e+"<br>");
      return ;
    }
    sp.printHTML(html); 	
    sp.printHTMLfooter();
  }

  /**
   * Same as <code>doGet</code>.
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
