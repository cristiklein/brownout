package edu.rice.rubis.beans;

import javax.ejb.*;
import java.rmi.*;

/**
 * This is the Local Interface for the Comment Bean
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public interface CommentLocal extends EJBLocalObject {
  /**
   * Get comment's id.
   *
   * @return comment id
   */
  public Integer getId();

  /**
   * Get the author of the comment
   *
   * @return author
   */
  public UserLocal getFromUser();

  /**
   * Get the user this comment is about.
   *
   * @return user this comment is about
   */
  public UserLocal getToUser();

  /**
   * Get the item.
   *
   * @return item
   */
  public ItemLocal getItem();

  /**
   * Get the rating associated to this comment.
   *
   * @return rating
   */
  public int getRating();

  /**
   * Time of the Comment in the format 'YYYY-MM-DD hh:mm:ss'
   *
   * @return comment time
   */
  public String getDate();
  
  /**
   * Get the comment text.
   *
   * @return comment text
   */
  public String getComment();


  /**
   * Set a new author of the comment.
   *
   * @param newFromUser author
   */
  public void setFromUser(UserLocal newFromUser);

  /**
   * Set a new user this comment is about.
   *
   * @param newToUser user this comment is about
   */
  public void setToUser(UserLocal newToUser);

  /**
   * Set a new item.
   *
   * @param newItem item
   */
  public void setItem(ItemLocal newItem);

  /**
   * Set a new rating for the ToUserId.
   *
   * @param rating maximum comment price
   */
  public void setRating(int rating);

  /**
   * Set a new date for this comment
   *
   * @param newDate comment date
   */
  public void setDate(String newDate);

  /**
   * Set a new comment for ToUserId from FromUserId.
   *
   * @param newComment Comment
   */
  public void setComment(String newComment);

  /**
   * Display comment information as an HTML table row
   *
   * @return a <code>String</code> containing HTML code
   * @since 1.0
   */
  public String printComment(String userName);

}
