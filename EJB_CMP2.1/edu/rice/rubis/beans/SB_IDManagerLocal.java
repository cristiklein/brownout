package edu.rice.rubis.beans;

import javax.ejb.*;
import java.rmi.*;

/**
 * This is the local Interface of the SB_IDManager Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */
public interface SB_IDManagerLocal extends EJBLocalObject {

  /** 
   * Generate the category id.
   *
   * @return Value of the ID
   */
    public Integer getNextCategoryID();

  /** 
   * Generate the region id.
   *
   * @return Value of the ID
   */
    public Integer getNextRegionID();

  /** 
   * Generate the user id.
   *
   * @return Value of the ID
   */
  public Integer getNextUserID();

  /** 
   * Generate the item id.
   *
   * @return Value of the ID
   */
  public Integer getNextItemID();

  /** 
   * Generate the comment id.
   *
   * @return Value of the ID
   */
  public Integer getNextCommentID();

  /** 
   * Generate the bid id.
   *
   * @return Value of the ID
   */
  public Integer getNextBidID();

  /** 
   * Generate the buyNow id.
   *
   * @return Value of the ID
   */
  public Integer getNextBuyNowID();

}
