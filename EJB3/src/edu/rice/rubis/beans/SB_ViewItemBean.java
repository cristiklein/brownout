package edu.rice.rubis.beans;

import edu.rice.rubis.*;
import javax.ejb.*;
import javax.persistence.*;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;

/**
 * This is a stateless session bean used to get the information about an item.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
@Stateless(mappedName="SB_ViewItemBean")
@Remote(SB_ViewItem.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SB_ViewItemBean implements SB_ViewItem
{
  @PersistenceContext
  private EntityManager em;

  /**
   * Get the full description of an item and the bidding option if userId>0.
   *
   * @param item an <code>Item</code> value
   * @param userId an authenticated user id
   */
  public String getItemDescription(Integer itemId, int userId) throws RemoteException
  {
    StringBuffer html = new StringBuffer();
    ItemBean item;

    try 
    {
      item = (ItemBean)em.find(ItemBean.class, itemId);
      if (item == null)
        throw new Exception("Entity does not exist");
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot lookup Item: " +e);
    }
    try
    {
      float buyNow = item.getBuyNow();
      int nbOfBids = item.getNbOfBids();
      int qty = item.getQuantity();
      String firstBid;
      float maxBid = item.getMaxBid();
      String itemName = item.getName();

      if (maxBid == 0)
      {
        firstBid = "none";
        maxBid = item.getInitialPrice();
      }
      else
      {
        if (qty > 1)
        {
          try
          {
            /* Get the qty max first bids and parse bids in this order
               until qty is reached. The bid that reaches qty is the
               current minimum bid. */
            Query q = em.createNamedQuery("itemQtyMaxBid");
            q.setParameter(1, item);
            q.setMaxResults(qty);
            Collection list = q.getResultList();
            BidBean bid;
            int numberOfItems = 0;
            Iterator it = list.iterator();
            while (it.hasNext()) 
            {
              bid = (BidBean)it.next();
              numberOfItems += bid.getQuantity();
              if (numberOfItems >= qty)
              {
                maxBid = bid.getBid();
                break;
              }
            }
          } 
          catch (Exception e)
          {
            throw new RemoteException("Problem while computing current bid: "+e+"<br>");
          }
        }

        Float foo = new Float(maxBid);
        firstBid = foo.toString();
      }
      
      if (userId>0)
      {
        //header = printHTMLheader("RUBiS: Bidding\n");
        html.append(printHTMLHighlighted("You are ready to bid on: "+itemName));
      }
      else
      {
        //header = printHTMLheader("RUBiS: Viewing "+itemName+"\n");
        html.append(printHTMLHighlighted(itemName));
      }
      html.append("<TABLE>\n"+
                  "<TR><TD>Currently<TD><b><BIG>"+maxBid+"</BIG></b>\n");
      // Check if the reservePrice has been met (if any)
      float reservePrice = item.getReservePrice();
      Integer sellerId = item.getSeller().getId();
      if (reservePrice > 0)
      { // Has the reserve price been met ?
        if (maxBid >= reservePrice)
          html.append("(The reserve price has been met)\n");
        else
          html.append("(The reserve price has NOT been met)\n");
      }
      html.append("<TR><TD>Quantity<TD><b><BIG>"+qty+"</BIG></b>\n"+
                  "<TR><TD>First bid<TD><b><BIG>"+firstBid+"</BIG></b>\n"+
                  "<TR><TD># of bids<TD><b><BIG>"+nbOfBids+"</BIG></b> (<a href=\""+BeanConfig.context+"/servlet/ViewBidHistory?itemId="+itemId+"\">bid history</a>)\n"+
                  "<TR><TD>Seller<TD><a href=\""+BeanConfig.context+"/servlet/ViewUserInfo?userId="+sellerId+"\">"+item.getSellerNickname()+"</a> (<a href=\""+BeanConfig.context+"/servlet/PutCommentAuth?to="+sellerId+"&itemId="+itemId+"\">Leave a comment on this user</a>)\n"+
                  "<TR><TD>Started<TD>"+TimeManagement.dateToString(item.getStartDate())+"\n"+
                  "<TR><TD>Ends<TD>"+TimeManagement.dateToString(item.getEndDate())+"\n"+
                  "</TABLE>");
      // Can the user buy this item now ?
      if (buyNow > 0)
        html.append("<p><a href=\""+BeanConfig.context+"/servlet/BuyNowAuth?itemId="+itemId+"\">"+
                    "<IMG SRC=\""+BeanConfig.context+"/buy_it_now.jpg\" height=22 width=150></a>"+
                    "  <BIG><b>You can buy this item right now for only $"+buyNow+"</b></BIG><br><p>\n");

      if (userId<=0)
      {
        html.append("<a href=\""+BeanConfig.context+"/servlet/PutBidAuth?itemId="+itemId+"\"><IMG SRC=\""+BeanConfig.context+"/bid_now.jpg\" height=22 width=90> on this item</a>\n");
      }

      html.append(printHTMLHighlighted("Item description"));
      html.append(item.getDescription());
      html.append("<br><p>\n");

      if (userId>0)
      {
        html.append(printHTMLHighlighted("Bidding"));
        float minBid = maxBid+1;
        html.append("<form action=\""+BeanConfig.context+"/servlet/StoreBid\" method=POST>\n"+
                  "<input type=hidden name=minBid value="+minBid+">\n"+
                  "<input type=hidden name=userId value="+userId+">\n"+
                  "<input type=hidden name=itemId value="+itemId+">\n"+
                  "<input type=hidden name=maxQty value="+qty+">\n"+
                  "<center><table>\n"+
                  "<tr><td>Your bid (minimum bid is "+minBid+"):</td>\n"+
                  "<td><input type=text size=10 name=bid></td></tr>\n"+
                  "<tr><td>Your maximum bid:</td>\n"+
                  "<td><input type=text size=10 name=maxBid></td></tr>\n");
        if (qty > 1)
          html.append("<tr><td>Quantity:</td>\n"+
                    "<td><input type=text size=5 name=qty></td></tr>\n");
        else
          html.append("<input type=hidden name=qty value=1>\n");
        html.append("</table><p><input type=submit value=\"Bid now!\"></center><p>\n");
      }
    }
    catch (RemoteException re)
    {
      throw new RemoteException("Unable to print Item description (exception: "+re+")<br>\n");
    }
    return html.toString();
  }

  /**
   * Construct a html highlighted string.
   * @param msg the message to display
   * @return a string in html format
   * @since 1.1
   */
  public String printHTMLHighlighted(String msg)
  {
    return "<TABLE width=\"100%\" bgcolor=\"#CCCCFF\">\n<TR><TD align=\"center\" width=\"100%\"><FONT size=\"4\" color=\"#000000\"><B>"+msg+"</B></FONT></TD></TR>\n</TABLE><p>\n";
  }
}
