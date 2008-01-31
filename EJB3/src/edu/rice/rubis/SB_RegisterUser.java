package edu.rice.rubis;

import java.rmi.RemoteException;

/**
 * This is the Remote Interface of the SB_RegisterUser Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
public interface SB_RegisterUser {

  /**
   * Create a new user.
   *
   * @param firstname user's first name
   * @param lastname user's last name
   * @param nickname user's nick name
   * @param email user's email
   * @param password user's password
   * @param regionName name of the region where the user live
   * @return a string in html format
   * @since 1.1
   */
  public String createUser(String firstname, String lastname, String nickname, String email, String password, String regionName) throws RemoteException;

}
