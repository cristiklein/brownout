package edu.rice.rubis.beans;

import edu.rice.rubis.TimeManagement;
import java.util.*;
import javax.persistence.*;

/**
 * RegionBean is an entity bean with "container managed persistence". 
 * The state of an instance is stored into a relational database. 
 * The following table should exist:<p>
 * <pre>
 * CREATE TABLE regions (
 *    id   INTEGER UNSIGNED NOT NULL UNIQUE,
 *    name VARCHAR(20),
 *    PRIMARY KEY(id)
 * );
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
@Entity
@Table(name="regions")
@NamedQueries
({
  @NamedQuery(name="regionByName", query="select r from RegionBean r where r.name = ?1"),
  @NamedQuery(name="allRegions", query="select r from RegionBean r")
})
public class RegionBean
{
  @Id
  @Column(name="id")
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  protected Integer id;
  @Column(name="name")
  protected String name;
  @OneToMany(mappedBy="region")
  protected Collection<UserBean> users = new ArrayList<UserBean>();

  protected RegionBean() {}

  /**
   * Get region's id.
   *
   * @return region id
   */
  public Integer getId() { return id; }

  /**
   * Set region's id
   *
   * @param newId region id
   */
  public void setId(Integer newId) { id = newId; }

  /**
   * Get region name.
   *
   * @return region name
   */
  public String getName() { return name; }

  /**
   * Set region's name
   *
   * @param newName region name
   */
  public void setName(String newName) { name = newName; }

  /**
   * Get the region users.
   *
   * @return region users
   */
  public Collection<UserBean> getUsers() { return users; }

  /**
   * Set region's users
   *
   * @param newUsers region users
   */
  public void setUsers(Collection<UserBean> newUsers) { users = newUsers; }

  /**
   * This method is used to create a new Region Bean.
   *
   * @param regionName Region name
   */
  public RegionBean(String regionName)
  {
     setName(regionName);
  }
}
