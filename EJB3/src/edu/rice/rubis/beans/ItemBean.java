package edu.rice.rubis.beans;

import edu.rice.rubis.TimeManagement;
import java.util.*;
import javax.persistence.*;

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
@Entity
@Table(name="items")
@NamedQueries
({
  @NamedQuery(name="itemBidHistory", query="select b from ItemBean i left join i.bids b where i = ?1 order by b.date desc"),
  @NamedQuery(name="itemQtyMaxBid", query="select b from ItemBean i left join i.bids b where i = ?1 order by b.bid desc")
})
public class ItemBean
{
  @Id
  @Column(name="id")
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  protected Integer id;
  @Column(name="name")
  protected String name;
  @Column(name="description")
  protected String description;
  @Column(name="initial_price")
  protected float initialPrice;
  @Column(name="quantity")
  protected int quantity;
  @Column(name="reserve_price")
  protected float reservePrice;
  @Column(name="buy_now")
  protected float buyNow;
  @Column(name="max_bid")
  protected float maxBid;
  @Column(name="nb_of_bids")
  protected int nbOfBids;
  @Column(name="start_date")
  protected Calendar startDate;
  @Column(name="end_date")
  protected Calendar endDate;
  @OneToMany(mappedBy="item")
  protected Collection<BidBean> bids = new ArrayList<BidBean>();
  @OneToMany(mappedBy="item")
  protected Collection<BuyNowBean> buyNows = new ArrayList<BuyNowBean>();
  @OneToMany(mappedBy="item")
  protected Collection<CommentBean> comments = new ArrayList<CommentBean>();
  @ManyToOne
  @JoinColumn(name="seller")
  protected UserBean seller;
  @ManyToOne
  @JoinColumn(name="category")
  protected CategoryBean category;

  protected ItemBean() {}

  /**
   * Get item id.
   *
   * @return item id
   */
  public Integer getId() { return id; }

  /**
   * Set item id.
   *
   * @param newId item id
   */
  public void setId(Integer newId) { id = newId; }

  /**
   * Get item name. This description is usually a short description of the item.
   *
   * @return item name
   */
  public String getName() { return name; }

  /**
   * Set a new item name
   *
   * @param newName item name
   */
  public void setName(String newName) { name = newName; }


  /**
   * Get item description . This is usually an HTML file describing the item.
   *
   * @return item description
   */
  public String getDescription() { return description; }

  /**
   * Set a new item description
   *
   * @param newDescription item description
   */
  public void setDescription(String newDescription) { description = newDescription; }

  /**
   * Get item initial price set by the seller.
   *
   * @return item initial price
   */
  public float getInitialPrice() { return initialPrice; }

  /**
   * Set a new initial price for the item
   *
   * @param newInitialPrice item initial price
   */
  public void setInitialPrice(float newInitialPrice) { initialPrice = newInitialPrice; }

  /**
   * Get how many of this item are to be sold.
   *
   * @return item quantity
   */
  public int getQuantity() { return quantity; }

  /**
   * Set a new item quantity
   *
   * @param newQuantity item quantity
   */
  public void setQuantity(int newQuantity) { quantity = newQuantity; }

  /**
   * Get item reserve price set by the seller. The seller can refuse to sell if reserve price is not reached.
   *
   * @return item reserve price
   */
  public float getReservePrice() { return reservePrice; }

  /**
   * Set a new reserve price for the item
   *
   * @param newReservePrice item reserve price
   */
  public void setReservePrice(float newReservePrice) { reservePrice = newReservePrice; }

  /**
   * Get item Buy Now price set by the seller. A user can directly by the item at this price (no auction).
   *
   * @return item Buy Now price
   */
  public float getBuyNow() { return buyNow; }

  /**
   * Set a new Buy Now price for the item
   *
   * @param newBuyNow item Buy Now price
   */
  public void setBuyNow(float newBuyNow) { buyNow = newBuyNow; }

  /**
   * Get item maximum bid (if any) for this item.
   *
   * @return current maximum bid or 0 if no bid
   */
  public float getMaxBid() { return maxBid; }

  /**
   * Set item maximum bid.
   *
   * @param newMaxBid new maximum bid
   */
  public void setMaxBid(float newMaxBid) { maxBid = newMaxBid; }

  /**
   * Get number of bids for this item.
   *
   * @return number of bids
   */
  public int getNbOfBids() { return nbOfBids; }

  /**
   * Set the number of bids for this item
   *
   * @param newNbOfBids new number of bids
   */
  public void setNbOfBids(int newNbOfBids) { nbOfBids = newNbOfBids; }

  /**
   * Start date of the auction in the format 'YYYY-MM-DD hh:mm:ss'
   *
   * @return start date of the auction
   */
  public Calendar getStartDate() { return startDate; }

  /**
   * Set a new beginning date for the auction
   *
   * @param newDate auction new beginning date
   */
  public void setStartDate(Calendar newDate) { startDate = newDate; }

  /**
   * End date of the auction in the format 'YYYY-MM-DD hh:mm:ss'
   *
   * @return end date of the auction
   */
  public Calendar getEndDate() { return endDate; }

  /**
   * Set a new ending date for the auction
   *
   * @param newDate auction new ending date
   */
  public void setEndDate(Calendar newDate) { endDate = newDate; }

  /**
   * Get item bids.
   *
   * @return bids of the item
   */
  public Collection<BidBean> getBids() { return bids; }

  /**
   * Set item's bids.
   *
   * @param newBids new item bids
   */
  public void setBids(Collection<BidBean>  newBids) { bids = newBids; }

  /**
   * Get item buyNows.
   *
   * @return buyNows of the item
   */
  public Collection<BuyNowBean> getBuyNows() { return buyNows; }

  /**
   * Set item's buyNows.
   *
   * @param newBuyNows new item buyNows
   */
  public void setBuyNows(Collection<BuyNowBean> newBuyNows) { buyNows = newBuyNows; }

  /**
   * Get item comments.
   *
   * @return comments of the item
   */
  public Collection<CommentBean> getComments() { return comments; }

  /**
   * Set item comments.
   *
   * @param newComments new comments
   */
  public void setComments(Collection<CommentBean> newComments) { comments = newComments; }

  /**
   * Get the seller of the item
   *
   * @return seller
   */
  public UserBean getSeller() { return seller; }

  /**
   * Set a new seller.
   *
   * @param newSeller seller
   */
  public void setSeller(UserBean newSeller) { seller = newSeller; }

  /**
   * Give the category of the item
   *
   * @return category
   */
  public CategoryBean getCategory() { return category; }

  /**
   * Set a new category.
   *
   * @param newCategory category
   */
  public void setCategory(CategoryBean newCategory) { category = newCategory; }

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
  }

  /**
   * This method is used to create a new Item Bean.
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
   */
  public ItemBean(String itemName, String itemDescription, float itemInitialPrice,
                          int itemQuantity, float itemReservePrice, float itemBuyNow, int duration,
                          UserBean itemSeller, CategoryBean itemCategory)
  {
    GregorianCalendar start = new GregorianCalendar();
    GregorianCalendar end = TimeManagement.addDays(start, duration);
    
    setName(itemName);
    setDescription(itemDescription);
    setInitialPrice(itemInitialPrice);
    setQuantity(itemQuantity);
    setReservePrice(itemReservePrice);
    setBuyNow(itemBuyNow);
    setNbOfBids(0);
    setMaxBid(0);
    setStartDate(start);
    setEndDate(end);
    setSeller(itemSeller);
    setCategory(itemCategory);
  }

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
      "<TD>"+TimeManagement.dateToString(getEndDate())+
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
      "<TD>"+getInitialPrice()+"<TD>"+getMaxBid()+"<TD>"+bidMaxBid+"<TD>"+getQuantity()+"<TD>"+TimeManagement.dateToString(getStartDate())+"<TD>"+TimeManagement.dateToString(getEndDate())+
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
      "<TD>"+getInitialPrice()+"<TD>"+getMaxBid()+"<TD>"+getQuantity()+"<TD>"+getReservePrice()+"<TD>"+getBuyNow()+"<TD>"+TimeManagement.dateToString(getStartDate())+"<TD>"+TimeManagement.dateToString(getEndDate())+"\n";
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
      "<TR><TD>Started<TD>"+TimeManagement.dateToString(getStartDate())+"\n"+"<TR><TD>Ends<TD>"+TimeManagement.dateToString(getEndDate())+"\n"+
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
