package edu.rice.rubis;

import java.rmi.RemoteException;

/**
 * This is the Remote Interface of the SB_PutBid Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
public interface SB_PutBid {

  /**
   * Authenticate the user and get the information to build the html form.
   *
   * @return a string in html format
   * @since 1.1
   */
  public String getBiddingForm(Integer itemId, String username, String password) throws RemoteException;

}
