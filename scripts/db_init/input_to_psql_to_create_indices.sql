
CREATE INDEX auth ON users (nickname,password);
CREATE INDEX region_id ON users (region);
CREATE INDEX seller_id ON items (seller);
CREATE INDEX category_id ON items (category);
CREATE INDEX old_seller_id ON old_items (seller);
CREATE INDEX old_category_id ON old_items (category);
CREATE INDEX bid_item ON bids (item_id);
CREATE INDEX bid_user ON bids (user_id);
CREATE INDEX from_user ON comments (from_user_id);
CREATE INDEX to_user ON comments (to_user_id);
CREATE INDEX item ON comments (item_id);
CREATE INDEX buyer ON buy_now (buyer_id);
CREATE INDEX buy_now_item ON buy_now (item_id);
