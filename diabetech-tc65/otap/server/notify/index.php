<?php
global $HTTP_RAW_POST_DATA;

file_put_contents(
'notify.log', 
"\n["
.date(DATE_RFC822)
."]\n["
.serialize($_SERVER)
."]\n["
.serialize($_POST)
."]\n["
.date(DATE_RFC822)
."]\n["
.serialize($HTTP_RAW_POST_DATA)
."]\n",
 FILE_APPEND
);
?>

