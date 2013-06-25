<?php
  // NOTE: This script serves for experimental convenience:
  // Clients retrieve items randomly, without having to change the URL.
  $itemId = rand(0, 32767);
  include("ViewItem.php");
?>
