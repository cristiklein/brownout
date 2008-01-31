package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used to get the list of 
 * categories from database and return the information to the BrowseRegions servlet. 
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_BrowseCategoriesBean")
@Remote(SB_BrowseCategories.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_BrowseCategoriesBean implements SB_BrowseCategories 
{
  @PersistenceContext
  private EntityManager em;
  @EJB
  private SB_AuthLocal auth;

  /**
   * Get all the categories from the database.
   *
   * @return a string that is the list of categories in html format
   * @since 1.1
   */
  /** List all the categories in the database */
  public String getCategories(String regionName, String username, String password) throws RemoteException
  {
    Collection list;
    CategoryBean cat;
    String html = "";
    int regionId = -1;
    UserBean user = null;

    if (regionName != null && regionName !="")
    {
      // get the region ID
      try
      {
        Query q = em.createNamedQuery("regionByName");
        q.setParameter(1, regionName);
        RegionBean region = (RegionBean)q.getSingleResult();
        regionId = region.getId();
      }
      catch (Exception e)
      {
        throw new RemoteException(" Region "+regionName+" does not exist in the database!<br>(got exception: " +e+")");
      }
    }
    else
    {
      // Authenticate the user who wants to sell items
      if ((username != null && !username.equals("")) || (password != null && !password.equals("")))
      {
        try 
        {
          user = auth.authenticate(username, password);
        } 
        catch (Exception e)
        {
          throw new RemoteException("Authentication failed: " +e);
        }
        if (user == null)
        {
           html = (" You don't have an account on RUBiS!<br>You have to register first.<br>");
           return html;
        }
      }
    }

    try 
    {
      Query q = em.createNamedQuery("allCategories");
      list = q.getResultList();
      if (list.isEmpty())
        html = ("<h2>Sorry, but there is no category available at this time. Database table is empty</h2><br>");
      else
      {
        Iterator it = list.iterator();
        while (it.hasNext())
        {
          cat = (CategoryBean)it.next();
          if (regionId != -1)
          {
            html = html + printCategoryByRegion(cat, regionId);
          }
          else
          {
            if (user != null)
              html = html + printCategoryToSellItem(cat, user.getId());
            else
              html = html + printCategory(cat);
          }
        }
      }
    } 
    catch (Exception e) 
    {
      throw new RemoteException("Exception getting category list: " + e);
    }
    return html;
  }

  /** 
   * Category related printed functions
   *
   * @param category the category to display
   * @return a string in html format
   * @since 1.1
   */

  public String printCategory(CategoryBean category) throws RemoteException
  {
    String html = "";
    try
    {
      html = (category.printCategory());
    }
    catch (EJBException re)
    {
      throw new EJBException("Unable to print Category 1 (exception: "+re+")");
    }
    return html;
  }

  /** 
   * List all the categories with links to browse items by region
   * @return a string in html format
   * @since 1.1
   */
  public String printCategoryByRegion(CategoryBean category, int regionId) throws RemoteException
  {
    String html = "";
    try
    {
      html = (category.printCategoryByRegion(regionId));
    }
    catch (EJBException re)
    {
      throw new EJBException("Unable to print Category 2 (exception: "+re+")<br>\n");
    }
    return html;
  }

  /** 
   * Lists all the categories and links to the sell item page
   * @return a string in html format
   * @since 1.1
   */
  public String printCategoryToSellItem(CategoryBean category, int userId) throws RemoteException
  {
    String html= "";
    try
    {
      html = (category.printCategoryToSellItem(userId));
    }
    catch (EJBException re)
    {
      throw new EJBException("Unable to print Category 3 (exception: "+re+")<br>\n");
    }
    return html;
  }
}
