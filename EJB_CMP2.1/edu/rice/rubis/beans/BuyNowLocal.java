package edu.rice.rubis.beans;

import javax.ejb.*;
import java.rmi.*;

/**
 * This is the Local Interface for the BuyNow Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public interface BuyNowLocal extends EJBLocalObject {
  /**
   * Get BuyNow id.
   *
   * @return BuyNow id
   */
  public Integer getId();

  /**
   * Get the buyer.
   *
   * @return buyer
   */
  public UserLocal getBuyer();

  /**
   * Get the item.
   *
   * @return item
   */
  public ItemLocal getItem();

  /**
   * Get how many of this item the buyer has bought.
   *
   * @return quantity of items for this bid.
   */
  public int getQuantity();

  /**
   * Time of the BuyNow in the format 'YYYY-MM-DD hh:mm:ss'
   *
   * @return bid time
   */
  public String getDate();

  /**
   * Set a new buyer.
   *
   * @param newBuyer buyer
   */
  public void setBuyer(UserLocal newBuyer);

  /**
   * Set a new item.
   *
   * @param newItem item
   */
  public void setItem(ItemLocal newItem);

  /**
   * Set a new quantity for this buy
   *
   * @param Qty quantity
   */
  public void setQuantity(int Qty);

  /**
   * Set a new date for this buy
   *
   * @param newDate bid date
   */
  public void setDate(String newDate);
}
