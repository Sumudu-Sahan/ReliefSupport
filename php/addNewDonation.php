<?php
date_default_timezone_set('Asia/Colombo');
 $date = date('Y-m-d H:i:s');
 
	$name = $_POST['name'];
	$telephone = $_POST['telephone'];
	$address = $_POST['address'];
	$city = $_POST['city'];
	$donation = $_POST['donation'];
	$information = $_POST['information'];
        $identifier = $_POST['identifier'];
	include_once('connection.php');

	$query = "INSERT INTO donations (name, telephone, address, city, donation, information, created_at, updated_at, identifier) 
	
	VALUES ('$name', '$telephone', '$address', '$city', '$donation', '$information', '$date', '$date', '$identifier')";

	mysqli_query($connection, $query);
	
	$query2 = "INSERT INTO notification_details (notification_main_value, notification_sub_value_1, notification_sub_value_2, notification_sub_value_3, notification_sub_value_4) 
	
	VALUES ('[ReliefSupport] New Donation', '$name', '$address', '$donation', '$information')";

	mysqli_query($connection, $query2);
	
	$queryForNotifications = "
	SELECT
	
	device_token_text
	
	FROM 
	
	device_token_details ";
	
	$result = mysqli_query($connection, $queryForNotifications);
	$tokens = array();
	while($rows = mysqli_fetch_array($result)){
		$tokens[] = $rows['device_token_text'];
	}
	
	$url = 'https://roseless-seat.000webhostapp.com/reliefsupport/php/notificationAPI.php'; //URL
	$fields = array(
				'userToken' => $tokens,
				'mainValue' => "[ReliefSupport] New Donation",
				'subValue1' => $name,
				'subValue2' => $address,
				'subValue3' => $donation,
				'subValue4' => $information
			);
			
			//open connection
			$ch = curl_init();

			//set the url, number of POST vars, POST data
			curl_setopt($ch, CURLOPT_URL, $url);
			curl_setopt($ch, CURLOPT_POST, count($fields));
			curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($fields));

			//execute post
			curl_exec($ch);

			//close connection
			curl_close($ch);
			
			
mysqli_close($connection);
print json_encode(utf8ize($ress), JSON_UNESCAPED_SLASHES);

function utf8ize($d) {
    if (is_array($d)) {
        foreach ($d as $k => $v) {
            $d[$k] = utf8ize($v);
        }
    } else if (is_string ($d)) {
        return utf8_encode($d);
    }
    return $d;
}
        
?>