DELETE FROM ids;
INSERT INTO ids (category,region,users,item,comment,bid,buyNow) SELECT MAX(categories.id),MAX(regions.id),MAX(users.id),MAX(items.id),MAX(comments.id),MAX(bids.id),MAX(buy_now.id) FROM categories,regions,users,items,comments,bids,buy_now;
