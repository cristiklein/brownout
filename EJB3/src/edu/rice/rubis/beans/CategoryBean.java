package edu.rice.rubis.beans;

import java.util.*;
import java.net.*;
import javax.persistence.*;

/**
 * CategoryBean is an entity bean with "container managed persistence". 
 * The state of an instance is stored into a relational database. 
 * The following table should exist:<p>
 * <pre>
 * CREATE TABLE categories (
 *    id   INTEGER UNSIGNED NOT NULL UNIQUE,
 *    name VARCHAR(50),
 *    PRIMARY KEY(id)
 * );
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
@Entity
@Table(name="categories")
@NamedQueries
({
  @NamedQuery(name="currentItemsInCategory", query="select i from CategoryBean c left join c.items i where c = ?1 and i.endDate >= ?2"),
  @NamedQuery(name="currentItemsInCategoryAndRegion", query="select i from CategoryBean c left join c.items i where c = ?1 and i.seller.region = ?2 and i.endDate >= ?3"),
  @NamedQuery(name="allCategories", query="select c from CategoryBean c")
})
public class CategoryBean
{
  @Id
  @Column(name="id")
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  protected Integer id;
  @Column(name="name")
  protected String name;
  @OneToMany(mappedBy="category")
  protected Collection<ItemBean> items = new ArrayList<ItemBean>();

  protected CategoryBean() {}

  /**
   * Get category's id.
   *
   * @return category id
   */
  public Integer getId() { return id; }

  /**
   * Set category's id
   *
   * @param newId category id
   */
  public void setId(Integer newId) { id = newId; }

  /**
   * Get the category name.
   *
   * @return category name
   */
  public String getName() { return name; }

  /**
   * Set category's name
   *
   * @param newName category name
   */
  public void setName(String newName) { name = newName; }

  /**
   * Get the category items.
   *
   * @return category items
   */
  public Collection<ItemBean> getItems() { return items; }

  /**
   * Set category's items.
   *
   * @param newItems category items
   */
  public void setItems(Collection<ItemBean> newItems) { items = newItems; }

  /**
   * This method is used to create a new Category Bean.
   *
   * @param categoryName Category name
   */
  public CategoryBean(String categoryName)
  {
    this();
    
    setName(categoryName);
  }

  /**
   * Display category information for the BrowseCategories servlet
   *
   * @return a <code>String</code> containing HTML code
   * @exception RemoteException if an error occurs
   * @since 1.0
   */
  public String printCategory()
  {
    return "<a href=\""+BeanConfig.context+"/servlet/SearchItemsByCategory?category="+getId()+
                  "&categoryName="+URLEncoder.encode(getName())+"\">"+getName()+"</a><br>\n";
  }

  /**
   * Display category information for the BrowseCategories servlet
   *
   * @return a <code>String</code> containing HTML code
   * @exception RemoteException if an error occurs
   * @since 1.0
   */
  public String printCategoryByRegion(int regionId)
  {
    return "<a href=\""+BeanConfig.context+"/servlet/SearchItemsByRegion?category="+getId()+
      "&categoryName="+URLEncoder.encode(getName())+"&region="+regionId+"\">"+getName()+"</a><br>\n";
  }

  /**
   * Display category information for the BrowseCategories servlet
   *
   * @return a <code>String</code> containing HTML code
   * @exception RemoteException if an error occurs
   * @since 1.0
   */
  public String printCategoryToSellItem(int userId)
  {
    return "<a href=\""+BeanConfig.context+"/servlet/SellItemForm?category="+getId()+"&user="+userId+"\">"+getName()+"</a><br>\n";
  }
}
