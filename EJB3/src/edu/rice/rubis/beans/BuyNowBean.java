package edu.rice.rubis.beans;

import edu.rice.rubis.TimeManagement;
import javax.persistence.*;
import java.util.*;

/**
 * BuyNowBean is an entity bean with "container managed persistence". 
 * The state of an instance is stored into a relational database. 
 * The following table should exist:<p>
 * <pre>
 * CREATE TABLE buy_now (
 *   id       INTEGER UNSIGNED NOT NULL UNIQUE,
 *   buyer_id INTEGER UNSIGNED NOT NULL,
 *   item_id  INTEGER UNSIGNED NOT NULL,
 *   qty      INTEGER,
 *   date     DATETIME,
 *   PRIMARY KEY(id),
 *   INDEX buyer (buyer_id),
 *   INDEX item (item_id)
 * );
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
@Entity
@Table(name="buy_now")
public class BuyNowBean
{
  @Id
  @Column(name="id")
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  protected Integer id;
  @Column(name="qty")
  protected int quantity;
  @Column(name="date")
  protected Calendar date = new GregorianCalendar();
  @ManyToOne
  @JoinColumn(name="buyer_id")
  protected UserBean buyer;
  @ManyToOne
  @JoinColumn(name="item_id")
  protected ItemBean item;

  protected BuyNowBean() {}

  /**
   * Get BuyNow id.
   *
   * @return BuyNow id
   */
  public Integer getId() { return id; }

  /**
   * Set buyNow id.
   *
   * @param newId BuyNow id
   */
  public void setId(Integer newId) { id = newId; }

  /**
   * Get how many of this item the user has bought.
   *
   * @return quantity of items for this BuyNow.
   */
  public int getQuantity() { return quantity; }

  /**
   * Set a new quantity for this BuyNow
   *
   * @param newQuantity BuyNow quantity
   */
  public void setQuantity(int newQuantity) { quantity = newQuantity; }

  /**
   * Time of the BuyNow in the format 'YYYY-MM-DD hh:mm:ss'
   *
   * @return BuyNow time
   */
  public Calendar getDate() { return date; }

  /**
   * Set a new date for this BuyNow
   *
   * @param newDate BuyNow date
   */
  public void setDate(Calendar newDate) { date = newDate; }

  /**
   * Get the buyer.
   *
   * @return buyer
   */
  public UserBean getBuyer() { return buyer; }

  /**
   * Set a new buyer.
   *
   * @param newBuyer BuyNow buyer
   */
  public void setBuyer(UserBean newBuyer) { buyer = newBuyer; }

  /**
   * Get the item.
   *
   * @return item
   */
  public ItemBean getItem() { return item; }

  /**
   * Set a new item.
   *
   * @param newItem BuyNow item
   */
  public void setItem(ItemBean newItem) { item = newItem; }

  /**
   * This method is used to create a new BuyNow Bean.
   * The date is automatically set to the current date when the method is called.
   *
   * @param buyNowUser buyer
   * @param buyNowItem item
   * @param quantity number of items the user wants to buy
   */
  public BuyNowBean(UserBean buyNowUser, ItemBean buyNowItem, int quantity)
  {
    this();
    
    setQuantity(quantity);
    setBuyer(buyNowUser);
    setItem(buyNowItem);
  }
}
