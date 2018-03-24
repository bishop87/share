<?php

//Quick and dirty example of server-side php script to log gps request in csv format file.
//I recommend to use https and any kind of password protection like .htaccess

if(filter_input(INPUT_POST, 'pwd') == "gpstracker"){
    $num = filter_input(INPUT_POST, 'num', FILTER_SANITIZE_NUMBER_INT);
    $lat = filter_input(INPUT_POST, 'lat', FILTER_SANITIZE_NUMBER_FLOAT, FILTER_FLAG_ALLOW_FRACTION);
    $lng = filter_input(INPUT_POST, 'lng', FILTER_SANITIZE_NUMBER_FLOAT, FILTER_FLAG_ALLOW_FRACTION);
    $acu = filter_input(INPUT_POST, 'acu', FILTER_SANITIZE_NUMBER_FLOAT, FILTER_FLAG_ALLOW_FRACTION);
    $pro = filter_input(INPUT_POST, 'pro', FILTER_SANITIZE_STRING);

    $fileName = "gps_log.csv";
    $data = date("Ymd-H:i:s",time()).";$num;$lat;$lng;$acu;$pro";
    $myfile = file_put_contents($fileName, $data.PHP_EOL , FILE_APPEND | LOCK_EX);

    echo 'posted gps data';
}else{
    echo 'Bad Request!';
    header('X-PHP-Response-Code: 400', true, 400);
}

?>