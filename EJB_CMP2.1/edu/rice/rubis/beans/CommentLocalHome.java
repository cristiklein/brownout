package edu.rice.rubis.beans;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

/** This is the Local Home interface of the Comment Bean */

public interface CommentLocalHome extends EJBLocalHome {
  /**
   * This method is used to create a new Comment Bean. 
   * The date is automatically set to the current date when the method is called.
   *
   * @param fromUser comment author
   * @param toUser user this comment is about
   * @param item item
   * @param rating rating given by the author
   * @param comment comment text
   *
   * @return pk primary key set to null
   * @exception CreateException if an error occurs
   */
  public CommentLocal create(UserLocal fromUser, UserLocal toUser, ItemLocal item, int rating, String comment) throws CreateException;

  /**
   * This method is used to retrieve a Comment Bean from its primary key,
   * that is to say its id.
   *
   * @param id Comment id (primary key)
   *
   * @return the Comment if found else null
   */
  public CommentLocal findByPrimaryKey(Integer id) throws FinderException;

  /**
   * This method is used to retrieve all comments from the database!
   *
   * @return List of all comments (eventually empty)
   */
  public Collection findAllComments() throws FinderException;
}
