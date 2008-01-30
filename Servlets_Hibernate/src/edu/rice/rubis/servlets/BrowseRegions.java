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

import edu.rice.rubis.hibernate.Region;

/** Builds the html page with the list of all region in the database */
public class BrowseRegions extends HibernateRubisHttpServlet
{

  public int getPoolSize()
  {
    return Config.BrowseRegionsPoolSize;
  }

/**
 * Get the list of regions from the database
 */
  private void regionList(ServletPrinter sp)
  {
    Session sess = null;
    Region region = null;
    List lst = null;
    
    // get the list of regions
    try
    {
      sess = getSession();
      
      Query q = sess.createQuery("select region from Region as region");
      lst = q.list();
    }
    catch (HibernateException he)
    {
      sp.printHTML("Failed to executeQuery for the list of regions" + he);
      releaseSession(sess);
      return;
    }
    
    try
    {
      Iterator it = lst.iterator();
      
      if (!it.hasNext())
      {
        sp.printHTML(
          "<h2>Sorry, but there is no region available at this time. Database table is empty</h2><br>");
        releaseSession(sess);
        return;
      }
      else
        sp.printHTML("<h2>Currently available regions</h2><br>");
      
      do
      {
        region = (Region) it.next();
        sp.printRegion(region);
      }
      while (it.hasNext());
      
      releaseSession(sess);
    }
    catch (Exception e)
    {
      sp.printHTML("Exception getting region list: " + e + "<br>");
      releaseSession(sess);
    }
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    ServletPrinter sp = null;
    sp = new ServletPrinter(response, "BrowseRegions");
    sp.printHTMLheader("RUBiS: Available regions");
    
    regionList(sp);
    sp.printHTMLfooter();
  }

  /**
   * Clean up the connection pool.
   */
  public void destroy()
  {
    super.destroy();
  }

}
