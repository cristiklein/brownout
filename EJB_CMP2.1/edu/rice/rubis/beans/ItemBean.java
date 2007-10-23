package edu.rice.rubis.beans;

import java.rmi.*;
import javax.ejb.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import java.util.GregorianCalendar;
import java.util.Collection;
/**
 * ItemBean is an entity bean with "container managed persistence".
 * The state of an instance is stored into a relational database.
 * The following table should exist:<p>
 * <pre>
 * CREATE TABLE items (
 *    id            INTEGER UNSIGNED NOT NULL UNIQUE,
 *    name          VARCHAR(100),
 *    description   TEXT,
 *    initial_price FLOAT UNSIGNED NOT NULL,
 *    quantity      INTEGER UNSIGNED NOT NULL,
 *    reserve_price FLOAT UNSIGNED DEFAULT 0,
 *    buy_now       FLOAT UNSIGNED DEFAULT 0,
 *    nb_of_bids    INTEGER UNSIGNED DEFAULT 0,
 *    max_bid       FLOAT UNSIGNED DEFAULT 0,
 *    start_date    DATETIME,
 *    end_date      DATETIME,
 *    seller        INTEGER,
 *    category      INTEGER,
 *    PRIMARY KEY(id),
 *    INDEX seller_id (seller),
 *    INDEX category_id (category)
 * );
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */

public abstract class ItemBean implements EntityBean 
{
  private EntityContext entityContext;
  private transient boolean isDirty; // used for the isModified function

  /****************************/
  /* Abstract accessor methods*/
  /****************************/

  /**
   * Set item id.
   *
   * @since 1.0
   */
  public abstract void setId(Integer id);


  /**
   * Get item id.
   *
   * @return item id
   * @since 1.0
   */
  public abstract Integer getId();


  /**
   * Get item name. This description is usually a short description of the item.
   *
   * @return item name
   * @since 1.0
   */
  public abstract String getName();


  /**
   * Get item description . This is usually an HTML file describing the item.
   *
   * @return item description
   * @since 1.0
   */
  public abstract String getDescription();


  /**
   * Get item initial price set by the seller.
   *
   * @return item initial price
   * @since 1.0
   */
  public abstract float getInitialPrice();


  /**
   * Get how many of this item are to be sold.
   *
   * @return item quantity
   * @since 1.0
   */
  public abstract int getQuantity();


  /**
   * Get item reserve price set by the seller. The seller can refuse to sell if reserve price is not reached.
   *
   * @return item reserve price
   * @since 1.0
   */
  public abstract float getReservePrice();


  /**
   * Get item Buy Now price set by the seller. A user can directly by the item at this price (no auction).
   *
   * @return item Buy Now price
   * @since 1.0
   */
  public abstract float getBuyNow();


  /**
   * Get item maximum bid (if any) for this item. This value should be the same as doing <pre>SELECT MAX(bid) FROM bids WHERE item_id=?</pre>
   *
   * @return current maximum bid or 0 if no bid
   * @since 1.1
   */
  public abstract float getMaxBid();


  /**
   * Get number of bids for this item. This value should be the same as doing <pre>SELECT COUNT(*) FROM bids WHERE item_id=?</pre>
   *
   * @return number of bids
   * @since 1.1
   */
  public abstract int getNbOfBids();


  /**
   * Start date of the auction in the format 'YYYY-MM-DD hh:mm:ss'
   *
   * @return start date of the auction
   * @since 1.0
   */
  public abstract String getStartDate();


  /**
   * End date of the auction in the format 'YYYY-MM-DD hh:mm:ss'
   *
   * @return end date of the auction
   * @since 1.0
   */
  public abstract String getEndDate();


  /**
   * Set a new item name
   *
   * @param newName item name
   * @since 1.0
   */
  public abstract void setName(String newName);


  /**
   * Set a new item description
   *
   * @param newDescription item description
   * @since 1.0
   */
  public abstract void setDescription(String newDescription);


  /**
   * Set a new initial price for the item
   *
   * @param newInitialPrice item initial price
   * @since 1.0
   */
  public abstract void setInitialPrice(float newInitialPrice);


  /**
   * Set a new item quantity
   *
   * @param qty item quantity
   * @since 1.0
   */
  public abstract void setQuantity(int qty);


  /**
   * Set a new reserve price for the item
   *
   * @param newReservePrice item reserve price
   * @since 1.0
   */
  public abstract void setReservePrice(float newReservePrice);


  /**
   * Set a new Buy Now price for the item
   *
   * @param newBuyNow item Buy Now price
   * @since 1.0
   */
  public abstract void setBuyNow(float newBuyNow);


  /**
   * Set item maximum bid. This function checks if newMaxBid is greater
   * than current maxBid and only updates the value in this case.
   *
   * @param newMaxBid new maximum bid
   * @since 1.1
   */
  public abstract void setMaxBid(float newMaxBid);


  /**
   * Set the number of bids for this item
   *
   * @param newNbOfBids new number of bids
   * @since 1.1
   */
  public abstract void setNbOfBids(int newNbOfBids);


  /**
   * Set a new beginning date for the auction
   *
   * @param newDate auction new beginning date
   * @since 1.0
   */
  public abstract void setStartDate(String newDate);


  /**
   * Set a new ending date for the auction
   *
   * @param newDate auction new ending date
   * @since 1.0
   */
  public abstract void setEndDate(String newDate);


  /*****************/
  /* relationships */
  /*****************/

  // This entity bean has a one to many relationship with the Bid entity.

  /**
   * Get item bids.
   *
   * @return bids of the item
   */
  public abstract Collection getBids();


  /**
   * Set item's bids.
   *
   * @param newBids new item bids
   */
  public abstract void setBids(Collection newBids);


  // This entity bean has a one to many relationship with the BuyNow entity.

  /**
   * Get item buyNows.
   *
   * @return buyNows of the item
   */
  public abstract Collection getBuyNows();


  /**
   * Set item's buyNows.
   *
   * @param newBuyNows new item buyNows
   */
  public abstract void setBuyNows(Collection newBuyNows);


  // This entity bean has one to many relationships with the Comment entity.

  /**
   * Get item comments.
   *
   * @return comments of the item
   */
  public abstract Collection getComments();


  /**
   * Set item comments.
   *
   * @param newComments new comments
   */
  public abstract void setComments(Collection newComments);


  // This entity bean has many to one relationship with the User entity.

  /**
   * Get the seller of the item
   *
   * @return seller
   * @since 1.0
   */
  public abstract UserLocal getSeller();


  /**
   * Set a new seller.
   *
   * @param seller seller
   * @since 1.0
   */
  public abstract void setSeller(UserLocal seller);


  // This entity bean has many to one relationship with the Category entity.

  /**
   * Give the category of the item
   *
   * @return item
   * @since 1.0
   */
  public abstract CategoryLocal getCategory();


  /**
   * Set a new category.
   *
   * @param category category
   * @since 1.0
   */
  public abstract void setCategory(CategoryLocal category);


  /*********************/
  /* ejbSelect methods */
  /*********************/

  /**
   * Get the first <i>maxToCollect</i> bids for an item sorted from the
   * maximum to the minimum.
   *
   * @param itemId item id
   * @param maxToCollect number of bids to collect
   *
   * @return Collection of bids
   * @exception FinderException if an error occurs
   * @since 1.0
   */
  public abstract Collection ejbSelectItemQtyMaxBid(Integer itemId, int maxToCollect) throws FinderException;

  /**
   * Get the bid history for an item sorted from the last bid to the
   * first bid (oldest one).
   *
   * @param itemId item id
   *
   * @return Collection of bids
   * @exception FinderException if an error occurs
   * @since 1.0
   */
  public abstract Collection ejbSelectItemBidHistory(Integer itemId) throws FinderException;

  /*****************/
  /* other methods */
  /*****************/

 /** 
   * Call the corresponding ejbSelect method.
   */
  public Collection getQtyMaxBid(int maxToCollect) throws FinderException
  {
      return ejbSelectItemQtyMaxBid(getId(), maxToCollect);
  }

 /** 
   * Call the corresponding ejbSelect method.
   */
  public Collection getBidHistory() throws FinderException
  {
      return ejbSelectItemBidHistory(getId());
  }

  /**
   * Get the seller's nickname by finding the Bean corresponding
   * to the user. 
   *
   * @return nickname
   * @since 1.0
   */
  public String getSellerNickname()
  {
    return getSeller().getNickName();
  }


  

  /**
   * Get the category name by finding the Bean corresponding to the category Id.
   *
   * @return category name
   * @since 1.0
   */
  public String getCategoryName()
  {
    return getCategory().getName();
  }


  /**
   * Add one bid for this item
   *
   * @since 1.1
   */
  public void addOneBid()
  {
    setNbOfBids(getNbOfBids()+1);
    isDirty = true; // the bean content has been modified
  }

  /**
   * This method is used to create a new Item Bean. Note that the item id
   * is automatically generated by the database (AUTO_INCREMENT) on the
   * primary key.
   *
   * @param itemName short item designation
   * @param itemDescription long item description, usually an HTML file
   * @param itemInitialPrice initial price fixed by the seller
   * @param itemQuantity number to sell (of this item)
   * @param itemReservePrice reserve price (minimum price the seller really wants to sell)
   * @param itemBuyNow price if a user wants to buy the item immediatly
   * @param duration duration of the auction in days (start date is when the method is called and end date is computed according to the duration)
   * @param itemSeller seller
   * @param itemCategory category
   *
   * @return pk primary key set to null
   *
   * @exception CreateException if an error occurs
   * @since 1.0
   */
  public Integer ejbCreate(String itemName, String itemDescription, float itemInitialPrice,
                          int itemQuantity, float itemReservePrice, float itemBuyNow, int duration,
                          UserLocal itemSeller, CategoryLocal itemCategory) throws CreateException
  {
    GregorianCalendar start = new GregorianCalendar();
    /*// Connecting to SB_IDManager Home interface thru JNDI
      SB_IDManagerLocalHome home = null;
      SB_IDManagerLocal idManager = null;
      
      try 
      {
        InitialContext initialContext = new InitialContext();
        home = (SB_IDManagerLocalHome)initialContext.lookup(
               "java:comp/env/ejb/SB_IDManager");
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
            setId(idManager.getNextItemID());
            break;
          }
          catch (TransactionRolledbackLocalException ex)
          {
            ex.printStackTrace();
          }
        }
        setName(itemName);
        setDescription(itemDescription);
        setInitialPrice(itemInitialPrice);
        setQuantity(itemQuantity);
        setReservePrice(itemReservePrice);
        setBuyNow(itemBuyNow);
        setNbOfBids(0);
        setMaxBid(0);
        setStartDate(TimeManagement.dateToString(start));
        setEndDate(TimeManagement.dateToString(TimeManagement.addDays(start, duration)));
      } 
     catch (Exception e)
     {
        throw new EJBException("Cannot create item: " +e);
      }
    return null;*/
    
    setName(itemName);
    setDescription(itemDescription);
    setInitialPrice(itemInitialPrice);
    setQuantity(itemQuantity);
    setReservePrice(itemReservePrice);
    setBuyNow(itemBuyNow);
    setNbOfBids(0);
    setMaxBid(0);
    setStartDate(TimeManagement.dateToString(start));
    setEndDate(TimeManagement.dateToString(TimeManagement.addDays(start, duration)));
    
    return null;
  }

  /** This method just set an internal flag to 
      reload the id generated by the DB */
  public void ejbPostCreate(String itemName, String itemDescription, float itemInitialPrice,
                          int itemQuantity, float itemReservePrice, float itemBuyNow, int duration,
                          UserLocal itemSeller, CategoryLocal itemCategory)
  {
    setSeller(itemSeller);
    setCategory(itemCategory);
    
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
   *                store the reference to the context in an instance variable. 
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
   * Display item information as an HTML table row
   *
   * @return a <code>String</code> containing HTML code
   * @since 1.0
   */
  public String printItem()
  {
    return "<TR><TD><a href=\""+BeanConfig.context+"/servlet/ViewItem?itemId="+getId()+"\">"+getName()+
      "<TD>"+getMaxBid()+
      "<TD>"+getNbOfBids()+
      "<TD>"+getEndDate()+
      "<TD><a href=\""+BeanConfig.context+"/servlet/PutBidAuth?itemId="+getId()+"\"><IMG SRC=\""+BeanConfig.context+"/bid_now.jpg\" height=22 width=90></a>\n";
  }

  /**
   * Display item information for the AboutMe servlet
   *
   * @return a <code>String</code> containing HTML code
   * @since 1.0
   */
  public String printUserBoughtItem(int qty)
  {
    return "<TR><TD><a href=\""+BeanConfig.context+"/servlet/ViewItem?itemId="+getId()+"\">"+getName()+"</a>\n"+
      "<TD>"+qty+"\n"+"<TD>"+getBuyNow()+"\n"+
      "<TD><a href=\""+BeanConfig.context+"/servlet/ViewUserInfo?userId="+getSeller().getId()+"\">"+getSellerNickname()+"</a>\n";
  }

  /**
   * Display item information for the AboutMe servlet
   *
   * @return a <code>String</code> containing HTML code (Warning last link must be completed by servlet)
   * @since 1.0
   */
  public String printItemUserHasBidOn(float bidMaxBid)
  {
    return "<TR><TD><a href=\""+BeanConfig.context+"/servlet/ViewItem?itemId="+getId()+"\">"+getName()+
      "<TD>"+getInitialPrice()+"<TD>"+getMaxBid()+"<TD>"+bidMaxBid+"<TD>"+getQuantity()+"<TD>"+getStartDate()+"<TD>"+getEndDate()+
      "<TD><a href=\""+BeanConfig.context+"/servlet/ViewUserInfo?userId="+getSeller().getId()+"\">"+getSellerNickname()+
      "<TD><a href=\""+BeanConfig.context+"/servlet/PutBid?itemId="+getId();
  }


  /**
   * Display item information as an HTML table row
   *
   * @return a <code>String</code> containing HTML code
   * @since 1.0
   */
  public String printSell()
  {
    return "<TR><TD><a href=\""+BeanConfig.context+"/servlet/ViewItem?itemId="+getId()+"\">"+getName()+
      "<TD>"+getInitialPrice()+"<TD>"+getMaxBid()+"<TD>"+getQuantity()+"<TD>"+getReservePrice()+"<TD>"+getBuyNow()+"<TD>"+getStartDate()+"<TD>"+getEndDate()+"\n";
  }

  /**
   * Display item information for the AboutMe servlet
   *
   * @return a <code>String</code> containing HTML code
   * @since 1.0
   */
  public String printUserWonItem()
  {
    return "<TR><TD><a href=\""+BeanConfig.context+"/servlet/ViewItem?itemId="+getId()+"\">"+getName()+"</a>\n"+
      "<TD>"+getMaxBid()+"\n"+
      "<TD><a href=\""+BeanConfig.context+"/servlet/ViewUserInfo?userId="+getSeller().getId()+"\">"+getSellerNickname()+"</a>\n";
  }

  /**
   * Display item information for the Buy Now servlet
   *
   * @return a <code>String</code> containing HTML code
   * @since 1.0
   */
  public String printItemDescriptionToBuyNow(int userId)
  {
    String result = "<TABLE>\n"+"<TR><TD>Quantity<TD><b><BIG>"+getQuantity()+"</BIG></b>\n"+
      "<TR><TD>Seller<TD><a href=\""+BeanConfig.context+"/servlet/ViewUserInfo?userId="+getSeller().getId()+"\">"+
      getSellerNickname()+"</a> (<a href=\""+BeanConfig.context+"/servlet/PutCommentAuth?to="+getSeller().getId()+"&itemId="+getId()+"\">Leave a comment on this user</a>)\n"+
      "<TR><TD>Started<TD>"+getStartDate()+"\n"+"<TR><TD>Ends<TD>"+getEndDate()+"\n"+
      "</TABLE>"+
      "<TABLE width=\"100%\" bgcolor=\"#CCCCFF\">\n"+
      "<TR><TD align=\"center\" width=\"100%\"><FONT size=\"4\" color=\"#000000\"><B>Item description</B></FONT></TD></TR>\n"+
      "</TABLE><p>\n"+getDescription()+"<br><p>\n"+
      "<TABLE width=\"100%\" bgcolor=\"#CCCCFF\">\n"+
      "<TR><TD align=\"center\" width=\"100%\"><FONT size=\"4\" color=\"#000000\"><B>Buy Now</B></FONT></TD></TR>\n"+
      "</TABLE><p>\n"+
      "<form action=\""+BeanConfig.context+"/servlet/StoreBuyNow\" method=POST>\n"+
      "<input type=hidden name=userId value="+userId+">\n"+
      "<input type=hidden name=itemId value="+getId()+">\n"+
      "<input type=hidden name=maxQty value="+getQuantity()+">\n";
    if (getQuantity() > 1)
      result = result + "<center><table><tr><td>Quantity:</td>\n"+
        "<td><input type=text size=5 name=qty></td></tr></table></center>\n";
    else
      result = result + "<input type=hidden name=qty value=1>\n";
    result = result + "<p><input type=submit value=\"Buy now!\"></center><p>\n";
    return result;
  }
}
