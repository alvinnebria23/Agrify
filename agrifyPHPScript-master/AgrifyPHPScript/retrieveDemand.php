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
		
		if($agriculturalSector == 'crops'){
			$query = "Select t1.crop_id, t1.crop_image, t1.crop_name, t1.needed_kilograms - t1.received_kilograms as demand_today, t2.demand_yesterday FROM (SELECT c.crop_id, c.crop_image, c.crop_name,SUM(d.needed_kilograms) as needed_kilograms, d.received_kilograms FROM crops c, demands d WHERE d.product_id = c.crop_id AND d.agricultural_sector = 'crops' AND d.status = 'INC' GROUP By c.crop_name) as T1 LEFT JOIN (SELECT c.crop_name, d.needed_kilograms as demand_yesterday from crops c, demands d WHERE c.crop_id = d.product_id AND d.date_demanded = '$dateYesterday' AND d.agricultural_sector = 'crops') as t2 on t1.crop_name = t2.crop_name";
		}elseif ($agriculturalSector == 'livestocks') {
			$query = "select t1.livestock_type_id, t1.livestock_type_image, t1.livestock_type_name, t1.needed_kilograms - t1.received_kilograms as demand_today, t2.demand_yesterday FROM (SELECT l.livestock_type_id, l.livestock_type_image, l.livestock_type_name, SUM(d.needed_kilograms) as needed_kilograms, d.received_kilograms FROM livestocks l, demands d WHERE d.product_id = l.livestock_type_id AND d.agricultural_sector = 'livestocks' AND d.status = 'INC' GROUP By l.livestock_type_name) as T1 LEFT JOIN (SELECT l.livestock_type_name, d.needed_kilograms as demand_yesterday from livestocks l, demands d WHERE l.livestock_type_id = d.product_id AND d.date_demanded = '$dateYesterday' AND agricultural_sector = 'livestocks') as t2 on t1.livestock_type_name = t2.livestock_type_name";
		}elseif ($agriculturalSector == 'poultries') {
			$query = "select t1.poultry_type_id, t1.poultry_type_image, t1.poultry_type_name, t1.needed_kilograms - t1.received_kilograms as demand_today, t2.demand_yesterday FROM (SELECT p.poultry_type_id, p.poultry_type_image, p.poultry_type_name, SUM(d.needed_kilograms) as needed_kilograms, d.received_kilograms FROM poultries p, demands d WHERE d.product_id = p.poultry_type_id AND d.agricultural_sector = 'poultries' AND d.status = 'INC' GROUP By p.poultry_type_name) as T1 LEFT JOIN (SELECT p.poultry_type_name, d.needed_kilograms as demand_yesterday from poultries p, demands d WHERE p.poultry_type_id = d.product_id AND d.date_demanded = '$dateYesterday' AND agricultural_sector = 'poultries') as t2 on t1.poultry_type_name = t2.poultry_type_name";
		}elseif ($agriculturalSector == 'fisheries') {
			$query = "select t1.fish_type_id, t1.fish_type_image, t1.fish_type_name, t1.needed_kilograms - t1.received_kilograms as demand_today, t2.demand_yesterday FROM (SELECT f.fish_type_id, f.fish_type_image, f.fish_type_name, SUM(d.needed_kilograms) as needed_kilograms, d.received_kilograms FROM fisheries f, demands d WHERE d.product_id = f.fish_type_id AND d.agricultural_sector = 'fisheries' AND d.status = 'INC' GROUP By f.fish_type_name) as T1 LEFT JOIN (SELECT f.fish_type_name, d.needed_kilograms as demand_yesterday from fisheries f, demands d WHERE f.fish_type_id = d.product_id AND d.date_demanded = '$dateYesterday' AND agricultural_sector = 'fisheries') as t2 on t1.fish_type_name = t2.fish_type_name";
		}
		if($stmt = $conn->prepare($query)){
			//executing the query 
			$stmt->execute();

			//binding results to the query 
			$stmt->bind_result($product_id, $product_image, $product_name, $demand_today, $demand_yesterday); 
		}else{
			var_dump($stmt1->error);
		}
		//traversing through all the result 
		$products = array();
		$row_number = 0;
		while($stmt->fetch()){
			if($demand_yesterday == null){
				$demand_yesterday = 0;
			}
			$temp = array();
			$row_number++;
			$temp['row_number']			= $row_number;
			$temp['product_id']			= $product_id;
			$temp['product_image'] 		= $product_image; 
			$temp['product_name'] 		= $product_name; 
			$temp['demand_today'] 		= $demand_today; 
			$temp['demand_yesterday']	= $demand_yesterday; 
			array_push($products, $temp);
		}
	}
	$stmt->close();
}

//displaying the result in json format 
echo json_encode($products);