package edu.rice.rubis.beans;

import edu.rice.rubis.TimeManagement;
import java.util.*;
import javax.persistence.*;
import javax.persistence.NamedQuery;

/**
 * UserBean is an entity bean with "container managed persistence". 
 * The state of an instance is stored into a relational database. 
 * The following table should exist:<p>
 * <pre>
 * CREATE TABLE users (
 *    id            INTEGER UNSIGNED NOT NULL UNIQUE,
 *    firstname     VARCHAR(20),
 *    lastname      VARCHAR(20),
 *    nickname      VARCHAR(20) NOT NULL UNIQUE,
 *    password      VARCHAR(20) NOT NULL,
 *    email         VARCHAR(50) NOT NULL,
 *    rating        INTEGER,
 *    balance       FLOAT,
 *    creation_date DATETIME,
 *    region        INTEGER,
 *    PRIMARY KEY(id),
 *    INDEX auth (nickname,password),
 *    INDEX region_id (region)
 * );
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
@Entity
@Table(name="users")
@NamedQueries
({
  @NamedQuery(name="userByNickName", query="select u from UserBean u where u.nickName = ?1"),
  @NamedQuery(name="userWonItems", query="select b.item from UserBean u left join u.bids b where u = ?1 and b.item.endDate < ?2 and b.item.endDate > ?3 and b.bid = b.item.maxBid"),
  @NamedQuery(name="userBidItems", query="select distinct b.item from UserBean u left join u.bids b where u = ?1 and b.item.endDate > ?2"),
  @NamedQuery(name="userMaxBid", query="select max(b.maxBid) from UserBean u left join u.bids b where u = ?1 and b.item = ?2"),
  @NamedQuery(name="userCurrentSellings", query="select i from UserBean u left join u.items i where u = ?1 and i.endDate > ?2"),
  @NamedQuery(name="userPastSellings", query="select i from UserBean u left join u.items i where u = ?1 and i.endDate < ?2 and i.endDate > ?3"),
  @NamedQuery(name="userBuyNow", query="select b from UserBean u left join u.buyNows b where u = ?1 and b.date > ?2")
})
public class UserBean
{
  @Id
  @Column(name="id")
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  protected Integer id;
  @Column(name="firstname")
  protected String firstName;
  @Column(name="lastname")
  protected String lastName;
  @Column(name="nickname")
  protected String nickName;
  @Column(name="password")
  protected String password;
  @Column(name="email")
  protected String email;
  @Column(name="rating")
  protected int rating;
  @Column(name="balance")
  protected float balance;
  @Column(name="creation_date")
  protected Calendar creationDate = new GregorianCalendar();
  @ManyToOne
  @JoinColumn(name="region")
  protected RegionBean region;
  @OneToMany(mappedBy="seller")
  protected Collection<ItemBean> items = new ArrayList<ItemBean>();
  @OneToMany(mappedBy="user")
  protected Collection<BidBean> bids = new ArrayList<BidBean>();
  @OneToMany(mappedBy="buyer")
  protected Collection<BuyNowBean> buyNows = new ArrayList<BuyNowBean>();
  @OneToMany(mappedBy="fromUser")
  protected Collection<CommentBean> fromComments = new ArrayList<CommentBean>();
  @OneToMany(mappedBy="toUser")
  protected Collection<CommentBean> toComments = new ArrayList<CommentBean>();

  protected UserBean() {}

  /**
   * Get user's id.
   *
   * @return user id
   */
  public Integer getId() { return id; }

  /**
   * Set user's id.
   *
   * @param newId user id
   */
  public void setId(Integer newId) { id = newId; }

  /**
   * Get user first name.
   *
   * @return user first name
   */
  public String getFirstName() { return firstName; }

  /**
   * Set user's first name
   *
   * @param newName user first name
   */
  public void setFirstName(String newName) { firstName = newName; }

  /**
   * Get user last name.
   *
   * @return user last name
   */
  public String getLastName() { return lastName; }

  /**
   * Set user's last name
   *
   * @param newName user last name
   */
  public void setLastName(String newName) { lastName = newName; }

  /**
   * Get user nick name. This name is unique for each user and is used for login.
   *
   * @return user nick name
   */
  public String getNickName() { return nickName; }

  /**
   * Set user's nick name
   *
   * @param newName user nick name
   */
  public void setNickName(String newName) { nickName = newName; }

  /**
   * Get user password.
   *
   * @return user password
   */
  public String getPassword() { return password; }

  /**
   * Set user's password
   *
   * @param newPassword a <code>String</code> value
   */
  public void setPassword(String newPassword) { password = newPassword; }

  /**
   * Get user email address.
   *
   * @return user email address
   */
  public String getEmail() { return email; }

  /**
   * Set user's email address
   *
   * @param newEmail a <code>String</code> value
   */
  public void setEmail(String newEmail) { email = newEmail; }

  /**
   * Get user rating. The higher the rating is, the most reliable the user is.
   *
   * @return user rating
   */
  public int getRating() { return rating; }

  /**
   * Set user rating. The higher the rating is, the most reliable the user is.
   *
   * @param newRating new user rating
   */
  public void setRating(int newRating) { rating = newRating; }

  /**
   * Get user's account current balance. This account is used when a user want to sell items.
   * There is a charge for each item to sell.
   *
   * @return user's account current balance
   */
  public float getBalance() { return balance; }

  /**
   * Set user's account current balance. This account is used when a user want to sell items.
   * There is a charge for each sold item.
   *
   * @param newBalance set user's account current balance
   */
  public void setBalance(float newBalance) { balance = newBalance; }

  /**
   * Get user creation date.
   *
   * @return user creation date
   */
  public Calendar getCreationDate() { return creationDate; }

  /**
   * Set a new creation date for this user account
   *
   * @param newCreationDate a <code>String</code> value
   */
  public void setCreationDate(Calendar newCreationDate) { creationDate = newCreationDate; }

  /**
   * Update the current rating by adding a new value to it. This value can
   * be negative if someone wants to decrease the user rating.
   *
   * @param diff value to add to the rating
   */
  public void updateRating(int diff)
  {
    setRating(getRating()+diff);
  }

  /**
   * Get user region.
   *
   * @return region of the user
   */
  public RegionBean getRegion() { return region; }

  /**
   * Set user's region
   *
   * @param region region
   */
  public void setRegion(RegionBean newRegion) { region = newRegion; }

  /**
   * Get user items.
   *
   * @return items of the user
   */
  public Collection<ItemBean> getItems() { return items; }

  /**
   * Set user's items.
   *
   * @param newItems new user items
   */
  public void setItems(Collection<ItemBean> newItems) { items = newItems; }

  /**
   * Get user bids.
   *
   * @return bids of the user
   */
  public Collection<BidBean> getBids() { return bids; }

  /**
   * Set user's bids.
   *
   * @param newBids new user bids
   */
  public void setBids(Collection<BidBean> newBids) { bids = newBids; }

  /**
   * Get user buyNows.
   *
   * @return buyNows of the user
   */
  public Collection<BuyNowBean> getBuyNows() { return buyNows; }

  /**
   * Set user's buyNows.
   *
   * @param newBuyNows new user buyNows
   */
  public void setBuyNows(Collection<BuyNowBean> newBuyNows) { buyNows = newBuyNows; }

  /**
   * Get user fromComments.
   *
   * @return fromComments of the user
   */
  public Collection<CommentBean> getFromComments() { return fromComments; }

  /**
   * Set user fromComments.
   *
   * @param newFromComments new fromComments
   */
  public void setFromComments(Collection<CommentBean> newFromComments) { fromComments = newFromComments; }

  /**
   * Get user toComments.
   *
   * @return toComments of the user
   */
  public Collection<CommentBean> getToComments() { return toComments; }

  /**
   * Set user toComments.
   *
   * @param newToComments new toComments
   */
  public void setToComments(Collection<CommentBean> newToComments) { toComments = newToComments; }

  /**
   * Returns a string displaying general information about the user.
   * The string contains HTML tags.
   *
   * @return string containing general user information
   */
  public String getHTMLGeneralUserInformation()
  {
    String result = new String();

    result = result+"<h2>Information about "+getNickName()+"<br></h2>";
    result = result+"Real life name : "+getFirstName()+" "+getLastName()+"<br>";
    result = result+"Email address  : "+getEmail()+"<br>";
    result = result+"User since     : "+TimeManagement.dateToString(getCreationDate())+"<br>";
    result = result+"Current rating : <b>"+getRating()+"</b><br>";
    return result;
  }

  /**
   * This method is used to create a new User Bean. The user id and the creationDate
   * are automatically set by the system.
   *
   * @param userFirstName user's first name
   * @param userLastName user's last name
   * @param userNickName user's nick name
   * @param userEmail email address of the user
   * @param userPassword user's password
   * @param userRegion region where the user lives
   */
  public UserBean(String userFirstName, String userLastName, String userNickName, String userEmail, 
                          String userPassword, RegionBean userRegion)
  {
    setFirstName(userFirstName);
    setLastName(userLastName);
    setNickName(userNickName);
    setPassword(userPassword);
    setEmail(userEmail);
    setRegion(userRegion);
  }
}
