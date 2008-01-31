package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used to get the list of regions
 * from the database. 
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_BrowseRegionsBean")
@Remote(SB_BrowseRegions.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_BrowseRegionsBean implements SB_BrowseRegions
{
  @PersistenceContext
  private EntityManager em;

  /**
   * Get all the regions.
   *
   * @return a string that is the list of regions in html format
   * @since 1.1
   */
  public String getRegions() throws RemoteException
  {
    Collection list;
    RegionBean reg;
    String html = "";

    try
    {
      Query q = em.createNamedQuery("allRegions");
      list = q.getResultList();
      if (list.isEmpty())
        html =
          ("<h2>Sorry, but there is no region available at this time. Database table is empty</h2><br>");
      else
      {
        Iterator it = list.iterator();
        while (it.hasNext())
        {
          reg = (RegionBean)it.next();
          html = html + printRegion(reg);
        }
      }
    }
    catch (Exception e)
    {
      throw new RemoteException("Exception getting region list: " + e);
    }
    return html;
  }

  /** 
   * Region related printed functions
   *
   * @param region the region to display
   * @return a string in html format
   * @since 1.1
   */

  public String printRegion(RegionBean region) throws RemoteException
  {
    String html;
    try
    {
      String name = region.getName();
      html =
        "<a href=\""
          + BeanConfig.context
          + "/servlet/BrowseCategories?region="
          + URLEncoder.encode(name)
          + "\">"
          + name
          + "</a><br>\n";
    }
    catch (EJBException re)
    {
      throw new EJBException("Unable to print Region (exception: " + re + ")");
    }
    return html;
  }
}
