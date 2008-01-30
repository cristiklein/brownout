package edu.rice.rubis.servlets;

import java.io.IOException;

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
import edu.rice.rubis.hibernate.Region;

/**
 * Build the html page with the list of all items for given category and region.
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public class SearchItemsByRegion extends HibernateRubisHttpServlet
{

  public int getPoolSize()
  {
    return Config.SearchItemsByRegionPoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: SearchItemsByRegion");
    sp.printHTML(
      "<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    
  }

  /** List items in the given category for the given region */
  private void itemList(Category category, Region region, Integer page, Integer nbOfItems, Session sess, ServletPrinter sp)
  {
    List lst = null;
    
    // get the list of items
    try
    {
      /* causes initialization of 'category' */
      /*
      Query q = sess.createFilter(category.getItems(), "select this where this.seller.region = :region");
      */
      Query q = sess.createQuery("select item from Item item where item.category = :category and item.seller.region = :region and item.endDate >= NOW() order by item.endDate asc");
      q.setEntity("category", category);
      q.setEntity("region", region);
      q.setFirstResult(page.intValue() * nbOfItems.intValue());
      q.setMaxResults(nbOfItems.intValue());
      lst = q.list();
    }
    catch (HibernateException he)
    {
      sp.printHTML("Failed to execute Query for items in region: " + he);
      return;
    }
    try
    {
      Iterator it = lst.iterator();
      
      if (!it.hasNext())
      {
        if (page.intValue() == 0)
        {
          sp.printHTML("<h3>Sorry, but there is no items in this category for this region.</h3><br>");
        }
        else
        {
          sp.printHTML("<h3>Sorry, but there is no more items in this category for this region.</h3><br>");
          sp.printItemHeader();
          sp.printItemFooter(
            "<a href=\"/servlet/edu.rice.rubis.servlets.SearchItemsByRegion?category="
              + category.getId()
              + "&region="
              + region.getId()
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
          "<a href=\"/servlet/edu.rice.rubis.servlets.SearchItemsByRegion?category="
            + category.getId()
            + "&region="
            + region.getId()
            + "&page="
            + (page.intValue() + 1)
            + "&nbOfItems="
            + nbOfItems
            + "\">Next page</a>");
      }
      else
      {
        sp.printItemFooter(
          "<a href=\"/servlet/edu.rice.rubis.servlets.SearchItemsByRegion?category="
            + category.getId()
            + "&region="
            + region.getId()
            + "&page="
            + (page.intValue() - 1)
            + "&nbOfItems="
            + nbOfItems
            + "\">Previous page</a>",
          "<a href=\"/servlet/edu.rice.rubis.servlets.SearchItemsByRegion?category="
            + category.getId()
            + "&region="
            + region.getId()
            + "&page="
            + (page.intValue() + 1)
            + "&nbOfItems="
            + nbOfItems
            + "\">Next page</a>");
      }
    }
    catch (Exception e)
    {
      sp.printHTML("Exception getting item list: " + e + "<br>");
    }
  }

  /* Read the parameters, lookup the remote category and region  and build the web page with
     the list of items */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    Integer categoryId = null, regionId = null;
    Category category = null;
    Region region = null;
    Integer page = null;
    Integer nbOfItems = null;
    Session sess = null;
    ServletPrinter sp = null;
    
    sp = new ServletPrinter(response, "SearchItemsByRegion");
    
    String categoryStr = request.getParameter("category");
    if (categoryStr == null || categoryStr.equals(""))
    {
      printError("You must provide a category!<br>", sp);
      return;
    }
    categoryId = new Integer(categoryStr);
    
    String regionStr = request.getParameter("region");
    if (regionStr == null || regionStr.equals(""))
    {
      printError("You must provide a region!<br>", sp);
      return;
    }
    regionId = new Integer(regionStr);
    
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
    try
    {
      region = (Region) sess.get(Region.class, regionId);
    }
    catch (HibernateException he)
    {
      printError("Exception getting region: " + he + "<br>", sp);
      releaseSession(sess);
      return;
    }
    if (region == null)
    {
      printError("This region does not exist in the database.", sp);
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
    try
    {
      region = (Region) sess.load(Region.class, regionId);
    }
    catch (HibernateException he)
    {
      printError("Exception getting region: " + he + "<br>", sp);
      releaseSession(sess);
      return;
    }
    
    sp.printHTMLheader("RUBiS: Search items by region");
    
    itemList(category, region, page, nbOfItems, sess, sp);
    
    releaseSession(sess);
    
    sp.printHTMLfooter();
  }

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
