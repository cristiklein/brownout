package edu.rice.rubis.servlets;

import java.io.IOException;
import java.net.URLEncoder;

import java.util.List;
import java.util.Iterator;

import net.sf.hibernate.Session;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.rice.rubis.hibernate.Item;
import edu.rice.rubis.hibernate.Category;

/** This servlets displays a list of items belonging to a specific category.
 * It must be called this way :
 * <pre>
 * http://..../SearchItemsByCategory?category=xx&categoryName=yy 
 *    where xx is the category id
 *      and yy is the category name
 * /<pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class SearchItemsByCategory extends HibernateRubisHttpServlet
{


  public int getPoolSize()
  {
    return Config.SearchItemsByCategoryPoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: Search Items By Category");
    sp.printHTML(
      "<h2>We cannot process your request due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    
  }

  private void itemList(Category category, String categoryName, Integer page, Integer nbOfItems, Session sess, ServletPrinter sp)
  {
    List lst = null;
    
    // get the list of items
    try
    {
      /* causes initialization of 'category' */
      /*
      Query q = sess.createFilter(category.getItems(), "select this");
      */
      Query q = sess.createQuery("select item from Item item where item.category = :category and item.endDate >= NOW() order by item.endDate asc");
      q.setEntity("category", category);
      q.setFirstResult(page.intValue() * nbOfItems.intValue());
      q.setMaxResults(nbOfItems.intValue());
      lst = q.list();
    }
    catch (HibernateException he)
    {
      sp.printHTML("Failed to executeQuery for item: " + he);
      return;
    }
    
    try
    {
      Iterator it = lst.iterator();
      
      if (!it.hasNext())
      {
        if (page.intValue() == 0)
        {
          sp.printHTML("<h2>Sorry, but there are no items available in this category !</h2>");
        }
        else
        {
          sp.printHTML("<h2>Sorry, but there are no more items available in this category !</h2>");
          sp.printItemHeader();
          sp.printItemFooter(
            "<a href=\"/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
              + category.getId()
              + "&categoryName="
              + URLEncoder.encode(categoryName)
              + "&page="
              + (page.intValue() - 1)
              + "&nbOfItems="
              + nbOfItems
              + "\">Previous page</a>",
            "");
        }
        return;
      }
      
      sp.printItemHeader();
      
      do
      {
        Item item = (Item) it.next();
        
        sp.printItem(item);
      }
      while (it.hasNext());
      
      if (page.intValue() == 0)
      {
        sp.printItemFooter(
          "",
          "<a href=\"/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
            + category.getId()
            + "&categoryName="
            + URLEncoder.encode(categoryName)
            + "&page="
            + (page.intValue() + 1)
            + "&nbOfItems="
            + nbOfItems
            + "\">Next page</a>");
      }
      else
      {
        sp.printItemFooter(
          "<a href=\"/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
            + category.getId()
            + "&categoryName="
            + URLEncoder.encode(categoryName)
            + "&page="
            + (page.intValue() - 1)
            + "&nbOfItems="
            + nbOfItems
            + "\">Previous page</a>",
          "<a href=\"/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
            + category.getId()
            + "&categoryName="
            + URLEncoder.encode(categoryName)
            + "&page="
            + (page.intValue() + 1)
            + "&nbOfItems="
            + nbOfItems
            + "\">Next page</a>");
      }
    }
    catch (Exception e)
    {
      printError("Exception getting item list: " + e + "<br>", sp);
    }
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    Integer nbOfItems = null;
    Integer categoryId = null;
    Integer page = null;
    String categoryName = null;
    Category category = null;
    Session sess = null;
    ServletPrinter sp = null;
    
    sp = new ServletPrinter(response, "SearchItemsByCategory");
    
    String categoryStr = request.getParameter("category");
    if (categoryStr == null || categoryStr.equals(""))
    {
      printError("You must provide a category identifier!<br>", sp);
      return;
    }
    categoryId = new Integer(categoryStr);
    
    String pageStr = request.getParameter("page");
    if (pageStr == null || pageStr.equals(""))
      page = new Integer(0);
    else
      page = new Integer(pageStr);
    
    String nbOfItemsStr = request.getParameter("nbOfItems");
    if (nbOfItemsStr == null || nbOfItemsStr.equals(""))
      nbOfItems = new Integer(25);
    else
      nbOfItems = new Integer(nbOfItemsStr);
    
    sess = getSession();
    
    /*
    try
    {
      category = (Category) sess.get(Category.class, categoryId);
    }
    catch (HibernateException he)
    {
      printError("Exception getting category: " + he + "<br>", sp);
      releaseSession(sess);
      return;
    }
    if (category == null)
    {
      printError("This category does not exist in the database.", sp);
      releaseSession(sess);
      return;
    }
    */
    
    try
    {
      category = (Category) sess.load(Category.class, categoryId);
    }
    catch (HibernateException he)
    {
      printError("Exception getting category: " + he + "<br>", sp);
      releaseSession(sess);
      return;
    }
    
    categoryName = request.getParameter("categoryName");
    if (categoryName == null)
    {
      sp.printHTMLheader("RUBiS: Missing category name");
      sp.printHTML("<h2>Items in this category</h2><br><br>");
    }
    else
    {
      sp.printHTMLheader("RUBiS: Items in category " + categoryName);
      sp.printHTML("<h2>Items in category " + categoryName + "</h2><br><br>");
    }
    
    itemList(category, categoryName, page, nbOfItems, sess, sp);
    
    releaseSession(sess);
    
    sp.printHTMLfooter();
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doGet(request, response);
  }

}
