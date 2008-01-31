package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used to get the list of items
 * that belong to a specific category in a specific region. 
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_SearchItemsByRegionBean")
@Remote(SB_SearchItemsByRegion.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_SearchItemsByRegionBean implements SB_SearchItemsByRegion
{
  @PersistenceContext
  private EntityManager em;

  /**
   * Get the items in a specific category.
   *
   * @return a string that is the list of items in html format
   * @since 1.1
   */
  public String getItems(Integer categoryId, Integer regionId, int page, int nbOfItems) throws RemoteException
  {
    Collection list;
    CategoryBean category;
    RegionBean region;
    StringBuffer html = new StringBuffer();

    try
    {
      category = (CategoryBean)em.find(CategoryBean.class, categoryId);
      if (category == null)
        throw new Exception("Entity does not exist");
    }
    catch (Exception e)
    {
      throw new RemoteException("Cannot get category: " +e);
    }

    try
    {
      region = em.find(RegionBean.class, regionId);
      if (region == null)
        throw new Exception("Entity does not exist");
    }
    catch (Exception e)
    {
      throw new RemoteException("Cannot get region: " +e);
    }

    try
    {
      GregorianCalendar now = new GregorianCalendar();
      
      Query q = em.createNamedQuery("currentItemsInCategoryAndRegion");
      q.setParameter(1, category);
      q.setParameter(2, region);
      q.setParameter(3, now);
      q.setFirstResult(page*nbOfItems);
      q.setMaxResults(nbOfItems);
      list = q.getResultList();
      Iterator it = list.iterator();
      while (it.hasNext()) 
      {
        ItemBean item = (ItemBean)it.next();
        html.append(printItem(item));
      }
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot get items list: " +e);
    }
    return html.toString();
  }

   /** 
   * Item related printed function
   *
   * @param item the item to display
   * @return a string in html format
   * @since 1.1
   */
  public String printItem(ItemBean item) throws RemoteException
  {
    try
    {
      return item.printItem();
    }
    catch (EJBException re)
    {
      throw new EJBException("Unable to print Item (exception: "+re+")<br>\n");
    }
  }
}
