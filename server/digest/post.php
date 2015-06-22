<?php
$title = $_POST['title'];
$abstract = $_POST['abstract'];
$content = $_POST["content"];
$thumbnail = $_POST['thumbnail'];
$more = $_POST["more"];
$url = $_POST["url"];
$type = $_POST["type"];

if (empty($title) && empty($content) && empty($body)) {
    die("Error: Empty data.");
}

$mysql = new SaeMysql();

$sql = "INSERT  INTO `android_digest`( `title`, `abstract`, `content`, `thumbnail`, `more`, `url`, `type`) "
	. "VALUES ('" . $title . "', '". $abstract . "', '" . $content . "', '" . $thumbnail . "', '" . $more . "', '" . $url ."', '" . $type . "') ";

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
