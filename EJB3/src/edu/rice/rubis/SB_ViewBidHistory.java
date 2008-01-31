package edu.rice.rubis;

import java.rmi.RemoteException;

/**
 * This is the Remote Interface of the SB_ViewBidHistory Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
public interface SB_ViewBidHistory {

  /**
   * Get the list of bids related to a specific item.
   *
   * @return a string in html format
   * @since 1.1
   */
  public String getBidHistory(Integer itemId) throws RemoteException;

}
