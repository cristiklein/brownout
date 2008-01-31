package edu.rice.rubis;

import java.rmi.RemoteException;

/**
 * This is the Remote Interface of the SB_BrowseCategories Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
public interface SB_BrowseCategories {

  /**
   * Get all the categories from the database.
   *
   * @return a string that is the list of categories in html format
   * @since 1.1
   */
  public String getCategories(String regionName, String userName, String password) throws RemoteException;

}
