package edu.rice.rubis.beans;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.FinderException;
import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * SB_IDManagerBean is used to generate id since the AUTO_INCREMENT
 * feature of the database that automatically generate id on the primary key 
 * is not supported by JBoss. 
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */

public class SB_IDManagerBean implements SessionBean 
{
  private SessionContext sessionContext;
  protected Context initialContext = null;
  protected IDsLocal ids = null;
  private static final int BLOCK_SIZE = 10;
  private int categoryStart = 0, categoryEnd = 0;
  private int regionStart = 0, regionEnd = 0;
  private int userStart = 0, userEnd = 0;
  private int itemStart = 0, itemEnd = 0;
  private int commentStart = 0, commentEnd = 0;
  private int bidStart = 0, bidEnd = 0;
  private int buyNowStart = 0, buyNowEnd = 0;

  /** 
   * Generate the category id.
   *
   * @return Value of the ID
   * @since 1.1
   */
  public Integer getNextCategoryID()
  {
    if (categoryStart == categoryEnd)
    {
      categoryStart = ids.getCategoryCount().intValue();
      categoryEnd = categoryStart + BLOCK_SIZE;
      ids.setCategoryCount(new Integer(categoryEnd));
    }
    categoryStart++;
    return new Integer(categoryStart);
  }

  /** 
   * Generate the region id.
   *
   * @return Value of the ID
   * @since 1.1
   */
  public Integer getNextRegionID()
  {
    if (regionStart == regionEnd)
    {
      regionStart = ids.getRegionCount().intValue();
      regionEnd = regionStart + BLOCK_SIZE;
      ids.setRegionCount(new Integer(regionEnd));
    }
    regionStart++;
    return new Integer(regionStart);
  }

  /** 
   * Generate the user id.
   *
   * @return Value of the ID
   * @since 1.1
   */
  public Integer getNextUserID()
  {
    if (userStart == userEnd)
    {
      userStart = ids.getUserCount().intValue();
      userEnd = userStart + BLOCK_SIZE;
      ids.setUserCount(new Integer(userEnd));
      System.out.println("userEnd: "+userEnd);
    }
    userStart++;
    return new Integer(userStart);
  }

  /** 
   * Generate the item id.
   *
   * @return Value of the ID
   * @since 1.1
   */
  public Integer getNextItemID()
  {
    if (itemStart == itemEnd)
    {
      itemStart = ids.getItemCount().intValue();
      itemEnd = itemStart + BLOCK_SIZE;
      ids.setItemCount(new Integer(itemEnd));
      System.out.println("itemEnd: "+itemEnd);
    }
    itemStart++;
    return new Integer(itemStart);
  }

  /** 
   * Generate the comment id.
   *
   * @return Value of the ID
   * @since 1.1
   */
  public Integer getNextCommentID()
  {
    if (commentStart == commentEnd)
    {
      commentStart = ids.getCommentCount().intValue();
      commentEnd = commentStart + BLOCK_SIZE;
      ids.setCommentCount(new Integer(commentEnd));
      System.out.println("commentEnd: "+commentEnd);
    }
    commentStart++;
    return new Integer(commentStart);
  }

  /** 
   * Generate the bid id.
   *
   * @return Value of the ID
   * @since 1.1
   */
  public Integer getNextBidID()
  {
    if (bidStart == bidEnd)
    {
      bidStart = ids.getBidCount().intValue();
      bidEnd = bidStart + BLOCK_SIZE;
      ids.setBidCount(new Integer(bidEnd));
      System.out.println("bidEnd: "+bidEnd);
    }
    bidStart++;
    return new Integer(bidStart);
  }

  /** 
   * Generate the buyNow id.
   *
   * @return Value of the ID
   * @since 1.1
   */
  public Integer getNextBuyNowID()
  {
    if (buyNowStart == buyNowEnd)
    {
      buyNowStart = ids.getBuyNowCount().intValue();
      buyNowEnd = buyNowStart + BLOCK_SIZE;
      ids.setBuyNowCount(new Integer(buyNowEnd));
      System.out.println("buyNowEnd: "+buyNowEnd);
    }
    buyNowStart++;
    return new Integer(buyNowStart);
  }


  // ======================== EJB related methods ============================

  /**
   * This method is empty for a stateless session bean
   */
  public void ejbCreate() throws CreateException
  {
  }

  /** This method is empty for a stateless session bean */
  public void ejbActivate() {}
  /** This method is empty for a stateless session bean */
  public void ejbPassivate() {}
  /** This method is empty for a stateless session bean */
  public void ejbRemove() {}


  /** 
   * Sets the associated session context. The container calls this method 
   * after the instance creation. This method is called with no transaction context.
   *
   * @param sessionContext - A SessionContext interface for the instance.
   */
  public void setSessionContext(SessionContext sessionContext)
  {
    this.sessionContext = sessionContext;
    
    IDsLocalHome idsHome = null;
    
    try
    {
      initialContext = new InitialContext();
    }
    catch (Exception e)
    {
      throw new EJBException("Cannot get JNDI InitialContext");
    }
    try
    {
      idsHome = (IDsLocalHome)initialContext.lookup("java:comp/env/ejb/IDs");
    }
    catch (Exception e)
    {
      throw new EJBException("Cannot lookup IDs: " +e);
    }
    try
    {
      IDsPK idsPK = new IDsPK();
      ids = idsHome.findByPrimaryKey(idsPK);
    }
    catch (Exception e)
    {
      throw new EJBException("Cannot find IDs: " +e);
    }
  }

}
