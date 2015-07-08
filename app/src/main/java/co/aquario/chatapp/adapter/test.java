package co.aquario.chatapp.adapter;

/**
 * Created by Mac on 7/7/15.
 */
public class test {

//
//    <?php
//    header('Content-type:application/json; charset=utf-8');
//    ini_set('display_errors', 'On');
//    include("noti.php");
//
//
//// http://armymax.com/api/noti/?title=ArmyMax%20Conference&m=arai%20na&f=31&n=ArmyMax1&t=1&type=500
//// http://armymax.com/api/noti/?title=ArmyMax%20Conference&m=arai%20na%20to%20all&f=31&n=ArmyMax1&t=1&type=500&all=1
//
//
//
//    if(
//    isset($_GET["title"]) &&
//    isset($_GET["m"]) &&
//    isset($_GET["f"]) &&
//    isset($_GET["n"]) &&
//    isset($_GET["t"]) &&
//    isset($_GET["type"]) &&
//    $_GET["f"] != $_GET["t"]
//            )
//    {
//        $title = $_GET["title"];
//        $m = $_GET["m"];
//        $f = $_GET["f"];
//        $n = $_GET["n"];
//        $t = $_GET["t"];
//        $type = $_GET["type"];
//
//        if(isset($_GET["all"])) {
//            $where = array(
//                    "deviceType" => array("\$in"=>array("android","ios"))
//            );
//        } else {
//            $where = array(
//                    // "deviceType" => "android",
//                    "user_id" => $t
//            );
//        }
//
//        echo "666";
//
//
//
//        // Additional Param
//        $badge 	= getUnreadNoti($t) + 1;
//        $type 	= intval($type);
//
//        //echo $TYPES_chatMessage." @@@@@@";
//
//        switch($type) {
//            case $TYPES_likeFeed:
//                $data = array(
//                        "type" => $type,
//                        "title" => $title,
//                    "alert" => $m,
//                    "from_id" => $f,
//                    "from_name" => $n,
//                    "to_id" => $t,
//                    "post_id" => $_GET["post_id"],
//                );
//                insertNoti($f,$n,$t,$m,$type,$_GET["post_id"]);
//
//                //$insert = "http://armymax.com/api/noti/noti.php?a=insert&f=".$f."&t=".$t."&msg=".$m."&type=".$type."&post_id=".$_GET["post_id"];
//                //file_get_contents($insert);
//
//		/*
//		$curl = curl_init();
//		curl_setopt_array($curl, array(
//			CURLOPT_RETURNTRANSFER => 1,
//			CURLOPT_URL => $insert
//		));
//
//		$result = curl_exec($curl);
//		curl_close($curl);
//		//echo $insert;
//		*/
//
//                break;
//            case $TYPES_commentFeed:
//                $data = array(
//                        "type" => $type,
//                        "title" => $title,
//                    "alert" => $m,
//                    "from_id" => $f,
//                    "from_name" => $n,
//                    "to_id" => $t,
//                    "post_id" => $_GET["post_id"]
//                );
//
//                insertNoti($f,$n,$t,$m,$type,$_GET["post_id"]);
//
//                break;
//            case $TYPES_liveNow:
//                $data = array(
//                        "type"=> $type,
//                        "title" => $title,
//                    "alert" => $m,
//                    "from_id" => $f,
//                    "from_name" => $n,
//                    "to_id" => $t,
//                    "type" => $type,
//                    "post_id" => $_GET["post_id"]
//                );
//                //file_get_contents("http://armymax.com/api/noti/noti.php?a=insert&f=".$f."&t=".$t."&msg=".$m."&type=".$type);
//                insertNoti($f,$n,$t,$m,$type,$_GET["post_id"]);
//                break;
//
//            case $TYPES_followedYou:
//                $data = array(
//                        "type"=> $type,
//                        "title" => $title,
//                    "alert" => $m,
//                    "from_id" => $f,
//                    "from_name" => $n,
//                    "to_id" => $t
//                );
//                insertNoti($f,$n,$t,$m,$type,$_GET["post_id"]);
//                break;
//
//            case $TYPES_chatMessage:
//            case $TYPES_chatSticker:
//            case $TYPES_chatFile:
//            case $TYPES_chatLocation:
//            case $TYPES_chatUserProfile:
//            case $TYPES_chatYoutube:
//            case $TYPES_chatSoundcloud:
//            case $TYPES_chatVoiceMessage:
//            case $TYPES_chatSystem:
//            case $TYPES_groupInvite:
//                $data = array(
//                        "type"=> $type,
//                        "title" => $title,
//                    "alert" => $m,
//                    "from_id" => $f,
//                    "from_name" => $n,
//                    "from_avatar" => getAvatarPath($f),
//                    "to_id" => $t,
//                    "action" => "co.aquario.vmmax.PUSH_NOTIFICATION",
//                    "customdata" => "GOD LIKE ! (300,500,501,502,503 Chat)"
//                );
//                insertNoti($f,$n,$t,$m,$type);
//                break;
//
//            case $TYPES_chatFreeCall:
//            case $TYPES_chatVideoCall:
//                $data = array(
//                        "type"=> $type,
//                        "title" => $title,
//                    "alert" => $m,
//                    "from_id" => $f,
//                    "from_name" => $n,
//                    "from_avatar" => getAvatarPath($f),
//                    "to_id" => $t,
//                    "type" => $type,
//                    "session" => $_GET["session"],
//                    "action" => "co.aquario.vmmax.PUSH_NOTIFICATION",
//                    "customdata" => "GOD LIKE ! (504,505 Chat)"
//                );
//                insertNoti($f,$n,$t,$m,$type);
//                break;
//            //file_get_contents("http://armymax.com/api/noti/noti.php?a=insert&f=".$f."&t=".$t."&msg=".$m."&type=".$type);
//
//            break;
//            case $TYPES_chatInviteGroup:
//                $data = array(
//                        "type"=> $type,
//                        "title" => $title,
//                    "alert" => $m,
//                    "from_id" => $f,
//                    "from_name" => $n,
//                    "from_avatar" => getAvatarPath($f),
//                    "to_id" => $t,
//                    "type" => $type,
//                    "cid" => $_GET["cid"],
//                    "extra" => $_GET["extra"],
//                    "chat_msg" => $m,
//                    "action" => "co.aquario.vmmax.PUSH_NOTIFICATION",
//                    "customdata" => "GOD LIKE ! (506 invite chat group)"
//                );
//                //file_get_contents("http://armymax.com/api/noti/noti.php?a=insert&f=".$f."&t=".$t."&msg=".$m."&type=".$type);
//                insertNoti($f,$n,$t,$m,$type,$_GET["cid"],$_GET["extra"]);
//                break;
//
//            case $TYPES_confInvite:
//                $data = array(
//                        "type"=> $type,
//                        "title" => $title,
//                    "alert" => $m,
//                    "from_id" => $f,
//                    "from_name" => $n,
//                    "from_avatar" => getAvatarPath($f),
//                    "to_id" => $t,
//                    "type" => $type,
//                    "room_name" => $_GET["room_name"],
//                    "action" => "co.aquario.vmmax.PUSH_NOTIFICATION",
//                    "customdata" => "GOD LIKE ! (600,601,602 conference)"
//                );
//                break;
//            case $TYPES_confCreate:
//            case $TYPES_confJoin:
//                if(isset($_GET["cid"])) {
//                    $data = array(
//                            "type"=> $type,
//                            "title" => $title,
//                            "alert" => $m,
//                            "from_id" => $f,
//                            "from_name" => $n,
//                            "from_avatar" => getAvatarPath($f),
//                            "to_id" => $t,
//                            "type" => $type,
//                            "cid" => $_GET["cid"],
//                            "room_name" => $_GET["room_name"]
//                    );
//                } else {
//                    $data = array(
//                            "type"=> $type,
//                            "title" => $title,
//                            "alert" => $m,
//                            "from_id" => $f,
//                            "from_name" => $n,
//                            "from_avatar" => getAvatarPath($f),
//                            "to_id" => $t,
//                            "type" => $type,
//                            "room_name" => $_GET["room_name"]
//                    );
//                }
//                insertNoti($f,$n,$t,$m,$type,$_GET["cid"],$_GET["room_name"]);
//                //file_get_contents("http://armymax.com/api/noti/noti.php?a=insert&f=".$f."&t=".$t."&msg=".$m."&type=".$type."&post_id=".$_GET["room_name"]);
//
//                break;
//
//        }
//
//        //Default for all
//        $data['badge'] = $badge;
//
//        //echo $_GET['type'];
//
//
//        $cURLHandler = curl_init();
//        $url = "https://api.parse.com/1/push";
//        // if(isset($_GET["all"])) {
//        // 	$send_json = array(
//        // 	"data" => $data
//        // 	);
//        // } else {
//        $send_json = array(
//                "where" => $where,
//                "data" => $data
//        );
//        // }
//
//        $a = json_encode($send_json);
//
//        if($cURLHandler) {
//            curl_setopt($cURLHandler, CURLOPT_HTTPHEADER, array("X-Parse-Application-Id: j6DTfeUL6JvI9PunllRInpQbUg3dJLCVNTvaAOfY","X-Parse-REST-API-Key: scpgvOkSsPgF9cEHVH2U8IFYkF3maZV0cOfQmsu0","Content-Type: application/json"));
//            curl_setopt($cURLHandler, CURLOPT_BINARYTRANSFER, 1);
//            curl_setopt($cURLHandler, CURLOPT_POST, 1);
//            curl_setopt($cURLHandler, CURLOPT_PORT,443);
//            curl_setopt($cURLHandler, CURLOPT_RETURNTRANSFER, 1);
//            curl_setopt($cURLHandler, CURLOPT_POSTFIELDS, $a);
//            curl_setopt($cURLHandler, CURLOPT_URL, $url);
//            $content = curl_exec($cURLHandler);
//            curl_close($cURLHandler);
//
//            //$send_json["response"] = $content;
//            //$b = json_encode($send_json);
//            $b = json_encode($content);
//            echo $b;
//
//        }
//        else {
//            throw new RuntimeException("CURL Exception");
//        }
//    } else {
//
//        getAvatarPath(21);
//
//	/*
//
//	echo " TITLE: ".$_GET["title"];
//	echo " MSG: ".$_GET["m"];
//	echo " FROM: ".($_GET["f"]);
//	echo " NAME: ".($_GET["n"]) ;
//	echo " TO: ".($_GET["t"]);
//	echo " TYPE: ".($_GET["type"]);
//
//	echo "param error";
//	*/
//    }
//
//    function dbConnect(){
//        $DBServer = '150.107.31.15';
//        $DBUser   = 'root';
//        $DBPass   = 'Nchl33&rVx';
//        $DBName   = 'vmmax_chat';
//        //DB Connect
//        $db = new mysqli($DBServer, $DBUser, $DBPass, $DBName);
//        $db->set_charset("utf8");
//        return $db;
//    }
//
//
//    function getAvatarPath($id) {
//        $db = dbConnect();
//        $sql = "SELECT
//        UserID ,
//                UserToken ,
//                UserName ,
//                UserAvatarPath ,
//                UserAvatarPathMedium
//        FROM users  WHERE UserID = {$id}";
//
//        $getAvatar = $db->query($sql);
//
//
//        $rows = fetchAll($getAvatar);
//
//        //print_r($rows);
//
//        return $rows[0]["UserAvatarPath"];
//
//    }
//
//    function fetchAll($result)
//    {
//        if ($result !== false) {
//            while($row = $result->fetch_assoc()) {
//                //print_r($row);
//                $rows[] = $row;
//            }
//            $result->close();
//            return $rows;
//        }
//    }
//
//    ?>

}
