package edu.rice.rubis.servlets;

import java.io.IOException;

import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.HashSet;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import edu.rice.rubis.hibernate.User;
import edu.rice.rubis.hibernate.Region;

/** 
 * Add a new user in the database 
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public class RegisterUser extends HibernateRubisHttpServlet
{

  public int getPoolSize()
  {
    return Config.RegisterUserPoolSize;
  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: Register user");
    sp.printHTML("<h2>Your registration has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    String firstname = null, lastname = null, nickname = null, email = null, password = null;
    Integer regionId = null;
    Integer userId = null;
    String creationDate = null, regionName = null;
    Session sess = null;
    Transaction trans = null;
    ServletPrinter sp = null;
    
    sp = new ServletPrinter(response, "RegisterUser");
    
    firstname = request.getParameter("firstname");
    if (firstname == null || firstname.equals(""))
    {
      printError("You must provide a first name!<br>", sp);
      return;
    }
    
    lastname = request.getParameter("lastname");
    if (lastname == null || lastname.equals(""))
    {
      printError("You must provide a last name!<br>", sp);
      return;
    }
    
    nickname = request.getParameter("nickname");
    if (nickname == null || nickname.equals(""))
    {
      printError("You must provide a nick name!<br>", sp);
      return;
    }
    
    email = request.getParameter("email");
    if (email == null || email.equals(""))
    {
      printError("You must provide an email address!<br>", sp);
      return;
    }
    
    password = request.getParameter("password");
    if (password == null || password.equals(""))
    {
      printError("You must provide a password!<br>", sp);
      return;
    }
    
    regionName = request.getParameter("region");
    if (regionName == null || regionName.equals(""))
    {
      printError("You must provide a valid region!<br>", sp);
      return;
    }
    
    try
    {
      sess = getSession();
      trans = sess.beginTransaction();
      
      Query q = sess.createQuery("select region from Region as region where region.name = :name");
      q.setString("name", regionName);
      List lst = q.list();
      Iterator it = lst.iterator();
      
      if (!it.hasNext())
      {
        printError(" Region " + regionName + " does not exist in the database!<br>", sp);
        try
        {
          trans.rollback();
        }
        catch (HibernateException he)
        {
          sp.printHTML("Transaction rollback failed: " + he + "<br>");
        }
        releaseSession(sess);
        return;
      }
      
      Region region = (Region) it.next();
      
      q = sess.createQuery("select user from User as user where user.nickname = :nickname");
      q.setString("nickname", nickname);
      lst = q.list();
      it = lst.iterator();
      
      if (it.hasNext())
      {
        printError("The nickname you have choosen is already taken by someone else. Please choose a new nickname.<br>", sp);
        try
        {
          trans.rollback();
        }
        catch (HibernateException he)
        {
          sp.printHTML("Transaction rollback failed: " + he + "<br>");
        }
        releaseSession(sess);
        return;
      }
      
      Date now = new Date();
      
      User newUser = new User();
      newUser.setFirstname(firstname);
      newUser.setLastname(lastname);
      newUser.setNickname(nickname);
      newUser.setPassword(password);
      newUser.setEmail(email);
      newUser.setRating(new Integer(0));
      newUser.setBalance(new Float(0));
      newUser.setCreationDate(now);
      newUser.setRegion(region);
      /*
      newUser.setItems(new HashSet());
      newUser.setBids(new HashSet());
      newUser.setBuys(new HashSet());
      newUser.setCommentsFrom(new HashSet());
      newUser.setCommentsTo(new HashSet());
      */
      
      sess.save(newUser);
      
      if (Hibernate.isInitialized(region) && Hibernate.isInitialized(region.getUsers()))
      {
        region.getUsers().add(newUser);
      }
      
      sp.printHTMLheader("RUBiS: Welcome to " + newUser.getNickname());
      sp.printHTML("<h2>Your registration has been processed successfully</h2><br>");
      sp.printHTML("<h3>Welcome " + newUser.getNickname() + "</h3>");
      sp.printHTML("RUBiS has stored the following information about you:<br>");
      sp.printHTML("First Name : " + newUser.getFirstname() + "<br>");
      sp.printHTML("Last Name  : " + newUser.getLastname() + "<br>");
      sp.printHTML("Nick Name  : " + newUser.getNickname() + "<br>");
      sp.printHTML("Email      : " + newUser.getEmail() + "<br>");
      sp.printHTML("Password   : " + newUser.getPassword() + "<br>");
      sp.printHTML("Region     : " + newUser.getRegion().getName() + "<br>");
      sp.printHTML("<br>The following information has been automatically generated by RUBiS:<br>");
      sp.printHTML("User id       :" + newUser.getId() + "<br>");
      sp.printHTML("Creation date :" + newUser.getCreationDate() + "<br>");
      
      trans.commit();
      releaseSession(sess);
    }
    catch (Exception e)
    {
      printError("Failed to register user: " + e, sp);
      try
      {
        trans.rollback();
      }
      catch (HibernateException he)
      {
        sp.printHTML("Transaction rollback failed: " + he + "<br>");
      }
      releaseSession(sess);
    }
    
    sp.printHTMLfooter();
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doGet(request, response);
  }

  /**
   * Clean up the connection pool.
   */
  public void destroy()
  {
    super.destroy();
  }
}
