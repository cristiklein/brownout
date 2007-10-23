package edu.rice.rubis.beans;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import java.util.Date;

/**
 * This is the Local Home interface of the BuyNow Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public interface BuyNowLocalHome extends EJBLocalHome {
  /**
   * This method is used to create a new BuyNow Bean.
   * The date is automatically set to the current date when the method is called.
   *
   * @param buyNowUser buyer
   * @param buyNowItem item
   * @param quantity number of items the user wants to buy
   *
   * @return pk primary key set to null
   * @exception CreateException if an error occurs
   */
  public BuyNowLocal create(UserLocal buyNowUser, ItemLocal buyNowItem, int quantity) throws CreateException;

  /**
   * This method is used to retrieve a BuyNow Bean from its primary key,
   * that is to say its id.
   *
   * @param id BuyNow id (primary key)
   *
   * @return the BuyNow if found else null
   * @exception FinderException if an error occurs
   */
  public BuyNowLocal findByPrimaryKey(Integer id) throws FinderException;

  /**
   * This method is used to retrieve all BuyNows from the database!
   *
   * @return List of all BuyNows (eventually empty)
   * @exception FinderException if an error occurs
   */
  public Collection findAllBuyNows() throws FinderException;

  /**
   * Get all the buyNow the user bought recently.
   *
   * @param user  user
   * @param oldest oldest date
   *
   * @return Collection of buyNows
   * @exception FinderException if an error occurs
   */
  public Collection findUserBuyNow(UserLocal user, Date oldest) throws FinderException;

}
