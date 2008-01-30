package edu.rice.rubis.servlets;

import java.io.IOException;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.rice.rubis.hibernate.Item;
import edu.rice.rubis.hibernate.User;
import edu.rice.rubis.hibernate.Category;

/** 
 * Add a new item in the database 
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public class RegisterItem extends HibernateRubisHttpServlet
{
  
  public int getPoolSize()
  {
    return Config.RegisterItemPoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: Register user");
    sp.printHTML(
      "<h2>Your registration has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    
  }

  /** Check the values from the html register item form and create a new item */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    String name = null, description = null;
    Float initialPrice = null, buyNow = null, reservePrice = null;
    Integer quantity = null, duration = null;
    Integer categoryId = null, userId = null;
    Date startDate = null, endDate = null;
    Session sess = null;
    Transaction trans = null;
    
    ServletPrinter sp = null;
    sp = new ServletPrinter(response, "RegisterItem");
    
    name = request.getParameter("name");
    if (name == null || name.equals(""))
    {
      printError("You must provide a name!<br>", sp);
      return;
    }
    
    description = request.getParameter("description");
    if (description == null || description.equals(""))
    {
      description = "No description.";
    }
    
    String initialPriceStr = request.getParameter("initialPrice");
    if (initialPriceStr == null || initialPriceStr.equals(""))
    {
      printError("You must provide an initial price!<br>", sp);
      return;
    }
    initialPrice = new Float(initialPriceStr);
    
    String reservePriceStr = request.getParameter("reservePrice");
    if (reservePriceStr == null || reservePriceStr.equals(""))
    {
      reservePrice = new Float(0);
    }
    reservePrice = new Float(reservePriceStr);
    
    String buyNowStr = request.getParameter("buyNow");
    if (buyNowStr == null || buyNowStr.equals(""))
    {
      buyNow = new Float(0);
    }
    buyNow = new Float(buyNowStr);
    
    String durationStr = request.getParameter("duration");
    if (durationStr == null || durationStr.equals(""))
    {
      printError("You must provide a duration!<br>", sp);
      return;
    }
    duration = new Integer(durationStr);
    GregorianCalendar cal;
    cal = new GregorianCalendar();
    startDate = cal.getTime();
    cal.add(Calendar.DATE, duration.intValue());
    endDate = cal.getTime();
    
    String quantityStr = request.getParameter("quantity");
    if (quantityStr == null || quantityStr.equals(""))
    {
      printError("You must provide a quantity!<br>", sp);
      return;
    }
    quantity = new Integer(quantityStr);
    
    String userStr = request.getParameter("userId");
    if (userStr == null || userStr.equals(""))
    {
      printError("You must provide a user identifier!<br>", sp);
      return;
    }
    userId = new Integer(userStr);
    
    String categoryStr = request.getParameter("categoryId");
    if (categoryStr == null || categoryStr.equals(""))
    {
      printError("You must provide a category identifier!<br>", sp);
      return;
    }
    categoryId = new Integer(categoryStr);
    
    try
    {
      sess = getSession();
      trans = sess.beginTransaction();
      
      User user = (User) sess.load(User.class, userId);
      Category category = (Category) sess.load(Category.class, categoryId);
      
      /*
      if (user == null)
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
      if (category == null)
      {
        try
        {
          trans.rollback();
        }
        catch (HibernateException he)
        {
          printError("Transaction rollback failed: " + he + "<br>", sp);
        }
        printError("This category does not exist in the database.", sp);
        releaseSession(sess);
        return;
      }
      */
      
      // Try to create a new item
      Item newItem = new Item();
      newItem.setName(name);
      newItem.setDescription(description);
      newItem.setInitialPrice(initialPrice);
      newItem.setQuantity(quantity);
      newItem.setReservePrice(reservePrice);
      newItem.setBuyNow(buyNow);
      newItem.setNbOfBids(new Integer(0));
      newItem.setMaxBid(new Float(0));
      newItem.setStartDate(startDate);
      newItem.setEndDate(endDate);
      newItem.setSeller(user);
      newItem.setCategory(category);
      /*
      newItem.setBids(new HashSet());
      newItem.setBuys(new HashSet());
      newItem.setComments(new HashSet());
      */
      
      sess.save(newItem);
      
      if (Hibernate.isInitialized(user) && Hibernate.isInitialized(user.getItems()))
      {
        user.getItems().add(newItem);
      }
      if (Hibernate.isInitialized(category) && Hibernate.isInitialized(category.getItems()))
      {
        category.getItems().add(newItem);
      }
      
      sp.printHTMLheader("RUBiS: Item to sell " + newItem.getName());
      sp.printHTML("<h2>Your Item has been successfully registered.</h2><br>");
      sp.printHTML("RUBiS has stored the following information about your item:<br>");
      sp.printHTML("Name         : " + newItem.getName() + "<br>");
      sp.printHTML("Description  : " + newItem.getDescription() + "<br>");
      sp.printHTML("Initial price: " + newItem.getInitialPrice() + "<br>");
      sp.printHTML("ReservePrice : " + newItem.getReservePrice() + "<br>");
      sp.printHTML("Buy Now      : " + newItem.getBuyNow() + "<br>");
      sp.printHTML("Quantity     : " + newItem.getQuantity() + "<br>");
      sp.printHTML("User id      :" + newItem.getSeller().getId() + "<br>");
      sp.printHTML("Category id  :" + newItem.getCategory().getId() + "<br>");
      sp.printHTML("Duration     : " + duration + "<br>");
      sp.printHTML("<br>The following information has been automatically generated by RUBiS:<br>");
      sp.printHTML("Start date   :" + newItem.getStartDate() + "<br>");
      sp.printHTML("End date     :" + newItem.getEndDate() + "<br>");
      sp.printHTML("item id      :" + newItem.getId() + "<br>");
      
      trans.commit();
      releaseSession(sess);
    }
    catch (Exception e)
    {
      sp.printHTML("Exception registering item: " + e + "<br>");
      try
      {
        trans.rollback();
      }
      catch (HibernateException he)
      {
        sp.printHTML("Transaction rollback failed: " + he + "<br>");
      }
      releaseSession(sess);
    }
    
    sp.printHTMLfooter();
  }

  /** 
   *	Call the doGet method: check the values from the html register item form 
   *	and create a new item 
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doGet(request, response);
  }

}
