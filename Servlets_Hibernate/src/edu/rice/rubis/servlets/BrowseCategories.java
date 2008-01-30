package edu.rice.rubis.servlets;

import java.io.IOException;

import java.util.List;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.hibernate.Session;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;

import edu.rice.rubis.hibernate.Category;
import edu.rice.rubis.hibernate.User;
import edu.rice.rubis.hibernate.Region;

/** Builds the html page with the list of all categories and provides links to browse all
    items in a category or items in a category for a given region */
public class BrowseCategories extends HibernateRubisHttpServlet
{

  public int getPoolSize()
  {
    return Config.BrowseCategoriesPoolSize;
  }

  /** List all the categories in the database */
  private void categoryList(Region region, User user, Session sess, ServletPrinter sp)
  {
    Category category = null;
    List lst = null;
    
    // get the list of categories
    try
    {
      Query q = sess.createQuery("select category from Category as category");
      lst = q.list();
    }
    catch (HibernateException he)
    {
      sp.printHTML("Failed to execute query for categories list: " + he);
      return;
    }
    
    try
    {
      Iterator it = lst.iterator();
      
      if (!it.hasNext())
      {
        sp.printHTML("<h2>Sorry, but there is no category available at this time. Database table is empty</h2><br>");
        return;
      }
      else
        sp.printHTML("<h2>Currently available categories</h2><br>");
      
      do
      {
        category = (Category) it.next();
        
        if (region != null)
        {
          sp.printCategoryByRegion(category, region);
        }
        else
        {
          if (user != null)
            sp.printCategoryToSellItem(category, user);
          else
            sp.printCategory(category);
        }
      }
      while (it.hasNext());
    }
    catch (Exception e)
    {
      sp.printHTML("Exception getting categories list: " + e + "<br>");
    }
  }

  /** Build the html page for the response */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    ServletPrinter sp = null;
    Session sess = null;
    String nick = null, pass = null;
    User user = null;
    String name = null;
    Region region = null;
    
    sp = new ServletPrinter(response, "BrowseCategories");
    sp.printHTMLheader("RUBiS available categories");
    
    nick = request.getParameter("nickname");
    pass = request.getParameter("password");
    
    sess = getSession();
    
    // Authenticate the user who want to sell items
    if ((nick != null && !nick.equals(""))
      || (pass != null && !pass.equals("")))
    {
      Auth auth = new Auth(sess, sp);
      user = auth.authenticate(nick, pass);
      if (user == null)
      {
        sp.printHTML(
          " You don't have an account on RUBiS!<br>You have to register first.<br>");
        sp.printHTMLfooter();
        releaseSession(sess);
        return;
      }
    }
    
    name = request.getParameter("region");
    if (name != null && !name.equals(""))
    {
      // get the region ID
      try
      {
        Query q = sess.createQuery("select region from Region as region where region.name = :name");
        q.setString("name", name);
        List lst = q.list();
        Iterator it = lst.iterator();
        
        if (!it.hasNext())
        {
          sp.printHTML(
            " Region " + name + " does not exist in the database!<br>");
          releaseSession(sess);
          return;
        }
        
        region = (Region) it.next();
      }
      catch (HibernateException he)
      {
        sp.printHTML("Failed to execute Query for region: " + he);
        releaseSession(sess);
        return;
      }
    }
    
    categoryList(region, user, sess, sp);
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
