package edu.rice.rubis.hibernate;

import java.util.Date;
import java.util.Set;

public class Item
{
    private Integer id;
    private String name;
    private String description;
    private Float initialPrice;
    private Integer quantity;
    private Float reservePrice;
    private Float buyNow;
    private Integer nbOfBids;
    private Float maxBid;
    private Date startDate;
    private Date endDate;
    private User seller;
    private Category category;
    private Set bids;
    private Set buys;
    private Set comments;
    
    public Integer getId()
    {
        return id;
    }
    
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public Float getInitialPrice()
    {
        return initialPrice;
    }
    
    public void setInitialPrice(Float initialPrice)
    {
        this.initialPrice = initialPrice;
    }
    
    public Integer getQuantity()
    {
        return quantity;
    }
    
    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }
    
    public Float getReservePrice()
    {
        return reservePrice;
    }
    
    public void setReservePrice(Float reservePrice)
    {
        this.reservePrice = reservePrice;
    }
    
    public Float getBuyNow()
    {
        return buyNow;
    }
    
    public void setBuyNow(Float buyNow)
    {
        this.buyNow = buyNow;
    }
    
    public Integer getNbOfBids()
    {
        return nbOfBids;
    }
    
    public void setNbOfBids(Integer nbOfBids)
    {
        this.nbOfBids = nbOfBids;
    }
    
    public Float getMaxBid()
    {
        return maxBid;
    }
    
    public void setMaxBid(Float maxBid)
    {
        this.maxBid = maxBid;
    }
    
    public Date getStartDate()
    {
        return startDate;
    }
    
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }
    
    public Date getEndDate()
    {
        return endDate;
    }
    
    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }
    
    public User getSeller()
    {
        return seller;
    }
    
    public void setSeller(User seller)
    {
        this.seller = seller;
    }
    
    public Category getCategory()
    {
        return category;
    }
    
    public void setCategory(Category category)
    {
        this.category = category;
    }
    
    public Set getBids()
    {
        return bids;
    }
    
    public void setBids(Set bids)
    {
        this.bids = bids;
    }
    
    public Set getBuys()
    {
        return buys;
    }
    
    public void setBuys(Set buys)
    {
        this.buys = buys;
    }
    
    public Set getComments()
    {
        return comments;
    }
    
    public void setComments(Set comments)
    {
        this.comments = comments;
    }
}
