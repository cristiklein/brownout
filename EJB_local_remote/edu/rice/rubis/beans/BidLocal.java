package edu.rice.rubis.beans;

import javax.ejb.*;
import java.rmi.*;

/**
 * This is the Local Interface for the Bid Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public interface BidLocal extends EJBLocalObject {
  /**
   * Get bid's id.
   *
   * @return bid id
   * @exception RemoteException if an error occurs
   */
  public Integer getId();

  /**
   * Get the user id which is the primary key in the users table.
   *
   * @return user id
   * @exception RemoteException if an error occurs
   */
  public Integer getUserId();

  /**
   * Get the item id which is the primary key in the items table.
   *
   * @return item id
   * @exception RemoteException if an error occurs
   */
  public Integer getItemId();

  /**
   * Get how many of this item the user wants.
   *
   * @return quantity of items for this bid.
   * @exception RemoteException if an error occurs
   */
  public int getQuantity();

  /**
   * Get the bid of the user.
   *
   * @return user's bid
   * @exception RemoteException if an error occurs
   */
  public float getBid();

  /**
   * Get the maximum bid wanted by the user.
   *
   * @return user's maximum bid
   * @exception RemoteException if an error occurs
   */
  public float getMaxBid();

  /**
   * Time of the Bid in the format 'YYYY-MM-DD hh:mm:ss'
   *
   * @return bid time
   * @exception RemoteException if an error occurs
   */
  public String getDate();

  /**
   * Give the nick name of the bidder.
   *
   * @return bidder's nick name
   * @exception RemoteException if an error occurs
   */
  public String getBidderNickName();
  

  /**
   * Set a new user identifier. This id must match
   * the primary key of the users table.
   *
   * @param id user id
   * @exception RemoteException if an error occurs
   */
  public void setUserId(Integer id);

  /**
   * Set a new item identifier. This id must match
   * the primary key of the items table.
   *
   * @param id item id
   * @exception RemoteException if an error occurs
   */
  public void setItemId(Integer id);

  /**
   * Set a new quantity for this bid
   *
   * @param Qty quantity
   * @exception RemoteException if an error occurs
   */
  public void setQuantity(int Qty);

  /**
   * Set a new bid on the item for the user
   *
   * @param newBid bid price
   * @exception RemoteException if an error occurs
   */
  public void setBid(float newBid);

  /**
   * Set a new maximum bid on the item for the user
   *
   * @param newBid maximum bid price
   * @exception RemoteException if an error occurs
   */
  public void setMaxBid(float newBid);

  /**
   * Set a new date for this bid
   *
   * @param newDate bid date
   * @exception RemoteException if an error occurs
   */
  public void setDate(String newDate);

  /**
   * Display bid history information as an HTML table row
   *
   * @return a <code>String</code> containing HTML code
   * @exception RemoteException if an error occurs
   * @since 1.0
   */
  public String printBidHistory();
}
