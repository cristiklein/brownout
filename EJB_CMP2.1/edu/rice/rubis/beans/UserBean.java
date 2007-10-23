package edu.rice.rubis.beans;

import java.rmi.*;
import javax.ejb.*;
import javax.rmi.PortableRemoteObject;
import javax.naming.InitialContext;
import java.util.Collection;
import java.util.Date;

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

public abstract class UserBean implements EntityBean 
{
  private EntityContext entityContext;
  private transient boolean isDirty; // used for the isModified function

  /****************************/
  /* Abstract accessor methods*/
  /****************************/

  /**
   * Get user's id.
   *
   * @return user id
   */
  public abstract Integer getId();


  /**
   * Set user's id.
   *
   */
  public abstract void setId(Integer id);


  /**
   * Get user first name.
   *
   * @return user first name
   */
  public abstract String getFirstName();


  /**
   * Get user last name.
   *
   * @return user last name
   */
  public abstract String getLastName();


  /**
   * Get user nick name. This name is unique for each user and is used for login.
   *
   * @return user nick name
   */
  public abstract String getNickName();


  /**
   * Get user password.
   *
   * @return user password
   */
  public abstract String getPassword();


  /**
   * Get user email address.
   *
   * @return user email address
   */
  public abstract String getEmail();
 

  /**
   * Get user rating. The higher the rating is, the most reliable the user is.
   *
   * @return user rating
   */
  public abstract int getRating();

  
  /**
   * Get user's account current balance. This account is used when a user want to sell items.
   * There is a charge for each item to sell.
   *
   * @return user's account current balance
   */
  public abstract float getBalance();


  /**
   * Get user creation date.
   *
   * @return user creation date
   */
  public abstract String getCreationDate();


  /**
   * Set user's first name
   *
   * @param newName user first name
   */
  public abstract void setFirstName(String newName);


  /**
   * Set user's last name
   *
   * @param newName user last name
   */
  public abstract void setLastName(String newName);


  /**
   * Set user's nick name
   *
   * @param newName user nick name
   */
  public abstract void setNickName(String newName);


  /**
   * Set user's password
   *
   * @param newPassword a <code>String</code> value
   */
  public abstract void setPassword(String newPassword);


  /**
   * Set user's email address
   *
   * @param newEmail a <code>String</code> value
   */
  public abstract void setEmail(String newEmail);


  /**
   * Set a new creation date for this user account
   *
   * @param newCreationDate a <code>String</code> value
   */
  public abstract void setCreationDate(String newCreationDate);


  /**
   * Set user rating. The higher the rating is, the most reliable the user is.
   *
   * @param newRating new user rating
   */
  public abstract void setRating(int newRating);


  /**
   * Set user's account current balance. This account is used when a user want to sell items.
   * There is a charge for each sold item.
   *
   * @param newBalance set user's account current balance
   */
  public abstract void setBalance(float newBalance);


  /**
   * Update the current rating by adding a new value to it. This value can
   * be negative if someone wants to decrease the user rating.
   *
   * @param diff value to add to the rating
   */
  public void updateRating(int diff)
  {
    setRating(getRating()+diff);
    isDirty = true; // the bean content has been modified
  }


  /*****************/
  /* relationships */
  /*****************/

  // This entity bean has a one to many relationship with the Item entity.

  /**
   * Get user items.
   *
   * @return items of the user
   */
  public abstract Collection getItems();


  /**
   * Set user's items.
   *
   * @param newItems new user items
   */
  public abstract void setItems(Collection newItems);


  // This entity bean has a one to many relationship with the bid entity.

  /**
   * Get user bids.
   *
   * @return bids of the user
   */
  public abstract Collection getBids();


  /**
   * Set user's bids.
   *
   * @param newBids new user bids
   */
  public abstract void setBids(Collection newBids);


  // This entity bean has a one to many relationship with the buyNow entity.

  /**
   * Get user buyNows.
   *
   * @return buyNows of the user
   */
  public abstract Collection getBuyNows();


  /**
   * Set user's buyNows.
   *
   * @param newBuyNows new user buyNows
   */
  public abstract void setBuyNows(Collection newBuyNows);


  // This entity bean has a many to one relationship with the Region entity.

  /**
   * Get user region.
   *
   * @return region of the user
   */
  public abstract RegionLocal getRegion();


  /**
   * Set user's region
   *
   * @param region region
   */
  public abstract void setRegion(RegionLocal newRegion);


  // This entity bean has one to many relationships with the Comment entity.

  /**
   * Get user fromComments.
   *
   * @return fromComments of the user
   */
  public abstract Collection getFromComments();


  /**
   * Set user fromComments.
   *
   * @param newFromComments new fromComments
   */
  public abstract void setFromComments(Collection newFromComments);


  /**
   * Get user toComments.
   *
   * @return toComments of the user
   */
  public abstract Collection getToComments();


  /**
   * Set user toComments.
   *
   * @param newToComments new toComments
   */
  public abstract void setToComments(Collection newToComments);


  /*********************/
  /* ejbSelect methods */
  /*********************/

  /**
   * Get all the items the user won recently.
   *
   * @param userId user id
   * @param now current date
   * @param oldest oldest date
   *
   * @return Collection of items
   * @exception FinderException if an error occurs
   * @since 1.0
   */
  public abstract Collection ejbSelectUserWonItems(Integer userId, Date now, Date oldest) throws FinderException;

  /**
   * Get the active items the user had bid on.
   *
   * @param userId user id
   * @param now current date
   *
   * @return Collection of items
   * @exception FinderException if an error occurs
   */
  public abstract Collection ejbSelectUserBidItems(Integer userId, Date now) throws FinderException;

  /**
   * Get the max bid for an item the user had bid on.
   *
   * @param userId user id
   * @param now current date
   *
   * @return Max bid
   * @exception FinderException if an error occurs
   */
  public abstract float ejbSelectUserMaxBid(Integer userId, Integer itemId) throws FinderException;

  /**
   * Get all the items the user is currently selling.
   *
   * @param userId user id
   * @param now current date
   *
   * @return Collection of items
   * @exception FinderException if an error occurs
   */
  public abstract Collection ejbSelectUserCurrentSellings(Integer userId, Date now) throws FinderException;

  /**
   * Get all the items the user sold recently.
   *
   * @param userId user id
   * @param now current date
   * @param oldest oldest date
   *
   * @return Collection of items
   * @exception FinderException if an error occurs
   */
  public abstract Collection ejbSelectUserPastSellings(Integer userId, Date now, Date oldest) throws FinderException;

  /**
   * Get all the buyNow the user bought recently.
   *
   * @param userId user id
   * @param oldest oldest date
   *
   * @return Collection of buyNows
   * @exception FinderException if an error occurs
   */
  public abstract Collection ejbSelectUserBuyNow(Integer userId, Date oldest) throws FinderException;

  /*****************/
  /* other methods */
  /*****************/

 /** 
   * Call the corresponding ejbSelect method.
   */
  public Collection getWonItems(int days) throws FinderException
  {
      Date now = new Date();
      Date oldest = new Date(now.getTime() - ((long)days)*24L*60L*60L*1000L);
      
      return ejbSelectUserWonItems(getId(), now, oldest);
  }

 /** 
   * Call the corresponding ejbSelect method.
   */
  public Collection getBidItems() throws FinderException
  {
      Date now = new Date();
      
      return ejbSelectUserBidItems(getId(), now);
  }

 /** 
   * Call the corresponding ejbSelect method.
   */
  public float getMaxBid(ItemLocal item) throws FinderException
  {
      return ejbSelectUserMaxBid(getId(), item.getId());
  }

 /** 
   * Call the corresponding ejbSelect method.
   */
  public Collection getCurrentSellings() throws FinderException
  {
      Date now = new Date();
      
      return ejbSelectUserCurrentSellings(getId(), now);
  }

  /** 
   * Call the corresponding ejbSelect method.
   */
  public Collection getPastSellings(int days) throws FinderException
  {
      Date now = new Date();
      Date oldest = new Date(now.getTime() - ((long)days)*24L*60L*60L*1000L);
      
      return ejbSelectUserPastSellings(getId(), now, oldest);
  }

  /** 
   * Call the corresponding ejbSelect method.
   */
  public Collection getBuyNow(int days) throws FinderException
  {
      Date now = new Date();
      Date oldest = new Date(now.getTime() - ((long)days)*24L*60L*60L*1000L);
      
      return ejbSelectUserBuyNow(getId(), oldest);
  }

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
    result = result+"User since     : "+getCreationDate()+"<br>";
    result = result+"Current rating : <b>"+getRating()+"</b><br>";
    return result;
  }


  // =============================== EJB methods ===================================

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
   *
   * @return pk primary key set to null
   *
   * @exception CreateException if an error occurs
   */
  public Integer ejbCreate(String userFirstName, String userLastName, String userNickName, String userEmail, 
                          String userPassword, RegionLocal userRegion) throws CreateException
  {
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
            setId(idManager.getNextUserID());
            break;
          }
          catch (TransactionRolledbackLocalException ex)
          {
            ex.printStackTrace();
          }
        }
       setFirstName(userFirstName);
       setLastName(userLastName);
       setNickName(userNickName);
       setPassword(userPassword);
       setEmail(userEmail);
       setCreationDate(TimeManagement.currentDateToString());
     } 
      catch (Exception e)
      {
        throw new EJBException("Cannot create user: " +e);
      }
    return null;*/
    
    setFirstName(userFirstName);
    setLastName(userLastName);
    setNickName(userNickName);
    setPassword(userPassword);
    setEmail(userEmail);
    setCreationDate(TimeManagement.currentDateToString());
    
    return null;
  }


  /** This method just set an internal flag to 
      reload the id generated by the DB */
  public void ejbPostCreate(String userFirstName, String userLastName, String userNickName, String userEmail, 
                            String userPassword, RegionLocal userRegion)
  {
    setRegion(userRegion);
    
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

}
