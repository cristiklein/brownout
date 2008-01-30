package edu.rice.rubis.hibernate;

import java.util.Date;
import java.util.Set;

public class User
{
    private Integer id;
    private String firstname;
    private String lastname;
    private String nickname;
    private String password;
    private String email;
    private Integer rating;
    private Float balance;
    private Date creationDate;
    private Region region;
    private Set items;
    private Set bids;
    private Set buys;
    private Set commentsFrom;
    private Set commentsTo;
    
    public Integer getId()
    {
        return id;
    }
    
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public String getFirstname()
    {
        return firstname;
    }
    
    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }
    
    public String getLastname()
    {
        return lastname;
    }
    
    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }
    
    public String getNickname()
    {
        return nickname;
    }
    
    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public Integer getRating()
    {
        return rating;
    }
    
    public void setRating(Integer rating)
    {
        this.rating = rating;
    }
    
    public Float getBalance()
    {
        return balance;
    }
    
    public void setBalance(Float balance)
    {
        this.balance = balance;
    }
    
    public Date getCreationDate()
    {
        return creationDate;
    }
    
    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }
    
    public Region getRegion()
    {
        return region;
    }
    
    public void setRegion(Region region)
    {
        this.region = region;
    }
    
    public Set getItems()
    {
        return items;
    }
    
    public void setItems(Set items)
    {
        this.items = items;
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
    
    public Set getCommentsFrom()
    {
        return commentsFrom;
    }
    
    public void setCommentsFrom(Set commentsFrom)
    {
        this.commentsFrom = commentsFrom;
    }
    
    public Set getCommentsTo()
    {
        return commentsTo;
    }
    
    public void setCommentsTo(Set commentsTo)
    {
        this.commentsTo = commentsTo;
    }
}
