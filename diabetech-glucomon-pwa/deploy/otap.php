<?php
global $HTTP_RAW_POST_DATA;

file_put_contents(
'otap.log', 
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


$appVer="0.2.69";

//$onReceipt = microtime(true) * 1000 - (1000 * 60 * 360);
//$onSend = microtime(true) * 1000 - (1000 * 60 * 360);
//print "".$appVer.":".sprintf("%.0f",$onReceipt).":".sprintf("%.0f",$onSend);
print "".$appVer
?>


