<?php 

require_once 'Constants.php';

//connecting to database and getting the connection object
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

//Checking if any error occured while connecting
if (mysqli_connect_errno()) {
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
	die();
}
//creating a query

if($_SERVER['REQUEST_METHOD'] == 'POST'){
	$con = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die('Unable to Connect');
		if(isset($_POST['user_id'])){
			$userID 			= $_POST['user_id'];
		$query = "select c.crop_image, d.demand_id, d.agricultural_sector, c.crop_name, cv.crop_variety_name, v.vendor_id, d.price, d.needed_kilograms, d.received_kilograms, d.date_demanded, d.duration_end, d.description, d.status from demands d, crops c, crop_varieties cv, vendors v WHERE d.product_id = c.crop_id AND cv.crop_variety_id = d.variety_id AND d.vendor_id = v.vendor_id AND v.user_id = '$userID' AND d.agricultural_sector = 'crops' AND NOT d.status = 'EXP' order by d.demand_id";
		$query1 = "select f.fish_type_image, d.demand_id, d.agricultural_sector, f.fish_type_name, fv.fish_type_variety_name, v.vendor_id, d.price, d.needed_kilograms, d.received_kilograms, d.date_demanded, d.duration_end, d.description, d.status from demands d, fisheries f, fish_type_varieties fv, vendors v WHERE d.product_id = f.fish_type_id AND fv.fish_type_variety_id = d.variety_id AND d.vendor_id = v.vendor_id AND v.user_id = '$userID' AND d.agricultural_sector = 'fisheries' AND NOT d.status = 'EXP' order by d.demand_id";
		$query2 = "select l.livestock_type_image, d.demand_id, d.agricultural_sector, l.livestock_type_name, ltv.livestock_type_variety_name, v.vendor_id, d.price, d.needed_kilograms, d.received_kilograms, d.date_demanded, d.duration_end, d.description, d.status from demands d, livestocks l, livestock_type_varieties ltv, vendors v WHERE d.product_id = l.livestock_type_id AND ltv.livestock_type_variety_id = d.variety_id AND d.vendor_id = v.vendor_id AND v.user_id = '$userID' AND d.agricultural_sector = 'livestocks' AND NOT d.status = 'EXP' order by d.demand_id";
		$query3 = "select p.poultry_type_image, d.demand_id, d.agricultural_sector, p.poultry_type_name, ptv.poultry_type_variety_name, v.vendor_id, d.price, d.needed_kilograms, d.received_kilograms, d.date_demanded, d.duration_end, d.description, d.status from demands d, poultries p, poultry_type_varieties ptv, vendors v WHERE d.product_id = p.poultry_type_id AND ptv.poultry_type_variety_id = d.variety_id AND d.vendor_id = v.vendor_id AND v.user_id = '$userID' AND d.agricultural_sector = 'poultries' AND NOT d.status = 'EXP' order by d.demand_id";
		if($stmt1 = $conn->prepare($query)){
			//executing the query 
			$stmt1->execute();

			//binding results to the query 
			$stmt1->bind_result($product_image, $demand_id, $agricultural_sector, $product_name, $product_variety_name, $vendor_id, $price, $needed_kilograms, $received_kilograms, $date_demanded, $duration_end, $description, $status); 
		}else{
			var_dump($stmt1->error);
		}
		//traversing through all the result 
		$products = array();
		$row_number = 0;
		while($stmt1->fetch()){
			$temp = array();
			$row_number++;
			$temp['product_image']			= $product_image;
			$temp['demand_id']				= $demand_id;
			$temp['agricultural_sector']	= $agricultural_sector; 
			$temp['product_name']			= $product_name; 
			$temp['product_variety_name']	= $product_variety_name; 
			$temp['vendor_id']				= $vendor_id; 
			$temp['price']					= $price;
			$temp['needed_kilograms']		= $needed_kilograms;
			$temp['received_kilograms']		= $received_kilograms; 
			$temp['date_demanded']			= $date_demanded; 
			$temp['duration_end']			= $duration_end; 
			$temp['description']			= $description;
			$temp['status']					= $status;  
			array_push($products, $temp);
		}
		
		if($stmt2 = $conn->prepare($query1)){
			//executing the query 
			$stmt2->execute();

			//binding results to the query 
			$stmt2->bind_result($product_image, $demand_id, $agricultural_sector, $product_name, $product_variety_name, $vendor_id, $price, $needed_kilograms, $received_kilograms, $date_demanded, $duration_end, $description, $status); 
		}else{
			var_dump($stmt2->error);
		}
		//traversing through all the result 
		while($stmt2->fetch()){
			$temp = array();
			$row_number++;
			$temp['product_image']			= $product_image;
			$temp['demand_id']				= $demand_id;
			$temp['agricultural_sector']	= $agricultural_sector; 
			$temp['product_name']			= $product_name; 
			$temp['product_variety_name']	= $product_variety_name; 
			$temp['vendor_id']				= $vendor_id; 
			$temp['price']					= $price;
			$temp['needed_kilograms']		= $needed_kilograms;
			$temp['received_kilograms']		= $received_kilograms; 
			$temp['date_demanded']			= $date_demanded; 
			$temp['duration_end']			= $duration_end; 
			$temp['description']			= $description;
			$temp['status']					= $status; 
			array_push($products, $temp);
		}

		if($stmt3 = $conn->prepare($query2)){
			//executing the query 
			$stmt3->execute();

			//binding results to the query 
			$stmt3->bind_result($product_image, $demand_id, $agricultural_sector, $product_name, $product_variety_name, $vendor_id, $price, $needed_kilograms, $received_kilograms, $date_demanded, $duration_end, $description, $status); 
		}else{
			var_dump($stmt3->error);
		}
		//traversing through all the result 
		while($stmt3->fetch()){
			$temp = array();
			$row_number++;
			$temp['product_image']			= $product_image;
			$temp['demand_id']				= $demand_id;
			$temp['agricultural_sector']	= $agricultural_sector; 
			$temp['product_name']			= $product_name; 
			$temp['product_variety_name']	= $product_variety_name; 
			$temp['vendor_id']				= $vendor_id; 
			$temp['price']					= $price;
			$temp['needed_kilograms']		= $needed_kilograms;
			$temp['received_kilograms']		= $received_kilograms; 
			$temp['date_demanded']			= $date_demanded; 
			$temp['duration_end']			= $duration_end; 
			$temp['description']			= $description;
			$temp['status']					= $status; 
			array_push($products, $temp);
		}
		if($stmt4 = $conn->prepare($query3)){
			//executing the query 
			$stmt4->execute();

			//binding results to the query 
			$stmt4->bind_result($product_image, $demand_id, $agricultural_sector, $product_name, $product_variety_name, $vendor_id, $price, $needed_kilograms, $received_kilograms, $date_demanded, $duration_end, $description, $status); 
		}else{
			var_dump($stmt4->error);
		}
		//traversing through all the result 
		while($stmt4->fetch()){
			$temp = array();
			$row_number++;
			$temp['product_image']			= $product_image;
			$temp['demand_id']				= $demand_id;
			$temp['agricultural_sector']	= $agricultural_sector; 
			$temp['product_name']			= $product_name; 
			$temp['product_variety_name']	= $product_variety_name; 
			$temp['vendor_id']				= $vendor_id; 
			$temp['price']					= $price;
			$temp['needed_kilograms']		= $needed_kilograms;
			$temp['received_kilograms']		= $received_kilograms; 
			$temp['date_demanded']			= $date_demanded; 
			$temp['duration_end']			= $duration_end; 
			$temp['description']			= $description;
			$temp['status']					= $status; 
			array_push($products, $temp);
		}
	}
	$stmt1->close();
	$stmt2->close();
	$stmt3->close();
	$stmt4->close();
}

//displaying the result in json format 
echo json_encode($products);