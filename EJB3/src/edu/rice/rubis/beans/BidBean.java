package edu.rice.rubis.beans;

import edu.rice.rubis.TimeManagement;
import javax.persistence.*;
import java.util.*;


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

@Entity
@Table(name="bids")
public class BidBean
{
  @Id
  @Column(name="id")
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  protected Integer id;
  @Column(name="qty")
  protected int quantity;
  @Column(name="bid")
  protected float bid;
  @Column(name="max_bid")
  protected float maxBid;
  @Column(name="date")
  protected Calendar date = new GregorianCalendar();
  @ManyToOne
  @JoinColumn(name="user_id")
  protected UserBean user;
  @ManyToOne
  @JoinColumn(name="item_id")
  protected ItemBean item;

  protected BidBean() {}

  /**
   * Get bid's id.
   *
   * @return bid id
   */
  public Integer getId() { return id; }

  /**
   * Set bid id.
   *
   * @param newId bid id
   */
  public void setId(Integer newId) { id = newId; }

  /**
   * Get how many of this item the user wants.
   *
   * @return quantity of items for this bid.
   */
  public int getQuantity() { return quantity; }

  /**
   * Set a new quantity for this bid
   *
   * @param newQuantity bid quantity
   */
  public void setQuantity(int newQuantity) { quantity = newQuantity; }

  /**
   * Get the bid of the user.
   *
   * @return user's bid
   */
  public float getBid() { return bid; }

  /**
   * Set a new bid on the item for the user.
   * <pre>
   * Warning! This method does not update the maxBid value in the items table
   * </pre>
   *
   * @param newBid a <code>float</code> value
   */
  public void setBid(float newBid) { bid = newBid; }

  /**
   * Get the maximum bid wanted by the user.
   *
   * @return user's maximum bid
   */
  public float getMaxBid() { return maxBid; }

  /**
   * Set a new maximum bid on the item for the user
   *
   * @param newMaxBid a <code>float</code> value
   */
  public void setMaxBid(float newMaxBid) { maxBid = newMaxBid; }

  /**
   * Time of the Bid in the format 'YYYY-MM-DD hh:mm:ss'
   *
   * @return bid time
   */
  public Calendar getDate() { return date; }

  /**
   * Set a new date for this bid
   *
   * @param newDate bid date
   */
  public void setDate(Calendar newDate) { date = newDate; }

  /**
   * Get the user.
   *
   * @return user
   */
  public UserBean getUser() { return user; }

  /**
   * Set a new user.
   *
   * @param newUser bid user
   */
  public void setUser(UserBean newUser) { user = newUser; }

  /**
   * Get the item.
   *
   * @return item
   */
  public ItemBean getItem() { return item; }

  /**
   * Set a new item.
   *
   * @param newItem bid item
   */
  public void setItem(ItemBean newItem) { item = newItem; }

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
   */
  public BidBean(UserBean bidUser, ItemBean bidItem, float userBid, float userMaxBid, int quantity)
  {
    this();
    
    bidItem.setMaxBid(userBid);
    bidItem.addOneBid();
    
    setBid(userBid);
    setMaxBid(userMaxBid);
    setQuantity(quantity);
    setUser(bidUser);
    setItem(bidItem);
  }

  /**
   * Display bid history information as an HTML table row
   *
   * @return a <code>String</code> containing HTML code
   * @since 1.0
   */
  public String printBidHistory()
  {
    return "<TR><TD><a href=\""+BeanConfig.context+"/servlet/ViewUserInfo?userId="+getUser().getId()+
      "\">"+getBidderNickName()+"<TD>"+getBid()+"<TD>"+TimeManagement.dateToString(getDate())+"\n";
  }
}
