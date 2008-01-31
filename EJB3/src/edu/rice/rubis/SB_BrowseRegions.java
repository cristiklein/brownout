package edu.rice.rubis;

import java.rmi.RemoteException;

/**
 * This is the Remote Interface of the SB_BrowseRegions Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
public interface SB_BrowseRegions {

  /**
   * Get all the regions from the database.
   *
   * @return a string that is the list of regions in html format
   * @since 1.1
   */
  public String getRegions() throws RemoteException;

}
