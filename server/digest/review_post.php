<?php
$title = $_POST['title'];
$abstract = $_POST['abstract'];
$url = $_POST["url"];
$thumbnail = $_POST['thumbnail'];
$type = $_POST["type"];
$status = $_POST["status"];
$reviewer = $_POST["reviewer"];
$deliver = $_POST["deliver"];
$create_at = $_POST["create_at"];
$delete = $_POST["delete"];

if (empty($title)) {
    die("{\"s_status\": \"ERROR\",\"s_message\": \"Empty data\",\"s_code\": 0}");
}

$mysql = new SaeMysql();

$sql = "INSERT  INTO `android_digest_review`( `title`, `abstract`, `url`, `thumbnail`, `type`, `status`, `reviewer`, `deliver`, `create_at`, `delete`) "
    . "VALUES ('" . $title . "', '"
                  . $abstract . "', '"
                  . $url . "', '"
                  . $thumbnail . "', '"
                  . $type . "', '"
                  . $status . "', '"
                  . $reviewer . "', '"
                  . $deliver . "', '"
                  . $create_at . "', '"
                  . $delete
                  . "') ";

$mysql->runSql($sql);
if ($mysql->errno() != 0) {
    die("{\"s_status\": \"ERROR\",\"s_message\": \"" . $mysql->errmsg() . "\",\"s_code\": 1}");
} else {
    die("{\"s_status\": \"OK\",\"s_message\": \"\",\"s_code\": 200}");
}

$mysql->closeDb();
?>
