<?php
require_once('dbConnection.php');
function executeURL($url){
            $ch = curl_init();

        curl_setopt($ch, CURLOPT_URL, $url);
      
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);

        $output = curl_exec($ch);
        curl_close($ch);
        return $output;
    
}
//request debugger
$file = 'people.log';
$actual_link = "http://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]";
$param = "";
foreach($_REQUEST as $key=>$value){
 $param .=  $key . '=' . $value . "&";
}
file_put_contents($file, $actual_link.$param."\n", FILE_APPEND | LOCK_EX); 


if ($_REQUEST["action"] == "NEW_ACCOUNT") {
    $con = new dbConnection();
    $name=$_REQUEST ['name'];
    $lat=$_REQUEST ['lat'];
	$lon=$_REQUEST ['lon'];
    $blood_type=$_REQUEST ['blood_type'];
    $email=$_REQUEST ['email'];
    $mobile=$_REQUEST ['mobile'];
    $location=$_REQUEST ['location'];
    $dob=$_REQUEST ['dob'];
    $weight=$_REQUEST ['weight'];
    $profile_pic=$_REQUEST ['profile_pic'];
    $token=$_REQUEST ['token'];
	//$contacts=$_REQUEST ['contacts']; //igonring for now
	if(trim($location)==""){
		$map_url= "http://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lon&sensor=true";
		$getResult=executeURL($map_url);
		//echo $getResult;

		$json = json_decode($getResult, true);
   		$location= $json['results'][0]['formatted_address'];	
	}
    $recid=$con->createaccount($name, $blood_type, $email, $mobile, $location, $lat,$lon, $dob, $weight,$profile_pic,$token);
	
	//$con->linkFriends($recid,$contacts); //commeting temp
	echo $recid;
}
else if ($_REQUEST ["action"] == "LINK_FRIENDS") {
    $con = new dbConnection();
    $uid=$_REQUEST ['uid'];
    $contacts=$_REQUEST['contacts'];
    $con->linkFriends($uid,$contacts);
}
else if ($_REQUEST ["action"] == "GET_USER") {
    $con = new dbConnection();
    $uid=$_REQUEST ['uid'];
    $lat=$_REQUEST['lat'];
	$lon=$_REQUEST['lon'];
	$group=$_REQUEST['blood'];
    $con->getusers($uid, $lat,$lon,$group);
}
else if ($_REQUEST ["action"] == "GET_PROFILE_PIC") {
    $con = new dbConnection();
    $uid=$_REQUEST ['uid'];
    $con->getProfilePic($uid);
}
else if ($_REQUEST ["action"] == "MYFRIENDS") {
    $con = new dbConnection();
    $uid=$_REQUEST ['uid'];
    $lat=$_REQUEST['lat'];
	$lon=$_REQUEST['lon'];
    $con->getMyFriends($uid,$lat,$lon);
}
else if ($_REQUEST ["action"] == "BROADCAST") {
    $con = new dbConnection();
    $uid=$_REQUEST ['uid'];
    $lat=$_REQUEST['lat'];
	$lon=$_REQUEST['lon'];
	$msg=$_REQUEST['blood'];
    //$con->broadcast($uid,$msg, $lat,$lon);
    $tmp_token = array("APA91bF7HLVdw51zkygmigk9WLMoMsk--HuEJeSSIL9r7P0BglXmH0iYfOu7Qo4uODkhl4ouqhk91BbdrE-q-xB4wkgokLR4HDpj7M61y-_ki7Mi5v1HaQbxwbksv_VYOy5k5kowkYEsE8qE9t8FvYT8cfMvVL0WGA");
    $con->send_push_notification($tmp_token,"Need A1+");
}
else if ($_REQUEST ["action"] == "GET_MSG") {
    $con = new dbConnection();
    $uid=$_REQUEST ['uid'];
	$lat=$_REQUEST['lat'];
	$lon=$_REQUEST['lon'];
	$token=$_REQUEST['token'];
    $con->getMyPushMsg($uid,$lat,$lon);
    $con->updateToken($uid,$token);
}
else if ($_REQUEST ["action"] == "NOTIFY") {
    $con = new dbConnection();
    $uid=$_REQUEST ['uid'];
	$lat=$_REQUEST ['lat'];
	$lon=$_REQUEST ['lon'];
    $con->validateNotification($uid,$lat,$lon);
}
else if ($_REQUEST ["action"] == "UPDATE_TOKEN") {
    $con = new dbConnection();
    $uid=$_REQUEST ['uid'];
    $token=$_REQUEST ['token'];
    $con->updateToken($uid,$token);
}
else if ($_REQUEST ["action"] == "UPDATE_LOC") {
    $con = new dbConnection();
    $uid=$_REQUEST ['uid'];
	$lat=$_REQUEST ['lat'];
	$lon=$_REQUEST ['lon'];
    $con->updatelocation($uid,$lat,$lon);
}
else if ($_REQUEST ["action"] == "UPDATE_VISIBILITY") {
    $con = new dbConnection();
    $uid=$_REQUEST ['uid'];
    $visibility=$_REQUEST['blood'];
    $con->updatevisbility($uid, $visibility);
}
else{
	$map_url= "http://maps.googleapis.com/maps/api/geocode/json?latlng=13.13,80.18&sensor=true";
	$getResult=executeURL($map_url);
	//echo $getResult;
	$json = json_decode($getResult, true);
    echo $json['results'][0]['formatted_address'];
}
?>
