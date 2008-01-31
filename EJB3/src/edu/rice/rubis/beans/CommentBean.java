package edu.rice.rubis.beans;

import edu.rice.rubis.TimeManagement;
import javax.persistence.*;
import java.util.*;

/**
 * BidBean is an entity bean with "container managed persistence". 
 * The state of an instance is stored into a relational database. 
 * The following table should exist:<p>
 * <pre>
 * CREATE TABLE comments (
 *   id           INTEGER UNSIGNED NOT NULL UNIQUE,
 *   from_user_id INTEGER,
 *   to_user_id   INTEGER,
 *   item_id      INTEGER,
 *   rating       INTEGER,
 *   date         DATETIME,
 *   comment      TEXT
 *   PRIMARY KEY(id),
 *   INDEX from_user (from_user_id),
 *   INDEX to_user (to_user_id),
 *   INDEX item (item_id)
 * );
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
@Entity
@Table(name="comments")
public class CommentBean
{
  @Id
  @Column(name="id")
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  protected Integer id;
  @Column(name="rating")
  protected int rating;
  @Column(name="date")
  protected Calendar date = new GregorianCalendar();
  @Column(name="comment")
  protected String comment;
  @ManyToOne
  @JoinColumn(name="from_user_id")
  protected UserBean fromUser;
  @ManyToOne
  @JoinColumn(name="to_user_id")
  protected UserBean toUser;
  @ManyToOne
  @JoinColumn(name="item_id")
  protected ItemBean item;

  protected CommentBean() {}

  /**
   * Get comment's id.
   *
   * @return comment id
   */
  public Integer getId() { return id; }

  /**
   * Set comment id.
   *
   * @param newId comment id
   */
  public void setId(Integer newId) { id = newId; }

  /**
   * Get the rating associated to this comment.
   *
   * @return rating
   */
  public int getRating() { return rating; }

  /**
   * Set a new rating for the ToUserId.
   *
   * @param newRating an <code>int</code> value
   */
  public void setRating(int newRating) { rating = newRating; }

  /**
   * Time of the Comment in the format 'YYYY-MM-DD hh:mm:ss'
   *
   * @return comment time
   */
  public Calendar getDate() { return date; }

  /**
   * Set a new date for this comment
   *
   * @param newDate comment date
   */
  public void setDate(Calendar newDate) { date = newDate; }

  /**
   * Get the comment text.
   *
   * @return comment text
   */
  public String getComment() { return comment; }

  /**
   * Set a new comment for ToUserId from FromUserId.
   *
   * @param newComment Comment
   */
  public void setComment(String newComment) { comment = newComment; }

  /**
   * Get the author of the comment
   *
   * @return author
   */
  public UserBean getFromUser() { return fromUser; }

  /**
   * Set a new author of the comment.
   *
   * @param newFromUser author
   */
  public void setFromUser(UserBean newFromUser) { fromUser = newFromUser; }

  /**
   * Get the user this comment is about.
   *
   * @return user this comment is about
   */
  public UserBean getToUser() { return toUser; }


  /**
   * Set a new user this comment is about.
   *
   * @param newToUser user this comment is about
   */
  public void setToUser(UserBean newToUser) { toUser = newToUser; }

  /**
   * Get the item.
   *
   * @return item
   */
  public ItemBean getItem() { return item; }

  /**
   * Set a new item.
   *
   * @param newItem item
   */
  public void setItem(ItemBean newItem) { item = newItem; }


  /**
   * This method is used to create a new Comment Bean. 
   * The date is automatically set to the current date when the method is called.
   *
   * @param fromUser comment author
   * @param toUser user this comment is about
   * @param item item
   * @param rating rating given by the author
   * @param comment comment text
   */
  public CommentBean(UserBean fromUser, UserBean toUser, ItemBean item, int rating, String comment)
  {
    this();
    
    setRating(rating);
    setComment(comment);
    setFromUser(fromUser);
    setToUser(toUser);
    setItem(item);
  }

  /**
   * Display comment information as an HTML table row
   *
   * @return a <code>String</code> containing HTML code
   * @since 1.0
   */
  public String printComment(String userName)
  {
    return "<DT><b><BIG><a href=\""+BeanConfig.context+"/servlet/ViewUserInfo?userId="+getFromUser().getId()+"\">"+userName+"</a></BIG></b>"+
      " wrote the "+TimeManagement.dateToString(getDate())+"<DD><i>"+getComment()+"</i><p>\n";
  }
}
