<?php

require_once 'Constants.php';

//connecting to database and getting the connection object
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

//Checking if any error occured while connecting
if (mysqli_connect_errno()) {
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
	die();
}
date_default_timezone_set('Etc/GMT+7');
$dateToday	=	date('Ymd',strtotime("0 days")); 
$response = array();
// Attempt update query execution
$sql = "UPDATE `demands` SET `status` = 'EXP' WHERE duration_end = '$dateToday'";
if(mysqli_query($conn, $sql)){
    $response['message'] = 'success';
} else {
    $response['message'] = "ERROR: Could not able to execute $sql. " . mysqli_error($conn);
}

echo json_encode($response);
// Close connection
mysqli_close($conn);
?>