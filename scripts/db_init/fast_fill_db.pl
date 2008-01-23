#!/usr/bin/perl

use FileHandle;

##################################################
##################################################
#
# Generates SQL commands to create a database 
# for the RUBiS benchmark from Rice University.
# 
# Copyright Jan 15, 2007: Steven.Dropsho@EPFL.ch
#
##################################################
##################################################

##################################################
##################################################
#
# Notable issues:
#
# + The 'ids' table is not loaded by InitDB.java
#
# + The paper "Bottleneck Characterization of Dynamic Web Site Benchmarks"
# mentions an 'old_comments' table in Section 3.
# There is no such table. We attempt to match the
# ratio of the number of entries in each table
# here.
#
# + Adding BuyNow seems incorrect in InitDB.java.
# RegisterItem assigns a buy_now flag to a fraction
# of the items but nowhere is there an insert into
# the buy_now table. We follow InitDB.java here.
#
# + InitDB.java does not set the users 'rating'
# and 'balance' fields in the users table.
# 
#
##################################################
##################################################

##################################################
#
# Constants
$SQL_STREAM_QUANTUM = 10000;
$MAGIC_SIZE_MULTIPLIER = 1000;

$RUBIS_INSTALL_DIR = ".";
$PROPERTIES_FILE_BASE = "rubis.properties";

# Use number of items as base scale. It is the value input.
$RATIO_USERS_TO_ITEMS = (1000000/33000);
$RATIO_OLD_ITEMS_TO_ITEMS = (500000/33000);

$USERS_FILE = "insert_users.sql";
$ITEMS_FILE = "insert_items.sql";
$REGIONS_FILE = "insert_regions.sql";
$CATEGORIES_FILE = "insert_categories.sql";

$KEY_NUMBER_USERS = "database_number_of_users";
$KEY_REGIONS_FILE = "database_regions_file";
$KEY_REGIONS_ARRAY = "regions_array_key";
$KEY_CATEGORIES_FILE = "database_categories_file";
$KEY_CATEGORIES_ARRAY = "regions_categories_key";
$KEY_TOTAL_ACTIVE_ITEMS = "total_active_items_key";

$MAX_RATING = 5;

$STATIC_DESCRIPTION = &MakeSafe("This incredible item is exactly what you need !<br>It has a lot of very nice features including "."a coffee option.<p>It comes with a free license for the free RUBiS software, that's really cool. But RUBiS even if it "."is free, is <B>(C) Rice University/INRIA 2001</B>. It is really hard to write an interesting generic description for "."automatically generated items, but who will really read this ?<p>You can also check some cool software available on "."http://sci-serv.inrialpes.fr. There is a very cool DSM system called SciFS for SCI clusters, but you will need some "."SCI adapters to be able to run it ! Else you can still try CART, the amazing 'Cluster Administration and Reservation "."Tool'. All those software are open source, so don't hesitate ! If you have a SCI Cluster you can also try the Whoops! "."clustered web server. Actually Whoops! stands for something ! Yes, it is a Web cache with tcp Handoff, On the fly "."cOmpression, parallel Pull-based lru for Sci clusters !! Ok, that was a lot of fun but now it is starting to be quite late "."and I'll have to go to bed very soon, so I think if you need more information, just go on <h1>http://sci-serv.inrialpes.fr</h1> "."or you can even try http://www.cs.rice.edu and try to find where Emmanuel Cecchet or Julie Marguerite are and you will "."maybe get fresh news about all that !!<p>");

@STATIC_COMMENTS = (&MakeSafe("This is a very bad comment. Stay away from this seller !!<p>"),
		    &MakeSafe("This is a comment below average. I don't recommend this user !!<p>"),
		    &MakeSafe("This is a neutral comment. It is neither a good or a bad seller !!<p>"),
		    &MakeSafe("This is a comment above average. You can trust this seller even if it is not the best deal !!<p>"),
		    &MakeSafe("This is an excellent comment. You can make really great deals with this seller !!<p>")); 

##################################################
#
# Main loop

# MUST get input parameters from a rubis.properties file
# that will match that of the rubis runs to be done.
($properties_file, $magic_size_number) = &Usage(\@ARGV);

$properties = &LoadProperties($properties_file, $magic_size_number);

# Print the properties file to be used by RUBiS, append the magic number to name
&PrintProperties($properties, "$PROPERTIES_FILE_BASE.$magic_size_number");

&GenerateUsers($properties);
&GenerateRegions($properties);
&GenerateCategories($properties);

my $addBidsPercent = 1;
my $addCommentsPercent = 0.95;
&GenerateItems($properties, $addBidsPercent, $addCommentsPercent);

exit(1);

##################################################
############ Support routines ####################
##################################################

sub Usage
{
    local($argv) = @_;

    if ($#$argv < 2) {
	die "Usage error: fast_fill_db.pl <rubis install dir> <rubis.properties file> <number users/items/olditems in thousands, 10=>10,000>\n";
    }

    my $i = 0;
    $RUBIS_INSTALL_DIR = $argv->[$i++];
    my $rubis_properties_file = $argv->[$i++];
    my $magic_size_number = $argv->[$i++];

    return($rubis_properties_file, $magic_size_number);
}

sub MakeSafe
{
    local($string) = @_;

    # Removes 'bad' chars from strings
    $string =~ s/\'/\\\'/g;  # The \' mark is a problem for postgres as it denotes constants
    $string =~ s/\\+$//;     # Get rid of escape chars at end
    return $string;
}

sub LoadRegionsFile
{
    local($regions_file) = @_;
    local(*fd);

    open(fd, "< $regions_file") or die "ERROR: Cannot open regions file <$regions_file>\n";
    my @regions = ();

    while (my $buf = <fd>) {
	# Expect simple one-entry per line format, no whitespace
	$buf =~ s/[\s\n]*//g;
	push(@regions, $buf);
    }
    close(fd);
    return(\@regions);
}

sub LoadCategoriesFile
{
    local($categories_file) = @_;
    local(*fd);

    open(fd, "< $categories_file") or die "ERROR: Cannot open regions file <$categories_file>\n";
    my %categories = ();

    my $total = 0;
    while (my $buf = <fd>) {
	# Expect simple one-entry per line format, "description (count)" format
	$buf =~ /(.+)\s+\((\d+)\)/;
	my $desc = $1;
	my $count = $2;
	$categories{$desc} = $count;
	$total += $count;
    }
    close(fd);
    return(\%categories, $total);
}

sub LoadProperties
{
    local($rubis_properties_file, $magic_size_number) = @_;
    local(*fd);

    open(fd, "< $rubis_properties_file") or die "Error: Cannot open for reading the properties file <$rubis_properties_file>\n";

    # Parse properties file into associative array
    my %properties = ();
    while (my $buf = <fd>) {
	$buf =~ s/^\s+//g;      # Remove leading whitespace
	$buf =~ s/\#.*\n//;     # Remove comments
	$buf =~ s/\n//;         # Remove end of line
	($name, $value) = split(/\s*=\s*/, $buf);
	$value =~ s/RUBIS_BASE_DIR/$RUBIS_INSTALL_DIR/g;

	if ($name ne "") {
	    $properties{$name} = $value;
	}
    }
    close(fd);

    # Set some properties not defined in the properties file as multiples of the user input "magic_size_number"
    $properties{"magic_size_number"} = $magic_size_number;
    my $nbItems = int($magic_size_number * $MAGIC_SIZE_MULTIPLIER);
    my $nbUsers = int($nbItems * $RATIO_USERS_TO_ITEMS);
    my $nbOldItems = int($nbItems * $RATIO_OLD_ITEMS_TO_ITEMS);
    $properties{$KEY_NUMBER_USERS} = $nbUsers;
    $properties{"database_number_of_old_items"} = $nbOldItems;
    $properties{"database_number_of_items"} = $nbItems;

    # Load regions info from file
    my $regions = &LoadRegionsFile($properties{$KEY_REGIONS_FILE});
    $properties{$KEY_REGIONS_ARRAY} = $regions;

    # Load categories info from file
    my $categories;
    my $totalActiveItems;
    ($categories, $totalActiveItems) = &LoadCategoriesFile($properties{$KEY_CATEGORIES_FILE});
    $properties{$KEY_CATEGORIES_ARRAY} = $categories;
    $properties{$KEY_TOTAL_ACTIVE_ITEMS} = $totalActiveItems;

    return(\%properties);
}

sub PrintProperties
{
    local($properties, $file) = @_;
    local(*fd);
    
    # Print the properties file to be used by RUBiS
    open(fd, "> $file") or die "Error: Cannot create properties file <$file>\n";
    foreach my $p (sort keys %$properties) {
	my $value = $properties->{$p};
	if ($p =~ /[a-zA-Z]/) { # None empty name
	    printf fd "%s = %s\n", $p, $value;
	}
    }
    close(fd);
}

sub GenerateRegions
{
    local($properties) = @_;
    local(*ufd);

    my $regions = $properties->{$KEY_REGIONS_ARRAY}; # Ref to array containing regions
    my $nbRegions = $#$regions+1;

    printf stdout "Generating $nbRegions regions insert commands to file $REGIONS_FILE\n";

    open(ufd, "> $REGIONS_FILE") or die "ERROR: Cannot open file to record SQL regions insert commands\n";
    for (my $i=0; $i<$nbRegions; $i++) {

	my $id = $i+1;
	my $region_name = $regions->[$i];

	my $sql_cmd = "INSERT INTO regions VALUES ("
	    . $id
	    . ", \'"
	    . $region_name
            . "\')";

	printf ufd "$sql_cmd;\n";
    }
    printf stdout "-- $nbRegions : putting regions in db...\n";
    close(ufd);
    $result = system("psql -d rubis -f $REGIONS_FILE | grep -i error");
}

sub GenerateCategories
{
    local($properties) = @_;
    local(*ufd);

    my $cat_keys = &OrderCategories($properties);
    my $nbCats = $#$cat_keys+1;

    printf stdout "Generating $nbCats categories insert commands to file $CATEGORIES_FILE\n";

    open(ufd, "> $CATEGORIES_FILE") or die "ERROR: Cannot open file to record SQL categories insert commands\n";
    for (my $i=0; $i<$nbCats; $i++) {

	my $id = $i+1;
	my $category_name = $cat_keys->[$i];

	my $sql_cmd = "INSERT INTO categories VALUES ("
	    . $id
	    . ", \'"
	    . $category_name
            . "\')";

	printf ufd "$sql_cmd;\n";
    }
    printf stdout "-- $nbCats : putting categories in db...\n";
    close(ufd);
    $result = system("psql -d rubis -f $CATEGORIES_FILE | grep -i error");
}

sub GenerateUsers
{
    local($properties) = @_;
    local(*ufd);

    my $nbUsers = $properties->{$KEY_NUMBER_USERS};
    my $regions = $properties->{$KEY_REGIONS_ARRAY}; # Ref to array containing regions
    my $nbRegions = $#$regions+1;

    printf stdout "Generating $nbUsers user insert commands to file $USERS_FILE\n";

    # Generate SQL commands to insert users, but do so in batches so the SQL file does
    # not get massive
    my $opened = 0;
    for (my $i=0; $i<$nbUsers; $i++) {

	if (!$opened) {
	    open(ufd, "> $USERS_FILE") or die "ERROR: Cannot open file to record SQL user insert commands\n";
	    $opened = 1;
	}
	my $id = $i+1;
	my $firstname = "Great".$id;
	my $lastname = "User".$id;
	my $nickname = "user".$id;
	my $email = $firstname . "." . $lastname . "\@rubis.com";
	my $password = "password" . $id;
	my $regionId = $i % $nbRegions;

	my $sql_cmd = "INSERT INTO users VALUES ("
	    . $id
	    . ", \'"
	    . $firstname
	    . "\', \'"
            . $lastname
            . "\', \'"
            . $nickname
            . "\', \'"
            . $password
            . "\', \'"
            . $email
            . "\', 0, 0, "
            . "now()"
            . ", "
            . $regionId
            . ")";

	printf ufd "$sql_cmd;\n";

	# Every quantum run the psql command that processes the users file
	if ($id % $SQL_STREAM_QUANTUM == 0) {
	    printf stdout "$id : putting users in db...\n";
	    close(ufd);
	    $opened = 0;
	    $result = system("psql -d rubis -f $USERS_FILE | grep -i error");
	    system("rm $USERS_FILE");
	}
    }
    # Process the final users (if total users not a multiple of the quantum)
    if ($opened) {
	printf stdout "-- final : putting users in db...\n";
	close(ufd);
	$result = system("psql -d rubis -f $USERS_FILE | grep -i error");
	system("rm $USERS_FILE");
    }
}

sub OrderCategories
{
    local($properties) = @_;
    my $categories = $properties->{$KEY_CATEGORIES_ARRAY}; # An assoc array "category -> number items in cat."
    my @cat_keys = sort keys %$categories;
    return(\@cat_keys);
}

sub GenerateItems
{
    local($properties, $generate_bids_percent, $generate_comments_percent) = @_;

    my $getItemDescriptionLength = $properties->{"database_item_description_length"};
    my $getPercentReservePrice = $properties->{"database_percentage_of_items_with_reserve_price"};
    my $getPercentBuyNow = $properties->{"database_percentage_of_buy_now_items"};
    my $getPercentUniqueItems = $properties->{""};
    my $getMaxItemQty = $properties->{"database_max_quantity_for_multiple_items"};
    my $getCommentMaxLength = $properties->{"database_comment_max_length"};
    my $getNbOfUsers = $properties->{$KEY_NUMBER_USERS};
    my $getMaxBidsPerItem = $properties->{"database_max_bids_per_item"};

    my $categories = $properties->{$KEY_CATEGORIES_ARRAY}; # An assoc array "category -> number items in cat."
    my $cat_keys = &OrderCategories($properties);
    my $getNbOfCategories = $#$cat_keys+1;
    my $oldItems = $properties->{"database_number_of_old_items"};
    my $activeItems = $properties->{"database_number_of_items"};
    
    my $totalItems = $oldItems + $activeItems;

    # Generate a maximum sized description (once) then just take appropriately sized substrings in loop
    my $max_description = "";
    my $now_length = 0;
    my $sd_length = length($STATIC_DESCRIPTION);
    while ($now_length < $getItemDescriptionLength) {
	$max_description .= $STATIC_DESCRIPTION;
	$now_length += $sd_length;
    }

    # Generate a maximum sized comments (once) then just take appropriately sized substrings in loop
    my @max_comments = ();
    for (my $c=0; $c<$MAX_RATING; $c++) {
	my $max_comment = "";
	my $now_length = 0;
	my $sc_length = length($STATIC_COMMENTS[$c]);
	while ($now_length < $getCommentMaxLength) {
	    $max_comment .= $STATIC_COMMENTS[$c];
	    $now_length += $sc_length;
	}
	$max_comments[$c] = $max_comment;
    }

    printf stdout "Generating $totalItems item ($oldItems old/$activeItems active) insert commands to file $ITEMS_FILE\n";

    my $bid_id = 0;
    my $comment_id = 0;

    #
    # InitDB.java has one loop for generating both old and active items. Therefore, I do the same here,
    # but it causes some confusion in setting the table name ("old_items" or "items")
    # 
    my $total_bids = 0;
    my $total_comments = 0;
    my $total_buy_nows = 0;
    my $opened = 0;
    for (my $i=0; $i<$totalItems; $i++) {

	if (!$opened) {
	    open(ifd, "> $ITEMS_FILE") or die "ERROR: Cannot open file to record SQL item insert commands\n";
	    $opened = 1;
	}

	# There are two categories of items: Old and active. Set vars to appropriate set then proceed.
	my $itable = ($i < $oldItems) ? "old_items" : "items";

	my $itemId = $i+1; # SGD: Note, we do not restart the itemId at 0 for active items. Ids unique across ALL items
	my $name = &MakeSafe("RUBiS automatically generated item #".$itemId);
	my $initialPrice = int(rand(5000))+1;
	my $duration = int(rand(7))+1;

	# Note, this follows the RUBiS InitDB.java method. All categories will have the same number of items.
	# Disregards the relative active item values given in the categories file.
	my $categoryId = $i % $getNbOfCategories;
	while ($categories->{$cat_keys->[$categoryId]} <= 0) {
	    $categoryId = ($categoryId+1) % $getNbOfCategories;
	}
	my $sellerId = int(rand($getNbOfUsers))+1;

	# Returns a description clipped to the randomly generated size.
	# Annoying: Must ensure escaped chars such as \' are not split by the random end of string location chosen.
	# Clip them if so.
	my $end_of_desc_index = int(rand($getItemDescriptionLength))+1;
	my $description = substr($max_description,0,$end_of_desc_index);
	$description =~ s/\\+$//; # clip any escape chars at end of string

	my $plus_minus = ($itable eq "old_items") ? "-" : "+"; # Old items have a end date < start date ("-")

	# SGD: InitDB.java has an error in how it uses the percents in splitting the actions in each category.
	# SGD: Just use a simple call to rand(1) to get the distribution.
	my $quantity = (100*rand(1) < $getPercentUniqueItems) ? 1 : int(rand($getMaxItemQty));
	my $reservePrice = (100*rand(1) < $getPercentReservePrice) ? int(rand(1000))+$initialPrice : 0;
	my $buyNow = (100*rand(1) < $getPercentBuyNow) ? int(rand(1000))+$initialPrice+$reservePrice : 0;

	if ($buyNow > 0) {
	    $total_buy_nows++;
	}

	# SGD: Note, InitDB.java performs no insertions into the old_items table! Old items are put
	# into the "items" table. There are, however, queries of the old_items table (which should 
	# always be empty since there are no insertions). We put items into the old_items table here.
	my $sql_cmd = "INSERT INTO $itable VALUES ("
	    . $itemId
	    . ", \'"
	    . $name
	    . "\', \'"
            . $description
            . "\', \'"
            . $initialPrice
            . "\', \'"
            . $quantity
            . "\', \'"
            . $reservePrice
            . "\', \'"
            . $buyNow
            . "\', 0, 0, "
            . "now()"
            . ", "
            . "now() $plus_minus interval \'$duration days\'"
            . ", \'"
            . $sellerId
            . "\', "
            . $categoryId
            . ")";

	printf ifd "$sql_cmd;\n";

	# Not every item has to have a bid
	if ($generate_bids_percent >= rand(1)) {
	    $total_bids++;
	    my $maxOfMaxBids = 0;
	    my $nbBids = int(rand($getMaxBidsPerItem));
	    for (my $j=0; $j<$nbBids; $j++) {

		# Bid id is just $itemId+1 in the original code InitDB.java. 
		# I think that is wrong and what is here is correct (a unique
		# bid id).
		$bid_id++;

		my $userId = int(rand($getNbOfUsers))+1;

		my $addBid = int(rand(10))+1;
		my $bid = $initialPrice + $addBid;
		my $maxBid = $bid + $addBid;
		if ($maxBid > $maxOfMaxBids) {
		    $maxOfMaxBids = $maxBid;
		}

		my $qty = int(rand($quantity))+1;
		
		$sql_cmd = "INSERT INTO bids VALUES ($bid_id, \'"
		    . $userId
		    . "\', \'"
		    . $itemId
		    . "\', \'"
		    . $qty
		    . "\', \'"
		    . $bid
		    . "\', \'"
		    . $maxBid
		    . "\', "
		    . "now()"
		    . ")";
	    
		printf ifd "$sql_cmd;\n";

		$initialPrice += $addBid;
	    }
	    # Update meta-bid data
	    $sql_cmd = "UPDATE $itable SET max_bid=\'"
		. $maxOfMaxBids
		. "\', nb_of_bids=\'"
		. $nbBids
		. "\' WHERE id=\'"
		. $itemId
		. "\';";
	    
	    printf ifd "$sql_cmd;\n";
	}
    
	# Not every item has a comment
	if ($generate_comments_percent >= rand(1)) {
	    $total_comments++;
	    $comment_id++;
	    my $rating = int(rand($MAX_RATING));
	    my $comment = substr($max_comments[$rating], 0, int(rand($getCommentMaxLength))+1);
	    $comment =~ s/\\+$//; # clip any escape chars at end of string

	    my $fromId = int(rand($getNbOfUsers))+1;

	    $sql_cmd = "INSERT INTO comments VALUES ($comment_id, \'"
              . $fromId
              . "\', \'"
              . $sellerId
              . "\', \'"
              . $itemId
              . "\', \'"
              . $rating
              . "\', "
              . "now()"
              . ", \'"
              . $comment
              . "\');";

	    printf ifd "$sql_cmd\n";
	}
	
	# To limit the size of the SQL command file, periodically process the commands and clear file
	if ($itemId % $SQL_STREAM_QUANTUM == 0) {
	    printf stdout "-- $itemId : putting items in db...\n";
	    close(ifd);
	    $opened = 0;
	    $result = system("psql -d rubis -f $ITEMS_FILE | grep -i error");
	    system("rm $ITEMS_FILE");
	}
    }
    # Process last set of commands
    if ($opened) {
	printf stdout "-- $totalItems : putting items in db...\n";
	close(ifd);
	$opened = 0;
	$result = system("psql -d rubis -f $ITEMS_FILE | grep -i error");
	system("rm $ITEMS_FILE");
    }

    printf(stdout "STATS: items %d, old_items %d, bids %d, buy_nows %d, comments %d\n",
	   $activeItems, $oldItems, $total_bids, $total_buy_nows, $total_comments);
}
