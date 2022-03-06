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
$dateYesterday	=	date('Ymd',strtotime("-1 days"));  

//creating a query

if($_SERVER['REQUEST_METHOD'] == 'POST'){
	$con = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die('Unable to Connect');
		if(isset($_POST['agricultural_sector'])){
			$agriculturalSector  = $_POST['agricultural_sector'];
			$productID			 = $_POST['product_id'];
		if($agriculturalSector == 'crops'){
			$query = "select up.profile_pic, u.user_id, CONCAT(u.first_name, ' ' , u.last_name) AS fullname, u.location, u.contact_no, d.demand_id, d.price, cv.crop_variety_name, d.needed_kilograms, d.received_kilograms, d.duration_end, d.description FROM users u, user_profile up, vendors v, demands d, crop_varieties cv WHERE d.variety_id = cv.crop_variety_id AND v.user_id = u.user_id AND d.agricultural_sector = 'crops' AND d.status = 'INC' AND up.user_id = u.user_id AND d.product_id = '$productID' AND d.vendor_id = v.vendor_id";
		}elseif ($agriculturalSector == 'livestocks') {
			$query = "select  up.profile_pic, u.user_id, CONCAT(u.first_name, ' ' , u.last_name) AS fullname, u.location, u.contact_no, d.demand_id, d.price, ltv.livestock_type_variety_name, d.needed_kilograms, d.received_kilograms, d.duration_end, d.description  FROM users u, user_profile up, vendors v, demands d, livestock_type_varieties ltv WHERE d.variety_id = ltv.livestock_type_variety_id AND v.user_id = u.user_id AND d.agricultural_sector = 'livestocks' AND d.status = 'INC' AND up.user_id = u.user_id AND d.product_id = '$productID' AND d.vendor_id = v.vendor_id";
		}elseif ($agriculturalSector == 'poultries') {
			$query = "select up.profile_pic, u.user_id, CONCAT(u.first_name, ' ' , u.last_name) AS fullname, u.location, u.contact_no, d.demand_id, d.price, ptv.poultry_type_variety_name, d.needed_kilograms, d.received_kilograms, d.duration_end, d.description FROM users u, user_profile up, vendors v, demands d, poultry_type_varieties ptv WHERE d.variety_id = ptv.poultry_type_variety_id AND v.user_id = u.user_id AND d.agricultural_sector = 'poultries' AND d.status = 'INC' AND up.user_id = u.user_id AND d.product_id = '$productID' AND d.vendor_id = v.vendor_id";
		}elseif ($agriculturalSector == 'fisheries') {
			$query = "select up.profile_pic, u.user_id, CONCAT(u.first_name, ' ' , u.last_name) AS fullname, u.location, u.contact_no, d.demand_id, d.price, ftv.fish_type_variety_name, d.needed_kilograms, d.received_kilograms, d.duration_end, d.description FROM users u, user_profile up, vendors v, demands d, fish_type_varieties ftv WHERE d.variety_id = ftv.fish_type_variety_id AND v.user_id = u.user_id AND d.agricultural_sector = 'fisheries' AND d.status = 'INC' AND up.user_id = u.user_id AND d.product_id = '$productID' AND d.vendor_id = v.vendor_id";
		}
		if($stmt = $conn->prepare($query)){
			//executing the query 
			$stmt->execute();

			//binding results to the query 
			$stmt->bind_result($profile_pic, $user_id, $fullname, $location, $contact_no, $demand_id, $price, $variety_name, $needed_kilograms, $received_kilograms, $duration_end, $description); 
		}else{
			var_dump($stmt->error);
		}
		//traversing through all the result 
		$demanders = array();
		while($stmt->fetch()){
			$temp = array();
			if($profile_pic == null){
				$profile_pic = 'http://192.168.1.36/profileImages/noprofile.png';
			}
			$temp['profile_pic']		= $profile_pic;
			$temp['user_id']			= $user_id;
			$temp['fullname'] 			= $fullname; 
			$temp['location'] 			= $location; 
			$temp['contact_no'] 		= $contact_no; 
			$temp['demand_id']			= $demand_id; 
			$temp['price'] 				= $price; 
			$temp['variety_name'] 		= $variety_name; 
			$temp['needed_kilograms'] 	= $needed_kilograms; 
			$temp['received_kilograms']	= $received_kilograms; 
			$temp['duration_end'] 		= $duration_end; 
			$temp['description']		= $description; 
			array_push($demanders, $temp);
		}
	}
	$stmt->close();
}
//displaying the result in json format 
echo json_encode($demanders);