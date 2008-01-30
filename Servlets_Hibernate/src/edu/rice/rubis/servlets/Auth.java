package edu.rice.rubis.servlets;

import net.sf.hibernate.Session;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;

import java.util.List;
import java.util.Iterator;

import edu.rice.rubis.hibernate.User;

public class Auth
{

  //private Context servletContext;
  private Session sess;
  private ServletPrinter sp;

  public Auth(Session session, ServletPrinter printer)
  {
    sess = session;
    sp = printer;
  }

  public User authenticate(String nickname, String password)
  {
    User user = null;
    
    // Lookup the user
    try
    {
      Query q = sess.createQuery("select user from User as user where user.nickname = :nickname and user.password = :password");
      q.setString("nickname", nickname);
      q.setString("password", password);
      List lst = q.list();
      Iterator it = lst.iterator();
      if (!it.hasNext())
      {
        sp.printHTML(" User " + nickname + " does not exist in the database!<br><br>");
        return user;
      }
      user = (User) it.next();
    }
    catch (HibernateException he)
    {
      sp.printHTML("Failed to executeQuery: " + he);
    }
    finally
    {
      return user;
    }
  }

}
