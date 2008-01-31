package edu.rice.rubis;

import java.rmi.RemoteException;

/**
 * This is the Remote Interface of the SB_ViewItem Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
public interface SB_ViewItem {

  /**
   * Get the full description of an item and the bidding option if userId>0.
   *
   * @param item an <code>Item</code> value
   * @param userId an authenticated user id
   */
  public String getItemDescription(Integer itemId, int userId) throws RemoteException;

}
