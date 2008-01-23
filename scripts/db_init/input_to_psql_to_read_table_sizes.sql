
SELECT relname, relpages FROM pg_class WHERE relname="categories" OR relname="regions" OR relname="users" OR relname="items" OR relname="old_items" OR relname="bids" OR relname="comments" OR relname="buy_now" OR relname="ids" ORDER BY relpages DESC;
