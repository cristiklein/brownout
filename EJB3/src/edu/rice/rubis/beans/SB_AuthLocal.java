package edu.rice.rubis.beans;

/**
 * This is the local Interface of the SB_Auth Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
public interface SB_AuthLocal {

  /**
   * Describe <code>authenticate</code> method here.
   *
   * @param name user nick name
   * @param password user password
   * @return a user or null if the password in incorrect
   */
  public UserBean authenticate (String name, String password);

}
