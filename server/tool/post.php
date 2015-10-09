<?php
$title = $_POST['title'];
$description = $_POST['description'];
$thumbnail = $_POST['thumbnail'];
$homepage = $_POST["homepage"];
$apk = $_POST["apk"];
$url = $_POST["url"];
$type = $_POST["type"];

if (empty($title) && empty($description)) {
    die("Error: Empty data.");
}

$mysql = new SaeMysql();

$sql = "INSERT  INTO `android_tool`( `title`, `description`, `thumbnail`, `homepage`, `apk`, `url`, `type`) "
	. "VALUES ('" . $title . "', '". $description . "', '" . $thumbnail . "', '" . $homepage . "', '" . $apk . "', '" . $url . "', '" . $type . "') ";

echo $sql;

$mysql->runSql($sql);
if ($mysql->errno() != 0)
{
    die("Error:" . $mysql->errmsg());
} else {
	echo "插入成功";
}

$mysql->closeDb();
?>
