package edu.rice.rubis.servlets;

import java.io.IOException;

import java.util.Date;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.rice.rubis.hibernate.Comment;
import edu.rice.rubis.hibernate.User;
import edu.rice.rubis.hibernate.Item;

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
 * /<pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class StoreComment extends HibernateRubisHttpServlet
{

  public int getPoolSize()
  {
    return Config.StoreCommentPoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: StoreComment");
    sp.printHTML(
      "<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
   
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doPost(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    Integer toId; // to user id
    Integer fromId; // from user id
    Integer itemId; // item id
    String comment; // user comment
    Integer rating; // user rating
    ServletPrinter sp = null;
    Session sess = null;
    Transaction trans = null;
    
    sp = new ServletPrinter(response, "StoreComment");
    
    /* Get and check all parameters */
    String toStr = request.getParameter("to");
    if (toStr == null || toStr.equals(""))
    {
      printError("<h3>You must provide a 'to user' identifier !<br></h3>", sp);
      return;
    }
    toId = new Integer(toStr);
    
    String fromStr = request.getParameter("from");
    if (fromStr == null || fromStr.equals(""))
    {
      printError("<h3>You must provide a 'from user' identifier !<br></h3>", sp);
      return;
    }
    fromId = new Integer(fromStr);
    
    String itemStr = request.getParameter("itemId");
    if (itemStr == null || itemStr.equals(""))
    {
      printError("<h3>You must provide an item identifier !<br></h3>", sp);
      return;
    }
    itemId = new Integer(itemStr);
    
    String ratingStr = request.getParameter("rating");
    if (ratingStr == null || ratingStr.equals(""))
    {
      printError("<h3>You must provide a rating !<br></h3>", sp);
      return;
    }
    rating = new Integer(ratingStr);
    
    comment = request.getParameter("comment");
    if (comment == null || comment.equals(""))
    {
      printError("<h3>You must provide a comment !<br></h3>", sp);
      return;
    }
    
    try
    {
      sess = getSession();
      trans = sess.beginTransaction();
      
      User toUser = (User) sess.load(User.class, toId);
      User fromUser = (User) sess.load(User.class, fromId);
      Item item = (Item) sess.load(Item.class, itemId);
      
      /*
      if (toUser == null)
      {
        try
        {
          trans.rollback();
        }
        catch (HibernateException he)
        {
          printError("Transaction rollback failed: " + he + "<br>", sp);
        }
        printError("This user does not exist in the database.", sp);
        releaseSession(sess);
        return;
      }
      if (fromUser == null)
      {
        try
        {
          trans.rollback();
        }
        catch (HibernateException he)
        {
          printError("Transaction rollback failed: " + he + "<br>", sp);
        }
        printError("This user does not exist in the database.", sp);
        releaseSession(sess);
        return;
      }
      if (item == null)
      {
        try
        {
          trans.rollback();
        }
        catch (HibernateException he)
        {
          printError("Transaction rollback failed: " + he + "<br>", sp);
        }
        printError("This item does not exist in the database.", sp);
        releaseSession(sess);
        return;
      }
      */
      
      Date now = new Date();
      
      // Try to create a new comment
      Comment newComment = new Comment();
      newComment.setFromUser(fromUser);
      newComment.setToUser(toUser);
      newComment.setItem(item);
      newComment.setRating(rating);
      newComment.setDate(now);
      newComment.setComment(comment);
      
      sess.save(newComment);
      
      if (Hibernate.isInitialized(toUser) && Hibernate.isInitialized(toUser.getCommentsTo()))
      {
        toUser.getCommentsTo().add(newComment);
      }
      if (Hibernate.isInitialized(fromUser) && Hibernate.isInitialized(fromUser.getCommentsFrom()))
      {
        fromUser.getCommentsFrom().add(newComment);
      }
      if (Hibernate.isInitialized(item) && Hibernate.isInitialized(item.getComments()))
      {
        item.getComments().add(newComment);
      }
      
      toUser.setRating(new Integer(toUser.getRating().intValue() + rating.intValue()));
      
      sp.printHTMLheader("RUBiS: Comment posting");
      sp.printHTML("<center><h2>Your comment has been successfully posted.</h2></center>");
      
      trans.commit();
      releaseSession(sess);
    }
    catch (Exception e)
    {
      printError("Error adding new comment: " + e + ")<br>", sp);
      try
      {
        trans.rollback();
      }
      catch (HibernateException he)
      {
        sp.printHTML("Transaction rollback failed: " + he + "<br>");
      }
      releaseSession(sess);
      return;
    }
    
    sp.printHTMLfooter();
  }

}
