package edu.rice.rubis.beans;

/**
 * This class is used to return the results of the JOIN query on items and bids
 * when a user is browsing items. The query issued is something like
 * <pre>SELECT items.id, items.name, items.initial_price, items.end_date,count(bids.id) AS count, max(bid) AS max 
 * FROM items LEFT JOIN bids ON items.id=bids.item_id WHERE items.category=? AND end_date>=NOW() GROUP BY items.id</pre>
 *
 * As it is not feasible to have an entity bean matching this kind of result, we use
 * this class as a way to return necessary information to the servlet.
 *
 * Note that member variables in this class are all public without access methods which is
 * quite dirty but it is only for efficiency purposes (we have to verify that it is really
 * more efficient than using access methods but I guess we avoid at least one indirection).
 * This is a critical part in the bidding system because it is the main used browsing function.
 *
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 * @deprecated This class is deprecated since maxBid and nbOfBids have been added to the items table in the database.
 * Note that performance was too bad when doing a join between items and bids tables. As this is widely used for
 * browsing (SearchItemsBy...), we really needed something faster and came up to this solution.
 */

public class ItemAndBids implements java.io.Serializable
{
  /** Item identifier matches field id in items table @see rubis.sql  */
  public int     itemId;
  /** Item name matches field name in items table @see rubis.sql  */
  public String  itemName;
  /** Item auction end date matches field end_date in items table @see rubis.sql  */
  public String  endDate;
  /** Highest bid for this item or initial price if no bid are present for this item */
  public float   maxBid;
  /** Number of bids for this item */
  public int     nbOfBids;

  /**
   * Creates a new <code>ItemAndBids</code> instance.
   *
   * @param ItemId item identifier
   * @param ItemName item name
   * @param EndDate auction end date
   * @param MaxBid highest bid for this item or initial price if no bid are present
   * @param NbOfBids number of bids
   */
  ItemAndBids(int ItemId, String  ItemName, String EndDate, float MaxBid, int NbOfBids)
  {
    itemId = ItemId;
    itemName = ItemName;
    endDate = EndDate;
    maxBid = MaxBid;
    nbOfBids = NbOfBids;
  }
}
