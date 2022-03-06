<?php 
 
 //importing dbDetails file 
 require_once 'Constants.php';
 
 //this is our upload folder 
 $upload_path = 'profileImages/';
 
 //Getting the server ip 
 $server_ip = gethostbyname(gethostname());
 
 //creating the upload url 
 $upload_url = 'http://'.$server_ip.'/'.$upload_path; 
 
 //response array 
 $response = array(); 

 if($_SERVER['REQUEST_METHOD']=='POST'){ 
 	if(isset($_POST['user_id'])){
 	$user_id	 	= $_POST['user_id'];
 	$name       	= $_FILES['image']['name']; 
 	$temp_name  	= $_FILES['image']['tmp_name'];  
 	$profile_description 	= $_POST['profile_description'];
	
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
				if(move_uploaded_file($temp_name, 'D:\\Program Files\\xampp\\htdocs\\profileImages\\'.$name.'.'.$extension)){
				 	echo "uploaded";
				 }else{
				 	echo "error file upload";
				 }
				 $sql = "INSERT INTO `agrify`.`user_profile` (`user_id`, `profile_pic`, `profile_description`) VALUES ('$user_id', '$file_url', '$profile_description');";
				 //adding the path and name to database 
				 if(mysqli_query($con,$sql)){
				 
					 //filling response array with values 
					 echo "success";
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