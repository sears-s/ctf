<?php
$hash = "0e747135815419029880333118591372";
$salt = "e361bfc569ba48dc";
$i = 0;
while (true) {
    if (md5($salt . $i) == $hash) {
        echo $i;
        break;
    }
    $i++;
}
?>