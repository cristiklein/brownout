package edu.rice.rubis.beans;

import javax.ejb.*;
import java.rmi.*;
import java.util.Collection;

/**
 * This is the local Interface of the IDs Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public interface IDsLocal extends EJBLocalObject {
  /**
   * Get id.
   *
   * @return IDs id
   */
  public Integer getId();

  /**
   * Get the category count.
   *
   * @return IDs category count
   */
  public Integer getCategoryCount();

  /**
   * Set the category count.
   *
   * @param newCategoryCount category count
   */
  public void setCategoryCount(Integer newCategoryCount);

  /**
   * Get the region count.
   *
   * @return IDs region count
   */
  public Integer getRegionCount();

  /**
   * Set the region count.
   *
   * @param newRegionCount region count
   */
  public void setRegionCount(Integer newRegionCount);

  /**
   * Get the user count.
   *
   * @return IDs user count
   */
  public Integer getUserCount();

  /**
   * Set the user count.
   *
   * @param newUserCount user count
   */
  public void setUserCount(Integer newUserCount);

  /**
   * Get the item count.
   *
   * @return IDs item count
   */
  public Integer getItemCount();

  /**
   * Set the item count.
   *
   * @param newItemCount item count
   */
  public void setItemCount(Integer newItemCount);

  /**
   * Get the comment count.
   *
   * @return IDs comment count
   */
  public Integer getCommentCount();

  /**
   * Set the comment count.
   *
   * @param newCommentCount comment count
   */
  public void setCommentCount(Integer newCommentCount);

  /**
   * Get the bid count.
   *
   * @return IDs bid count
   */
  public Integer getBidCount();

  /**
   * Set the bid count.
   *
   * @param newBidCount bid count
   */
  public void setBidCount(Integer newBidCount);

  /**
   * Get the buy now count.
   *
   * @return IDs buyNow count
   */
  public Integer getBuyNowCount();

  /**
   * Set the buy now count.
   *
   * @param newBuyNowCount buyNow count
   */
  public void setBuyNowCount(Integer newBuyNowCount);
}
