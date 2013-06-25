<?php
  // NOTE: This script serves for experimental convenience:
  // Clients retrieve stories randomly, without having to change the URL.
  $storyId = rand(1, 6000);
  include("ViewStory.php");
?>
