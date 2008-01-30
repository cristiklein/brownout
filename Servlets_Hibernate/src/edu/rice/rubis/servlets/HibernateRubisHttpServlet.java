package edu.rice.rubis.servlets;

import java.util.Properties;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import java.sql.Connection;

import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Session;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.HibernateException;

import edu.rice.rubis.hibernate.*;

public abstract class HibernateRubisHttpServlet extends RubisHttpServlet
{

    private static SessionFactory sessionFactory;
    private static Properties hibernateProperties;

    static
    {
      try
        {
          // Retrieve Hibernate properties
          hibernateProperties = new Properties();
          InputStream in = new FileInputStream(Config.HibernateProperties);
          hibernateProperties.load(in);
          
          // Configure Hibernate
          Configuration cfg = new Configuration();
          cfg.setProperties(hibernateProperties);
          
          // Add mappings for persitent classes
          cfg.addClass(Bid.class);
          cfg.addClass(Buy.class);
          cfg.addClass(Category.class);
          cfg.addClass(Comment.class);
          cfg.addClass(Item.class);
          cfg.addClass(Region.class);
          cfg.addClass(User.class);
          
          // Build a session factory
          sessionFactory = cfg.buildSessionFactory();
        }
        catch (Exception ex)
        {
          System.out.println("Exception while initializing hibernate: " + ex);
        }
    }

    public void init() throws ServletException
    {
      super.init();
    }

    protected Session getSession()
    {
      Session sess = null;
      
      Connection conn = getConnection();
      sess = sessionFactory.openSession(conn);
      
      return sess;
    }

    protected void releaseSession(Session sess)
    {
        try 
        {
          Connection conn = sess.close();
          releaseConnection(conn);
        }
        catch (HibernateException ex)
        {
        
        }
    }

}


