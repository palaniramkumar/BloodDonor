<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of dbConnection
 *
 * @author Mexico
 *
 *  /
 * 
 */
//session_start();

// DISPLAYS COMMENT POST TIME AS "1 year, 1 week ago" or "5 minutes, 7 seconds ago", etc...
function time_ago($date,$granularity=2) {
    $date = strtotime($date);
    $difference = time() - $date;
    $retval = "";
    $periods = array('decade' => 315360000,
        'year' => 31536000,
        'month' => 2628000,
        'week' => 604800, 
        'day' => 86400,
        'hour' => 3600,
        'minute' => 60,
        'second' => 1);
    if ($difference < 5) { // less than 5 seconds ago, let's say "just now"
        $retval = "posted just now";
        return $retval;
    } else {                            
        foreach ($periods as $key => $value) {
            if ($difference >= $value) {
                $time = floor($difference/$value);
                $difference %= $value;
                $retval .= ($retval ? ' ' : '').$time.' ';
                $retval .= (($time > 1) ? $key.'s' : $key);
                $granularity--;
            }
            if ($granularity == '0') { break; }
        }
        return ' Updated '.$retval.' ago';      
    }
}

class dbConnection {

    //put your code here
    //SSNC0lleg# [DB/username - unitev2, friendlyname;unitev2
    public function getConnection() {
        $mysql_host = "localhost";
        $mysql_database = "donorscloud";
        $mysql_user = "root";
        $mysql_password = "garc@somca";
        //$con = mysqli_connect("localhost", "root", "", "bloodzone");
        $con = mysqli_connect($mysql_host, $mysql_user, $mysql_password, $mysql_database);
        // Check connection
        if (mysqli_connect_errno()) {
		
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }
        return $con;
    }
	public function IS_SYNC($uid) {

        $con = $this->getConnection();
        $sql = "select * from user_info where id=$uid and last_update < now()";

        $result = mysqli_query($con, $sql); //write the password validation

        if (!$result) {
		$msg=true;
		mysqli_close($con);
		return ;
            //die('Invalid query: ' . $sql);
        }
		
		//echo $sql;
		$msg=true;
		if($row = mysqli_fetch_array($result)) {
			$msg=false;
		}
		
		
		
		mysqli_close($con);

		return $msg;
    }
    public function validateNotification($uid,$lat,$lon) {
		if($this->IS_SYNC($uid)==true)return; 
        $con = $this->getConnection();
        $sql = "select u.id,u.name,u.mobile,u.email_id,u.blood,u.lat,u.lon,m.msg from friends f, user_info u , msg_board m,user_info u1
where (f.uid= u.id or f.fid=u.id ) and (f.uid= '$uid' or f.fid='$uid')  and u.id<>'$uid' and m.uid in (u.id) and u1.id=$uid  and u1.last_update<=m.created_at";

		$file = 'people.log';				
		file_put_contents($file, $sql."\n", FILE_APPEND | LOCK_EX); 

        $result = mysqli_query($con, $sql); //write the password validation

        if (!$result) {

		mysqli_close($con);
		return;
            //die('Invalid query: ' . $sql);
        }
		
		//echo $sql;
		if($row = mysqli_fetch_array($result)) {
			$msg=$row[name]." Needs Your Help";
		}
		echo $msg;
		mysqli_close($con);
		$this->updatetime($uid);
		$this->updatelocation($uid,$lat,$lon);
		
		
		
    }
	

    public function updatelocation($uid, $lat,$lon) {

        if($lat==0 || $lon==0) return;
        $con = $this->getConnection();        

        $sql = "update app_blood_user_info set lat='$lat', lon='$lon' , last_location_update=now() where id=$uid ";
		$file = 'people.log';				
		file_put_contents($file, $sql."\n", FILE_APPEND | LOCK_EX); 
        $result = mysqli_query($con, $sql); //write the password validation

        if (!$result) {
		mysqli_close($con);
		return;
            //die('Invalid query: ' . $sql);
        }
		mysqli_close($con);
    }
    
    public function getMyFriends($uid,$lat,$lon) {

        $con = $this->getConnection();
        $sql = "select u.id,u.name,u.mobile,u.email_id,u.blood,u.lat,u.lon,(3956 * 2 *  ASIN(SQRT( POWER(SIN(($lat - abs(u.lat))*pi()/180/2),2)+COS($lat*pi()/180 )*COS(abs(u.lat)*pi()/180)*POWER(SIN(($lon-u.lon)*pi()/180/2),2))) *1.60934)  Distance ,last_location_update from  app_blood_friends f, app_blood_userinfo u 
where (f.uid= u.id or f.fid=u.id ) and (f.uid= '$uid' or f.fid='$uid')  and u.id<>'$uid'  group by u.id";

        $result = mysqli_query($con, $sql); //write the password validation

        if (!$result) {
            die('Invalid query: ' . $sql);
        }
		header('content-type:text/xml');
		echo "<BloodZone>";
		echo "<group>";
		echo "<header>";
		echo "<title>Friends List</title>";
		echo "<caption>Freind who are having your numbers</caption>";
		echo "<highlight>Your Help Needed</highlight>";
		echo "</header>";
		echo "<records>";
		if(mysqli_num_rows($result)==0){
			echo "<person>";
			echo "<name></name><mobile>No Friends List</mobile><blood></blood><lat>[lat]</lat><lon></lon>";
			echo "<message>You don't have any notification</message>";
			echo "<location></location>";
			echo "<distance></distance>";
			echo "<userid></userid>";
			echo "<ts></ts>";
			echo "</person>";	
		}
		while($row = mysqli_fetch_array($result)) {
		echo "<person>";
			echo "<name>$row[name]</name><mobile>$row[mobile]</mobile><blood>$row[blood]</blood><lat>$row[lat]</lat><lon>$row[lon]</lon>";
			echo "<message>$row[msg] </message>";
			echo "<location>$row[lat],$row[lon] </location>";
			echo "<distance>".round($row['Distance'])." KM (appx)</distance>";
			echo "<userid>$row[id]</userid>";
			echo "<ts>".time_ago($row['last_location_update'])."</ts>";
		echo "</person>";	
		}
	
		echo "</records>";
		echo "</group>";
		echo "</BloodZone>";
		
		mysqli_close($con);
    }
    
	public function getMyPushMsg($uid,$lat,$lon) {

        $con = $this->getConnection();
        $sql = "select u.id,u.name,u.mobile,u.email_id,u.blood,u.lat,u.lon,m.msg,(3956 * 2 *  ASIN(SQRT( POWER(SIN(($lat - abs(u.lat))*pi()/180/2),2)+COS($lat*pi()/180 )*COS(abs(u.lat)*pi()/180)*POWER(SIN(($lon-u.lon)*pi()/180/2),2))) *1.60934)  Distance,last_location_update  from app_blood_friends f, app_blood_userinfo u , app_blood_msgboard m
where (f.uid= u.id or f.fid=u.id ) and (f.uid= '$uid' or f.fid='$uid')  and u.id<>'$uid' and m.uid in (u.id) and DATE(created_at) = DATE(NOW()) group by u.id,m.msg";

        $result = mysqli_query($con, $sql); //write the password validation

        if (!$result) {
            die('Invalid query: ' . $sql);
        }
		header('content-type:text/xml');
		echo "<BloodZone>";
		echo "<group>";
		echo "<header>";
		echo "<title>Notification</title>";
		echo "<caption>Helps Required By Your Buddies</caption>";
		echo "<highlight>Your Help Needed</highlight>";
		echo "</header>";
		echo "<records>";
		if(mysqli_num_rows($result)==0){
			echo "<person>";
			echo "<name></name><mobile>No Alerts</mobile><blood></blood><lat></lat><lon></lon>";
			echo "<message>You don't have any notification</message>";
			echo "<location></location>";
			echo "<distance></distance>";
			echo "<userid>http://192.168.1.2/donorsclub/thumb-up.png</userid>";
			echo "<ts></ts>";
			echo "</person>";	
		}
		while($row = mysqli_fetch_array($result)) {
		echo "<person>";
			echo "<name>$row[name]</name><mobile>$row[mobile]</mobile><blood>$row[blood]</blood><lat>$row[lat]</lat><lon>$row[lon]</lon>";
			echo "<message>".ucfirst($row['msg']) ."</message>";
			echo "<location>$row[lat],$row[lon] </location>";
			echo "<distance>".round($row['Distance'])." KM (appx)</distance>";
			echo "<userid>$row[id]</userid>";
			echo "<ts>".time_ago($row['last_location_update'])."</ts>";
		echo "</person>";	
		}
	
		echo "</records>";
		echo "</group>";
		echo "</BloodZone>";
		
		mysqli_close($con);
    }
    
	public function getusers($uid, $lat,$lon,$group) {

        $con = $this->getConnection();
        $sql = "select * from 
				((select u.id,u.name,u.location,u.email_id,u.mobile,u.blood,u.lat,u.lon,'friend',if(blood='$group','Yes','No') status,(3956 * 2 *  ASIN(SQRT( POWER(SIN(($lat - abs(u.lat))*pi()/180/2),2)+COS($lat*pi()/180 )*COS(abs(u.lat)*pi()/180)*POWER(SIN(($lon-u.lon)*pi()/180/2),2))) *1.60934) Distance,u.last_location_update from app_blood_friends f, app_blood_userinfo u
				where blood='$group' and (f.uid= u.id or f.fid=u.id ) and (f.uid= '$uid' or f.fid='$uid')  and u.id<>'$uid') 	union ALL
				(select id,name,location,email_id,mobile,blood,lat,lon,'non-friend','Yes',(3956 * 2 *  ASIN(SQRT( POWER(SIN(($lat - abs(lat))*pi()/180/2),2)+COS($lat*pi()/180 )*COS(abs(lat)*pi()/180)*POWER(SIN(($lon-lon)*pi()/180/2),2))) *1.60934) Distance,last_location_update from app_blood_userinfo where blood='$group' and visibility=1 and id <>'$uid' having Distance < 100)
				) as merge_record group by mobile order by friend asc,status desc ";
		//echo $sql;
		$file = 'people.log';
		file_put_contents($file, $sql."\n", FILE_APPEND | LOCK_EX); 

        $result = mysqli_query($con, $sql); //write the password validation

        if (!$result) {
		mysqli_close($con);
		return;
            //die('Invalid query: ' . $sql);
        }
		$type=""; 
		
		header('content-type:text/xml');
		echo "<BloodZone>";
		$total=0;
		if(mysqli_num_rows($result)==0){
			echo "<group>";
			echo "<header>";
			echo "<title>Search Results for $group</title>";
			echo "<caption>Based on your Search for ". $group."</caption>";
			echo "<highlight>0 Match Found</highlight>";
			echo "</header>";
			echo "<records>";

			echo "<person>";
			echo "<name></name><mobile></mobile><blood></blood><lat></lat><lon></lon>";
			echo "<message>No Search Result Found</message>";
			echo "<location></location>";
			echo "<distance></distance>";
			echo "<userid></userid>";
			echo "</person>";	
			echo "</records>";
			echo "</group>";
			echo "</BloodZone>";

			mysqli_close($con);
			return;

		}
		while($row = mysqli_fetch_array($result)) {
			if($type!=($row['friend'].$row['status']) && $total!=0){
				
				echo "</records>";
				echo "</group>";
		
			}
			if($type!=($row['friend'].$row['status'])){
					
					echo "<group>";
					echo "<header>";
					echo "<title>Search Results for $group</title>";
					echo "<caption>Based on your Search with in $row[friend]</caption>";
					echo "<highlight>$row[friend]</highlight>";

					/*if($row[status]=='Yes')
						echo "<highlight>Matched</highlight>";
					else
						echo "<highlight>Non-Matched</highlight>";*/
					echo "</header>";
					echo "<records>";
						$type=($row['friend'].$row['status']);
			}
			
			echo "<person>";
			echo "<name>". ucfirst($row['name'])."</name><mobile>".$row['mobile']."</mobile><blood>".$row['blood']."</blood><lat>".$row['lat']."</lat><lon>".$row['lon']."</lon>";
			echo "<message></message>";
			echo "<location>".$row['lat'].",".$row['lon']." </location>";
			echo "<distance>".round($row['Distance'])." KM (appx) is ".time_ago($row['last_location_update'])."</distance>";
			echo "<userid>$row[id]</userid>";
			echo "</person>";	
			
			$total++;
		}
		echo "</records>";
		echo "</group>";
		echo "</BloodZone>";
		mysqli_close($con);
    }
    public function updatevisbility($uid, $flag) {

        $con = $this->getConnection();

        $sql = "update app_blood_user_info set visibility=$flag , last_update=now() where id=$uid ";
		$file = 'people.log';
		file_put_contents($file, $sql, FILE_APPEND | LOCK_EX); 

        $result = mysqli_query($con, $sql); //write the password validation

        if (!$result) {
		mysqli_close($con);
		return;
            //die('Invalid query: ' . mysql_error());
        }
		header('content-type:text/xml');
		echo "<BloodZone>";
		echo "<group>";
		echo "<header>";
		echo "<title>Profile Update</title>";
		echo "<caption>Change Search Visibility</caption>";
		echo "<highlight>Success</highlight>";
		echo "</header>";
		echo "<records>";
		
		echo "<person>";
			echo "<name></name><mobile></mobile><blood></blood><lat></lat><lon></lon>";
			echo "<message>Success</message>";
			echo "<location></location>";
			echo "<distance></distance>";
		echo "</person>";	
		
		echo "</records>";
		echo "</group>";
		echo "</BloodZone>";
	mysqli_close($con);
    }
	public function linkFriends($uid, $contacts) {

        $con = $this->getConnection();
        
        $sql = "select id from app_blood_userinfo where mobile in($contacts"."0) and id <> $uid";

		$file = 'people.log';
		file_put_contents($file, $sql, FILE_APPEND | LOCK_EX); 

        $result = mysqli_query($con, $sql); 

        if (!$result) {
		mysqli_close($con);
            //die('Invalid query: ' . $sql);
		return;
        }
		$con1 = $this->getConnection();
		while($row = mysqli_fetch_array($result)) {
			$sql="insert into app_blood_friends (uid,fid) values ($uid,$row[id])";
			$file = 'people.log';
			file_put_contents($file, $sql, FILE_APPEND | LOCK_EX); 
			$result1 = mysqli_query($con1, $sql); 
		}
		mysqli_close($con);
		mysqli_close($con1);
    }
    
    public function getProfilePic($uid) {

        $con = $this->getConnection();
        
        $sql = "select profile_pic from app_blood_userinfo where id = $uid";
        $result = mysqli_query($con, $sql);
		if (!$result) {
			mysqli_close($con);
			return;	
        }
		while($row = mysqli_fetch_array($result)) {
			header('content-type:image/png');
			echo base64_decode($row['profile_pic']);
		}
		mysqli_close($con);
    }

    public function createaccount($name, $blood_type, $email, $mobile, $location, $lat,$lon, $dob, $weight,$profile_pic,$token) {

        $con = $this->getConnection();
        $sql = "INSERT INTO `app_blood_userinfo`
        (
        `name`,
        `email_id`,
        `mobile`,
        `blood`,
        `lat`,
        `lon`,
        `location`,
		`weight`,
		`dob`,
		`profile_pic`,
        `visibility`,
        `token`,
        `last_update`,
        `created_time`,
        `last_location_update`)
        VALUES
        (
        '$name',
        '$email',
		'$mobile',
        '$blood_type',
		'$lat',
		'$lon',
        '$location',
		'$weight',
        '$dob',
        '$profile_pic',
        1,
        '$token',
        now(),
        now(),
        now())";
        $file = 'people.log';
		file_put_contents($file, $sql, FILE_APPEND | LOCK_EX); 
        $result = mysqli_query($con, $sql); //write the password validation
		
        if (!$result) {

           // die('Invalid query: ' . $sql);
        }
		if (mysqli_errno($con) == 1062) {
			$sql="select id from app_blood_userinfo where mobile='$mobile'";
			$result = mysqli_query($con, $sql); 
			if($row = mysqli_fetch_array($result)) {
				$incrementid=  $row["id"];
			}
		}
		else
			$incrementid=  mysqli_insert_id($con);
        mysqli_close($con);
		return $incrementid;
		
    }
	public function broadcast($uid,$msg,$lat,$lon) {

        $con = $this->getConnection();
        $sql = "insert into app_blood_msgboard (msg,uid,created_at,lat,lon) values ('$msg',$uid,now(),$lat,$lon)";
		//echo $sql;
        $result = mysqli_query($con, $sql); //write the password validation

        if (!$result) {
		mysqli_close($con);
		return;
            //die('Invalid query: ' . $sql);
        }
		header('content-type:text/xml');
		echo "<BloodZone>";
		echo "<group>";
		echo "<header>";
		echo "<title>Notification</title>";
		echo "<caption>Boadcasted your message to your friends</caption>";
		echo "<highlight>Success</highlight>";
		echo "</header>";
		echo "<records>";
		
		echo "<person>";
			echo "<name></name><mobile></mobile><blood></blood><lat></lat><lon></lon>";
			echo "<message>Your Message Broadcasted</message>";
			echo "<location></location>";
			echo "<distance></distance>";
		echo "</person>";	
		
		echo "</records>";
		echo "</group>";
		echo "</BloodZone>";
		
		mysqli_close($con);
		//$this->updatelocation($uid,$lat,$lon);
    }

    public function close($con) {
        mysqli_close($con);
    }
	
	public function updatetime($uid) {

        $con = $this->getConnection();
        $sql = "update app_blood_userinfo set last_update=now() where id=$uid ";
		//echo $sql;
		$file = 'people.log';
		file_put_contents($file, $sql, FILE_APPEND | LOCK_EX); 

        $result = mysqli_query($con, $sql); //write the password validation

        if (!$result) {
		mysqli_close($con);
		return;
            //die('Invalid query: ' . $sql);
        }
		
		mysqli_close($con);
    }
    public function updateToken($uid,$token) {

        $con = $this->getConnection();
        $sql = "update app_blood_userinfo set token='$token' where id=$uid ";
		//echo $sql;
		$file = 'people.log';
		file_put_contents($file, $sql, FILE_APPEND | LOCK_EX); 

        $result = mysqli_query($con, $sql); //write the password validation

        if (!$result) {
		mysqli_close($con);
		return;
            //die('Invalid query: ' . $sql);
        }
		
		mysqli_close($con);
    }
    
    function send_push_notification($registatoin_ids, $message) {
         
 
        // Set POST variables
        $url = 'https://android.googleapis.com/gcm/send';
 
        $fields = array(
            'registration_ids' => $registatoin_ids,
            'data' => array( "message" => $message ),
        );
 
        $headers = array(
            'Authorization: key=' . "AIzaSyCsQSWOtOcvytiIBJx2d1u5_llE8u9475k",
            'Content-Type: application/json'
        );
        //print_r($headers);
        // Open connection
        $ch = curl_init();
 
        // Set the url, number of POST vars, POST data
        curl_setopt($ch, CURLOPT_URL, $url);
 
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
 
        // Disabling SSL Certificate support temporarly
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
 
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
 
        // Execute post
        $result = curl_exec($ch);
        if ($result === FALSE) {
            die('Curl failed: ' . curl_error($ch));
        }
 
        // Close connection
        curl_close($ch);
        echo $result;
    }
    

}

?>
