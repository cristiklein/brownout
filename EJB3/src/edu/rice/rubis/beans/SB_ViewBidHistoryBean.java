package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used get the bid history of an item.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_ViewBidHistoryBean")
@Remote(SB_ViewBidHistory.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_ViewBidHistoryBean implements SB_ViewBidHistory
{
  @PersistenceContext
  private EntityManager em;

  /**
   * Get the list of bids related to a specific item.
   *
   * @return a string in html format
   * @since 1.1
   */
  public String getBidHistory(Integer itemId) throws RemoteException 
  {
    StringBuffer html;
    ItemBean item;
    BidBean bid;
    Collection bidList;
    Iterator it;

    try 
    {
      item = (ItemBean)em.find(ItemBean.class, itemId);
      if (item == null)
        throw new Exception("Entity does not exist");
      html = new StringBuffer("<center><h3>Bid History for "+item.getName()+"<br></h3></center>");
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot lookup Item ("+itemId+"): " +e);
    }
    try 
    {
      Query q = em.createNamedQuery("itemBidHistory");
      q.setParameter(1, item);
      bidList = q.getResultList();
    }
    catch (Exception e)
    {
      throw new RemoteException("Exception getting bids list: " +e+"<br>");
    }
    if (bidList.isEmpty())
    {
      return html.append("<h3>There is no bid corresponding to this item.</h3><br>").toString();
    }
    try
    {
      html.append(printBidHistoryHeader());
      it = bidList.iterator();
      while (it.hasNext()) 
      {
        // Get the bids
        bid = (BidBean)it.next();
        html.append(bid.printBidHistory());
      }
      html.append(printBidHistoryFooter());
    }
    catch (Exception e)
    {
      throw new RemoteException("Exception getting bid: " + e +"<br>");
    }
    return html.toString();
  }

  /** 
   * Bids list header printed function
   *
   * @return a string in html format
   * @since 1.1
   */                   
  public String printBidHistoryHeader()
  {
    return "<TABLE border=\"1\" summary=\"List of bids\">\n<THEAD>\n"+
      "<TR><TH>User ID<TH>Bid amount<TH>Date of bid\n<TBODY>\n";
  }

  /** 
   * Bids list footer printed function
   *
   * @return a string in html format
   * @since 1.1
   */
  public String printBidHistoryFooter()
  {
    return "</TABLE>\n";
  }
}
