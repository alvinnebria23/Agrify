<?php 
 
 //importing dbDetails file 
 require_once 'Constants.php';
 
 //this is our upload folder 
 $upload_path = 'fisheriesImages/';
 
 //Getting the server ip 
 $server_ip = gethostbyname(gethostname());
 
 //creating the upload url 
 $upload_url = 'http://'.$server_ip.'/'.$upload_path; 
 
 //response array 
 $response = array(); 

 if($_SERVER['REQUEST_METHOD']=='POST'){ 
 	if(isset($_POST['agricultural_sector'])){
 	$agriculturalSector = $_POST['agricultural_sector'];
 	$name       		= $_FILES['image']['name'];  
	$temp_name  		= $_FILES['image']['tmp_name']; 
		 if(isset($name)){
		 	if(!empty($name)){
		 	//connecting to the database 
			 $con = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die('Unable to Connect...');
			 
			 //getting name from the request 
			 $name = $_POST['name'];
			 
			 //getting file info from the request 
			 $fileinfo = pathinfo($_FILES['image']['name']);
			 
			 //getting the file extension 
			 $extension = $fileinfo['extension'];
			 
			 //file url to store in the database 
			 $file_url = $upload_url . $name . '.' . $extension;
			
			 //trying to save the file in the directory 
			 try{
			 	//saving the file 
				if(move_uploaded_file($temp_name, 'D:\\Program Files\\xampp\\htdocs\\fisheriesImages\\'.$name.'.'.$extension)){
				 	echo "uploaded";
				 }else{
				 	echo "error file upload";
				 }
				 if($agriculturalSector == 'crops'){
					 $sql = "INSERT INTO `agrify`.`crops` (`crop_id`, `crop_name`, `crop_image`) VALUES (NULL, '$name', '$file_url');";
				 }elseif ($agriculturalSector == 'livestocks') {
				 	 $sql = "INSERT INTO `agrify`.`livestocks` (`livestock_type_id`, `livestock_type_name`, `livestock_type_image`) VALUES (NULL, '$name', '$file_url');";
				 }elseif ($agriculturalSector == 'poultries') {
				 	 $sql = "INSERT INTO `agrify`.`poultries` (`poultry_type_id`, `poultry_type_name`, `poultry_type_image`) VALUES (NULL, '$name', '$file_url');";
				 }elseif ($agriculturalSector == 'fisheries') {
				 	$sql = "INSERT INTO `agrify`.`fisheries` (`fish_type_id`, `fish_type_name`, `fish_type_image`) VALUES (NULL, '$name', '$file_url');";
				 }
				 //adding the path and name to database 
				 if(mysqli_query($con,$sql)){
				 
					 //filling response array with values 
					 $response['error'] = false; 
					 $response['crop_image'] = $file_url; 
					 $response['crop_name'] = $name;
				 }
			 //if some error occurred 
			 }catch(Exception $e){
				 $response['error']=true;
				 $response['message']=$e->getMessage();
			 } 
			 //displaying the response 
			 echo json_encode($response);
			 
			 //closing the connection 
			 mysqli_close($con);
			 }else{
				 $response['error']=true;
				 $response['message']='Please choose a file';
			}
		}
	}
}