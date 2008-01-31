package edu.rice.rubis;

import java.rmi.RemoteException;

/**
 * This is the Remote Interface of the SB_StoreBid Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
public interface SB_StoreBid {

  /**
   * Create a new bid and update the number of bids and maxBid on the item.
   *
   * @param userId id of the user posting the bid
   * @param itemId id of the item related to the bid
   * @param bid value of the bid
   * @param maxBid maximun bid
   * @param qty number of items
   * @since 1.1
   */
  public void createBid(Integer userId, Integer itemId, float bid, float maxBid, int qty) throws RemoteException;

}
