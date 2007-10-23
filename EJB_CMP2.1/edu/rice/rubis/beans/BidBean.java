package edu.rice.rubis.beans;

import java.rmi.*;
import javax.ejb.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

/**
 * BidBean is an entity bean with "container managed persistence". 
 * The state of an instance is stored into a relational database. 
 * The following table should exist:<p>
 * <pre>
 * CREATE TABLE bids (
 *    id      INTEGER UNSIGNED NOT NULL UNIQUE,
 *    user_id INTEGER,
 *    item_id INTEGER,
 *    qty     INTEGER,
 *    bid     FLOAT UNSIGNED NOT NULL,
 *    max_bid FLOAT UNSIGNED NOT NULL,
 *    date    DATETIME
 *   INDEX item (item_id),
 *   INDEX user (user_id)
 * );
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public abstract class BidBean implements EntityBean 
{
  private EntityContext entityContext;
  private transient boolean isDirty; // used for the isModified function

  /****************************/
  /* Abstract accessor methods*/
  /****************************/

  /**
   * Set bid id.
   *
   * @since 1.0
   */
  public abstract void setId(Integer id);

  
  /**
   * Get bid's id.
   *
   * @return bid id
   */
  public abstract Integer getId();


  /**
   * Get how many of this item the user wants.
   *
   * @return quantity of items for this bid.
   */
  public abstract int getQuantity();


  /**
   * Get the bid of the user.
   *
   * @return user's bid
   */
  public abstract float getBid();


  /**
   * Get the maximum bid wanted by the user.
   *
   * @return user's maximum bid
   */
  public abstract float getMaxBid();


  /**
   * Time of the Bid in the format 'YYYY-MM-DD hh:mm:ss'
   *
   * @return bid time
   */
  public abstract String getDate();


  /**
   * Set a new quantity for this bid
   *
   * @param Qty quantity
   */
  public abstract void setQuantity(int Qty);


  /**
   * Set a new bid on the item for the user.
   * <pre>
   * Warning! This method does not update the maxBid value in the items table
   * </pre>
   *
   * @param newBid a <code>float</code> value
   */
  public abstract void setBid(float newBid);


  /**
   * Set a new maximum bid on the item for the user
   *
   * @param newBid a <code>float</code> value
   */
  public abstract void setMaxBid(float newBid);


  /**
   * Set a new date for this bid
   *
   * @param newDate bid date
   */
  public abstract void setDate(String newDate);


  /*****************/
  /* relationships */
  /*****************/

  // This entity bean has a many to one relationship with the User entity.

  /**
   * Get the user.
   *
   * @return user
   */
  public abstract UserLocal getUser();


  /**
   * Set a new user.
   *
   * @param newUser user
   */
  public abstract void setUser(UserLocal newUser);


  // This entity bean has a many to one relationship with the Item entity.

  /**
   * Get the item.
   *
   * @return item
   */
  public abstract ItemLocal getItem();


  /**
   * Set a new item.
   *
   * @param newItem item
   */
  public abstract void setItem(ItemLocal newItem);


  /**
   * Give the nick name of the bidder
   *
   * @return bidder's nick name
   */
  public String getBidderNickName()
  {
    return getUser().getNickName();
  }

  /**
   * This method is used to create a new Bid Bean.
   * The date is automatically set to the current date when the method is called.
   *
   * @param bidUser bidder
   * @param bidItem item
   * @param userBid the amount of the user bid
   * @param userMaxBid the maximum amount the user wants to bid
   * @param quantity number of items the user wants to buy
   *
   * @return pk primary key set to null
   * @exception CreateException if an error occurs
   */
  public Integer ejbCreate(UserLocal bidUser, ItemLocal bidItem, float userBid, float userMaxBid, int quantity) throws CreateException
  {
    bidItem.setMaxBid(userBid);
    bidItem.addOneBid();
     /*// Connecting to SB_IDManager Home interface thru JNDI
      SB_IDManagerLocalHome home = null;
      SB_IDManagerLocal idManager = null;
      
      try 
      {
        InitialContext initialContext = new InitialContext();
        home = (SB_IDManagerLocalHome)initialContext.lookup("java:comp/env/ejb/SB_IDManager");
      } 
      catch (Exception e)
      {
        throw new EJBException("Cannot lookup SB_IDManager: " +e);
      }
     try 
      {
        idManager = home.create();
        while (true)
        {
          try
          {
            setId(idManager.getNextBidID());
            break;
          }
          catch (TransactionRolledbackLocalException ex)
          {
            ex.printStackTrace();
          }
        }
        setBid(userBid);
        setMaxBid(userMaxBid);
        setQuantity(quantity);
        setDate(TimeManagement.currentDateToString());
      } 
     catch (Exception e)
     {
       throw new EJBException("Cannot create bid: " +e);
     }
    return null;*/
    
    setBid(userBid);
    setMaxBid(userMaxBid);
    setQuantity(quantity);
    setDate(TimeManagement.currentDateToString());
    
    return null;
  }


  /** This method just set an internal flag to 
      reload the id generated by the DB */
  public void ejbPostCreate(UserLocal bidUser, ItemLocal bidItem, float userBid, float userMaxBid, int quantity) 
  {
    setUser(bidUser);
    setItem(bidItem);
    
    isDirty = true; // the id has to be reloaded from the DB
  }

  /** Persistence is managed by the container and the bean
      becomes up to date */
  public void ejbLoad()
  {
    isDirty = false;
  }

  /** Persistence is managed by the container and the bean
      becomes up to date */
  public void ejbStore()
  {
    isDirty = false;
  }

  /** This method is empty because persistence is managed by the container */
  public void ejbActivate(){}
  /** This method is empty because persistence is managed by the container */
  public void ejbPassivate(){}
  /** This method is empty because persistence is managed by the container */
  public void ejbRemove() throws RemoveException {}

  /**
   * Sets the associated entity context. The container invokes this method 
   *  on an instance after the instance has been created. 
   * 
   * This method is called in an unspecified transaction context. 
   * 
   * @param context An EntityContext interface for the instance. The instance should 
   *              store the reference to the context in an instance variable. 
   * @exception EJBException  Thrown by the method to indicate a failure 
   *                          caused by a system-level error.
   */
  public void setEntityContext(EntityContext context)
  {
    entityContext = context;
  }

  /**
   * Unsets the associated entity context. The container calls this method 
   *  before removing the instance. This is the last method that the container 
   *  invokes on the instance. The Java garbage collector will eventually invoke 
   *  the finalize() method on the instance. 
   *
   * This method is called in an unspecified transaction context. 
   * 
   * @exception EJBException  Thrown by the method to indicate a failure 
   *                          caused by a system-level error.
   */
  public void unsetEntityContext()
  {
    entityContext = null;
  }


  /**
   * Returns true if the beans has been modified.
   * It prevents the EJB server from reloading a bean
   * that has not been modified.
   *
   * @return a <code>boolean</code> value
   */
  /*public boolean isModified() 
  {
    return isDirty;
  }*/


  /**
   * Display bid history information as an HTML table row
   *
   * @return a <code>String</code> containing HTML code
   * @since 1.0
   */
  public String printBidHistory()
  {
    return "<TR><TD><a href=\""+BeanConfig.context+"/servlet/ViewUserInfo?userId="+getUser().getId()+
      "\">"+getBidderNickName()+"<TD>"+getBid()+"<TD>"+getDate()+"\n";
  }
}
